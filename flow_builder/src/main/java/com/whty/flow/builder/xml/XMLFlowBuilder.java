package com.whty.flow.builder.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.whty.flow.EasyFlow;
import com.whty.flow.Event;
import com.whty.flow.FlowBuilder;
import com.whty.flow.State;
import com.whty.flow.Transition;
import com.whty.flow.builder.exception.BuildException;
import com.whty.flow.call.ContextHandler;
import com.whty.flow.call.ExecutionErrorHandler;
import com.whty.flow.container.FlowContainer;
import com.whty.flow.impl.context.FlowMapContext;
import com.whty.flow.impl.context.FlowStateHandler;
import com.whty.util.classname.Resources;
import com.whty.util.xml.parsing.XNode;
import com.whty.util.xml.parsing.XPathParser;

/**
 * XML流程构建类
 */
public class XMLFlowBuilder {

	private static Logger logger = (Logger) LoggerFactory
			.getLogger(XMLFlowBuilder.class);

	/**
	 * 基于文件构建流程模板
	 * 
	 * @param targetFile
	 * @throws BuildException
	 */
	public void build(String targetFile) throws BuildException {
		try {
			logger.info("begin to build flowtemplets from file  [{}]",
					targetFile);
			XPathParser parser = new XPathParser(
					Resources.getResourceAsReader(targetFile), true, null,
					new XMLMapperEntityResolver());
			parseFlowFile(parser.evalNode("flowtemplets"));
			logger.info("build complete");
		} catch (IOException e) {
			throw new BuildException("构建flowtemplets异常 找不到资源文件：" + targetFile,
					e);
		}
	}

	/**
	 * 解析流程模板文件
	 * 
	 * @param root
	 *            根节点
	 * @throws BuildException
	 */
	private void parseFlowFile(XNode root) throws BuildException {
		List<XNode> nodes = root.getChildren();
		for (Iterator<XNode> iterator = nodes.iterator(); iterator.hasNext();) {
			XNode xNode = (XNode) iterator.next();
			if ("subflows".equals(xNode.getName())) {
				for (Iterator<XNode> iterator2 = xNode.getChildren().iterator(); iterator2
						.hasNext();) {
					XNode flowNode = (XNode) iterator2.next();
					logger.info("begin to parse subflow from file  [{}]",
							flowNode.getStringAttribute("src"));
					buildFlow(flowNode.getStringAttribute("src"));
				}
			} else if ("flows".equals(xNode.getName())) {
				for (Iterator<XNode> iterator2 = xNode.getChildren().iterator(); iterator2
						.hasNext();) {
					XNode flowNode = (XNode) iterator2.next();
					logger.info("begin to parse flow from file  [{}]",
							flowNode.getStringAttribute("src"));
					buildFlow(flowNode.getStringAttribute("src"));
				}
			} else {
				logger.error("the subnode contains [{}]", xNode.getName());
				throw new BuildException("the subnode contains "
						+ xNode.getName());
			}
		}
	}

	/**
	 * 基于单个文件构建流程
	 * 
	 * @param filePath
	 *            文件路径
	 * @return
	 * @throws BuildException
	 */
	public EasyFlow<FlowMapContext> buildFlow(String filePath)
			throws BuildException {
		try {
			logger.info("begin to build Flow from file  [{}]", filePath);
			XPathParser parser = new XPathParser(
					Resources.getResourceAsReader(filePath), true, null,
					new XMLMapperEntityResolver());
			return parseFlow(filePath, parser.evalNode("flow"));
		} catch (IOException e) {
			throw new BuildException("构建flow异常 找不到资源文件：" + filePath, e);
		}
	}

	/**
	 * 解析根节点
	 * 
	 * @param filePath
	 *            文件路径
	 * @param root流程模板
	 *            根节点
	 * @return
	 * @throws BuildException
	 */
	private EasyFlow<FlowMapContext> parseFlow(String filePath, XNode root)
			throws BuildException {
		// 获取流程id
		String flow_id = root.getStringAttribute("id");
		if (FlowContainer.getFlow(flow_id) != null) {
			logger.error("the flow with id [{}] has already build ", flow_id);
			throw new BuildException("the flow with id [" + flow_id
					+ "] has already build ");
		}
		if (StringUtils.isEmpty(filePath)) {
			logger.error("the flow id is an empty string in file  [{}]",
					filePath);
			throw new BuildException(
					"the flow id is an empty string in file  [" + filePath
							+ "] ");
		}
		String name = root.getStringAttribute("name");
		String defaultStateHandlerClassName = root
				.getStringAttribute("handler");
		String executionErrorHandlerClassName = root.getStringAttribute(
				"error_handler",
				"com.whty.flow.impl.context.DefaultExecutionErrorHandler");

		FlowBuilder<FlowMapContext> flowBuilder = new FlowBuilder<FlowMapContext>();
		// 获取start-state id和name
		String start_state_id = root.getChildren().get(0)
				.getStringAttribute("id");
		String start_state_name = root.getChildren().get(0)
				.getStringAttribute("name");
		logger.info(
				"init flow id: [{}] name: [{}] state_id: [{}],state_name: [{}]",
				flow_id, name, start_state_id, start_state_name);
		// 初始化流程模板
		EasyFlow<FlowMapContext> flow = flowBuilder
				.from(flow_id, name, start_state_id, start_state_name,
						new HashMap<String, Object>());
		logger.info("begin to parse all states");
		// 解析所有states 并按照Map方式存储
		Map<String, State> allStates = parseAllStates(flow, root.getChildren());
		logger.info("begin to parse states handler");
		// 构建StateHandler 并按照Map方式存储
		Map<String, Class<? extends FlowStateHandler>> statesHandlerMap = parseStatesHandlers(
				root.getChildren(), defaultStateHandlerClassName);
		logger.info("begin to parse all allTransitions");
		// 解析获取所有Transitions
		Map<String, List<Transition>> allTransitions = parseAllTransitions(
				flowBuilder, flow, root.getChildren(), allStates);
		logger.info("begin to build entire flow");
		// 构建流程
		buildFlow(flowBuilder, flow, allStates, allTransitions, start_state_id);
		logger.info("begin to build handler");
		// 构建stateHandler
		buildHandlers(flow, allStates, statesHandlerMap,
				defaultStateHandlerClassName);
		logger.info("build file [{}] finished", filePath);
		// 构建errorHandler
		buildErrorHandler(flow, executionErrorHandlerClassName);
		// 向flow容器中存放flow
		FlowContainer.setFlow(flow);
		return flow;
	}

	/**
	 * 解析所有state节点
	 * 
	 * @param flow
	 *            流程
	 * @param states_nodes
	 *            节点dom列表
	 * @return 节点Map key: state_id ,val:State
	 */
	private Map<String, State> parseAllStates(EasyFlow<FlowMapContext> flow,
			List<XNode> states_nodes) {
		// 节点Map
		Map<String, State> statesMap = new HashMap<String, State>();
		// 遍历节点DOM
		for (Iterator<XNode> iterator = states_nodes.iterator(); iterator
				.hasNext();) {
			XNode xNode = (XNode) iterator.next();
			// 构建基础属性
			String stateId = xNode.getStringAttribute("id");
			logger.info("begin to parse state [{}]", stateId);
			if (statesMap.containsKey(stateId))
				throw new BuildException(
						"the flow already contains the state :" + stateId);
			if (StringUtils.isEmpty(stateId))
				throw new BuildException(
						"the flow already contains the state with empty id ");
			// 获取参数
			HashMap<String, Object> paramsHashMap = new HashMap<String, Object>();
			for (Iterator<XNode> iterator2 = xNode.getChildren().iterator(); iterator2
					.hasNext();) {
				XNode child = (XNode) iterator2.next();
				if (child.getName().equals("param")) {
					paramsHashMap.put(child.getStringAttribute("name"),
							child.getStringAttribute("value"));
				}
				// 覆盖构建start_state
				if (stateId.equals(flow.getStartState().getId())) {
					flow.getStartState().addParam(
							child.getStringAttribute("name"),
							child.getStringAttribute("value"));
				}
			}

			// 构建StateMap
			statesMap.put(
					stateId,
					new State(xNode.getStringAttribute("id"), xNode
							.getStringAttribute("name"), xNode
							.getBooleanAttribute("suspand", false), xNode
							.getChildren().size() == 0, flow, paramsHashMap));

		}

		return statesMap;
	}

	/**
	 * 构建StateHandlers
	 * 
	 * @param states_nodes
	 *            statesDom列表
	 * @param defaultStateHandlerClassName
	 *            默认HandlerClass
	 * @return state—handlerMap key:state_id ,val:handler class
	 */
	private Map<String, Class<? extends FlowStateHandler>> parseStatesHandlers(
			List<XNode> states_nodes, String defaultStateHandlerClassName) {
		Map<String, Class<? extends FlowStateHandler>> statesHandlerMap = new HashMap<String, Class<? extends FlowStateHandler>>();
		for (Iterator<XNode> iterator = states_nodes.iterator(); iterator
				.hasNext();) {
			XNode xNode = (XNode) iterator.next();
			// 获取基础属性
			String stateId = xNode.getStringAttribute("id");
			String className = xNode.getStringAttribute("handler",
					defaultStateHandlerClassName);
			logger.info("begin to parse state [{}]", stateId);
			try {
				if (!StringUtils.isEmpty(className)) {
					statesHandlerMap.put(stateId, Class.forName(className)
							.asSubclass(FlowStateHandler.class));
				}
			} catch (ClassNotFoundException e) {
				throw new BuildException("can not find class :" + className);
			}
		}
		return statesHandlerMap;
	}

	/**
	 * 解析获取所有Transitions
	 * 
	 * @param flowBuilder
	 *            流程buidler
	 * @param flow
	 *            当前流程末班
	 * @param states_nodes
	 *            states dom几点
	 * @param allStates
	 *            所有stateMap
	 * @return 按照key:state_id ,val:对应state_transtions 列表的map
	 */
	private Map<String, List<Transition>> parseAllTransitions(
			FlowBuilder<FlowMapContext> flowBuilder,
			EasyFlow<FlowMapContext> flow, List<XNode> states_nodes,
			Map<String, State> allStates) {
		// 定义返回数据结构
		Map<String, List<Transition>> statesTransitionMap = new HashMap<String, List<Transition>>();
		// 遍历states节点
		for (Iterator<XNode> iterator = states_nodes.iterator(); iterator
				.hasNext();) {
			XNode stateNode = (XNode) iterator.next();
			// transitions列表
			List<Transition> transitions = new ArrayList<Transition>();
			// events列表
			Set<String> stateTransitionEvents = new HashSet<String>();
			// 构建transitioins
			for (Iterator<XNode> iterator2 = stateNode.getChildren().iterator(); iterator2
					.hasNext();) {
				XNode transitionNode = (XNode) iterator2.next();
				if (transitionNode.getName().equals("transition")) {
					String targetStateID = transitionNode
							.getStringAttribute("to");
					String flowId = transitionNode.getStringAttribute("flow");
					if (allStates.get(targetStateID) == null) {
						throw new BuildException("in flow [" + flow.getId()
								+ "] the target state [" + targetStateID
								+ "] is not exist!");
					}
					State targetState = allStates.get(targetStateID);
					String eventVal = transitionNode
							.getStringAttribute("event");
					// 校验是否存在相同的event
					if (stateTransitionEvents.contains(eventVal)) {
						throw new BuildException("in flow [" + flow.getId()
								+ "] the state ["
								+ stateNode.getStringAttribute("id")
								+ "] transitions has same event [" + eventVal
								+ "] !");
					}
					stateTransitionEvents.add(eventVal);
					logger.info(
							"begin to parse state [{}] event [{}] target_state [{}]",
							stateNode.getStringAttribute("id"), eventVal,
							targetState.getId());
					Transition transition = null;
					// 根据是否有出口transtions 判断是否为finish
					if (targetState.isHasNoTransitions()) {
						transition = flowBuilder.on(new Event(eventVal), flow)
								.finish(targetState);
					} else {
						if (StringUtils.isEmpty(flowId)
								|| flow.getId().equals(flowId)) {
							transition = flowBuilder.on(new Event(eventVal),
									flow).to(targetState);
						} else {// subFlow
							transition = flowBuilder.on(new Event(eventVal),
									flow).to(targetState,
									FlowContainer.getFlow(flowId));
						}
					}
					// 设置起始state
					transition.setStateFrom(allStates.get(stateNode
							.getStringAttribute("id")));
					transitions.add(transition);
				}
			}
			statesTransitionMap.put(stateNode.getStringAttribute("id"),
					transitions);
		}
		return statesTransitionMap;
	}

	/**
	 * 构建flow对象
	 * 
	 * @param flowBuilder
	 * @param flow
	 * @param allStates
	 * @param statesTransitions
	 * @param start_state_id
	 */
	private void buildFlow(FlowBuilder<FlowMapContext> flowBuilder,
			EasyFlow<FlowMapContext> flow, Map<String, State> allStates,
			Map<String, List<Transition>> statesTransitions,
			String start_state_id) {
		// 从start_state开始构建transtions
		List<Transition> transitions = statesTransitions.get(start_state_id);
		// 遍历transition构建子transition
		for (Iterator<Transition> iterator = transitions.iterator(); iterator
				.hasNext();) {
			Transition transition = (Transition) iterator.next();
			// 构建transition
			buildTransitions(transition, statesTransitions);
		}
		// 构建对象
		flowBuilder.transit(flow,
				transitions.toArray(new Transition[transitions.size()]));
	}

	/**
	 * 构建Transition对象
	 * 
	 * @param sourceTransition
	 *            起始trantions
	 * @param statesTransitionMap
	 */
	private void buildTransitions(Transition sourceTransition,
			Map<String, List<Transition>> statesTransitionMap) {
		logger.info("begin to parse transion: event: [{}] target_state: [{}]",
				sourceTransition.getEvent().getVal(), sourceTransition
						.getStateTo().getId());
		List<Transition> transitions = statesTransitionMap.get(sourceTransition
				.getStateTo().getId());
		if (transitions == null || transitions.size() == 0) {
			logger.info("the target state  [{}] has no transitions",
					sourceTransition.getStateTo().getId());
			return;
		} else {
			List<Transition> transitionList = new ArrayList<Transition>();
			for (Iterator<Transition> iterator = transitions.iterator(); iterator
					.hasNext();) {
				Transition transition = (Transition) iterator.next();
				if (transition.equals(sourceTransition)) {
					continue;
				}
				// 递归调用buildTransitions
				buildTransitions(transition, statesTransitionMap);
				// 添加待处理的transition
				transitionList.add(transition);
			}
			// 构建transition
			sourceTransition.transit(transitionList
					.toArray(new Transition[transitionList.size()]));
		}
	}

	/**
	 * 处理handler
	 * 
	 * @param flow
	 *            流程对象
	 * @param allStates
	 *            stateMap
	 * @param statesHandlerMap
	 *            stateHandlerMap
	 * @param stateHandlerClassName
	 *            stateHandler类名
	 */
	private void buildHandlers(
			EasyFlow<FlowMapContext> flow,
			Map<String, State> allStates,
			final Map<String, Class<? extends FlowStateHandler>> statesHandlerMap,
			String stateHandlerClassName) {
		for (Iterator<String> iterator = statesHandlerMap.keySet().iterator(); iterator
				.hasNext();) {
			// 状态编号
			final String stateID = (String) iterator.next();
			// 获取对应State
			State state = allStates.get(stateID);
			if (!state.isHasNoTransitions()) {
				flow.whenEnter(state, new ContextHandler<FlowMapContext>() {
					@Override
					public void call(FlowMapContext context) throws Exception {
						// 获取state
						if (statesHandlerMap.get(stateID) != null) {
							FlowStateHandler handler = statesHandlerMap.get(
									stateID).newInstance();
							context.trigger(new Event(handler.handle(context)));
						}
					}
				});
			}
		}
	}

	/**
	 * 处理ErrorHandler
	 * @param flow
	 * @param executionErrorHandlerClassName
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void buildErrorHandler(EasyFlow<FlowMapContext> flow,
			String executionErrorHandlerClassName) {
		Class<? extends ExecutionErrorHandler> handler;
		try {
			handler = Class.forName(executionErrorHandlerClassName).asSubclass(
					ExecutionErrorHandler.class);
			flow.whenError(handler.newInstance());
		} catch (ClassNotFoundException e) {
			throw new BuildException("can not find class :"
					+ executionErrorHandlerClassName);
		} catch (InstantiationException e) {
			throw new BuildException("can not instantiation class :"
					+ executionErrorHandlerClassName);
		} catch (IllegalAccessException e) {
			throw new BuildException("illegalAccessException class :"
					+ executionErrorHandlerClassName);
		}

	}
}
