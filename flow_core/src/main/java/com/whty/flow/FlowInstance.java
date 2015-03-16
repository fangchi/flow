package com.whty.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.whty.flow.err.ExecutionError;
import com.whty.flow.err.LogicViolationError;

/**
 * 流程实例对象，通过EasyFlow的instance方法进行实例化
 * 
 * @param <C>
 */
public class FlowInstance<C extends StatefulContext> {

	static Logger log = LoggerFactory.getLogger(FlowInstance.class);

	/* 隶属flow */
	private EasyFlow<C> flow;

	/* log标记位 */
	private boolean trace = false;

	/* 富流程实例 */
	FlowInstance<C> parent = null;

	/**
	 * 初始化
	 * 
	 * @param flow
	 *            流程模板
	 */
	public FlowInstance(EasyFlow<C> flow) {
		this.flow = flow;
	}

	public EasyFlow<C> getFlow() {
		return flow;
	}

	// //getters and setters start
	public FlowInstance<C> getParent() {
		return parent;
	}

	public void setParent(FlowInstance<C> parent) {
		this.parent = parent;
	}

	/**
	 * 是否显示log
	 * 
	 * @return
	 */
	public FlowInstance<C> trace() {
		trace = true;
		return (FlowInstance<C>) this;
	}

	protected boolean isTrace() {
		return trace;
	}

	// //getters and setters end

	/**
	 * 开始执行
	 * 
	 * @param context
	 */
	public void start(C context) {
		start(false, context);
	}

	/**
	 * 开始执行
	 * 
	 * @param enterInitialState
	 *            是否输入开始state
	 * @param context
	 */
	public void start(boolean enterInitialState, final C context) {
		// 设置context流程模板至
		context.setFlow(this.flow);
		// 设置context流程实例
		context.setFlowInstance(this);
		// 设置当前状态
		if (context.getState() == null) { // 没有start state 情况
			setCurrentState(this.flow.getStartState(), false, context);
		} else if (enterInitialState) {
			setCurrentState(context.getState(), true, context);
		}
	}

	/**
	 * 继续执行
	 * 
	 * @param context
	 *            流程上下文
	 */
	@SuppressWarnings("unchecked")
	public void toContinue(C context) {
		// 如果是父流程
		if (context.getFlowInstance().getFlow().equals(this.getFlow())) {
			// 添加继续执行token
			context.addContinueToken();
			// 设置流程
			context.setFlow(this.flow);
			// 进入状态
			enter(context.getState(), context);
		} else {// 如果存在子流程
			context.getFlowInstance().toContinue(context);
			// 设置当前节点子流程为空 ,当前已经为子流程，故不存在子流程 流程引擎仅支持两层
			context.getState().setSubflow(null);
			// 进入状态
			enter(context.getState(), context);
		}
	}

	/**
	 * enter
	 * 
	 * @param state
	 *            目标state
	 * @param context
	 *            流程上下文
	 */
	private void enter(State state, C context) {
		if (context.isTerminated()) {
			return;
		}
		try {
			// first enter state
			if (isTrace())
				log.debug("when enter [{}] for [{}] <<<", state.getName(),
						context);
			// 如果存在子流程 进入子流程程执行
			if (state.getSubflow() != null) {
				if (isTrace())
					log.debug("subflow entered [{}] for [{}] <<<", state
							.getSubflow().getId(), context);
				enterSubFlow(state.getSubflow(), context);
			}

			// 如果是continue 或者是非挂起流程执行handle
			if (context.isContinueToken()
					|| (!context.isContinueToken() && !state.isSuspand())) {
				this.flow.getHandlers().callOnStateEntered(context.getState(),
						context);
			}

			// 如果当前状态为final(没有出口) 并且隶属于子流程
			if (this.flow.getTransitions().isFinal(state)
					&& this.parent != null) {
				if (isTrace())
					log.debug(
							"subflow [{}] is terminated [{}] return to the parent flow[{}] state[{}]",
							this.flow.getId(), context, this.parent.getFlow()
									.getId(), context.getParentState().getId());
				// 离开子流程
				leaveSubFlow(context.getParentState(), context);
			}

			// 如果当前状态为final(没有出口)
			if (this.flow.getTransitions().isFinal(state)
					&& this.parent == null) {
				if (isTrace())
					log.debug("state [{}] in flow [{}] ", state.getId(),
							context, this.flow.getName());
				// 执行terminal方法
				doOnTerminate(state, context);
			}
		} catch (Exception e) {
			doOnError(new ExecutionError(state, null, e,
					"Execution Error in [whenEnter] handler", context));
		}
	}

	/**
	 * 进入子流程
	 * 
	 * @param flow
	 *            流程
	 * @param context
	 *            流程上下文
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void enterSubFlow(EasyFlow flow, C context) {
		// 创建子流程实例
		FlowInstance<C> instance = flow.instance(this);
		// 设置父state为上下文当前state
		context.setParentState(context.getState());
		// 设置子流程开始state为流程当前state
		// ，这里相当于启动一个全新的子流程实例，但是同时同时将流程的当前状态（state）保存在上下文parent中，同时设置新的流程状态
		context.setState(flow.getStartState());
		if (trace)
			instance.trace();
		// 子流程启动
		instance.start(true, context);
	}

	/**
	 * 离开子流程
	 * 
	 * @param flow
	 * @param state
	 *            当前state
	 * @param context
	 */
	private void leaveSubFlow(State state, C context) {
		// 设置流程上下文为当前state
		context.setState(state);
		// 设置父state为空
		context.setParentState(null);
		// 切换流程实例至parent
		context.setFlowInstance(this.getParent());
		// 设置流程模板
		context.setFlow(this.getParent().getFlow());
	}

	/**
	 * leave
	 * 
	 * @param state
	 *            起始state
	 * @param context
	 *            流程上下文
	 */
	private void leave(State state, final C context) {
		if (context.isTerminated()) {
			return;
		}
		try {
			if (isTrace())
				log.debug("when leave [{}] for [{}] <<<", state.getName(),
						context);
			// trigger whenleaved handler
			this.flow.getHandlers().callOnStateLeaved(state, context);
		} catch (Exception e) {
			doOnError(new ExecutionError(state, null, e,
					"Execution Error in [whenLeave] handler", context));
		}
	}

	/**
	 * 设置当前状态
	 * 
	 * @param state
	 *            设置当前state
	 * @param enterInitialState设置起始state
	 * @param context
	 *            流程上下文
	 */
	protected void setCurrentState(State state, boolean enterInitialState,
			C context) {
		// 如果当前离开节点存在，则激发离开事件
		if (!enterInitialState) {
			State prevState = context.getState();
			if (prevState != null) {
				leave(prevState, context);
			}
		}
		// 设置状态
		context.setState(state);
		// 进入
		enter(state, context);
	}

	/**
	 * 终止句柄
	 * 
	 * @param state
	 * @param context
	 */
	protected void doOnTerminate(State state, C context) {
		if (!context.isTerminated()) {
			try {
				if (isTrace())
					log.debug("terminating context [{}] at state [{}]",
							context, context.getState());
				context.setTerminated();
				this.flow.getHandlers().callOnFinalState(state, context);
			} catch (Exception e) {
				log.error("Execution Error in [whenTerminate] handler", e);
			}
		}
	}

	/**
	 * 设置错误
	 * 
	 * @param error
	 */
	@SuppressWarnings("unchecked")
	protected void doOnError(ExecutionError error) {
		this.flow.getHandlers().callOnError(error);
		if (isTrace())
			log.error("the flow [{}] has error [{}] ", this.flow.getId(),
					error.toString());
		doOnTerminate(error.getState(), (C) error.getContext());
	}

	/**
	 * 激发事件
	 * 
	 * @param event
	 *            事件
	 * @param context
	 *            流程上下文
	 */
	public void trigger(Event event, C context) {
		if (context.isTerminated()) {
			return;
		}

		if (isTrace())
			log.debug(
					"begin to trigger event [{}] in state [{}] suspand: [{}] continueToken[{}]",
					event, context.getState(), context.getState().isSuspand(),
					context.isContinueToken());
		// 获取要离开的状态
		State stateFrom = context.getState();
		// 基于事件获取transition
		Optional<Transition> transition = flow.getTransitions().getTransition(
				stateFrom, event);
		try {
			// 流程尚未挂起 或者 有执行token这种在continue的场景中
			if (transition.isPresent()
					&& (!stateFrom.isSuspand() || context
							.consumeContinueToken())) {
				// 获取state from
				State stateTo = transition.get().getStateTo();
				if (isTrace())
					log.debug("when triggered [{}] in [{}] for [{}] <<<",
							event.toString(), stateFrom.toString(), context);
				// 后去对应的event handler
				flow.getHandlers().callOnEventTriggered(event, stateFrom,
						stateTo, context);
				if (isTrace())
					log.debug("when triggered [{}] in [{}] for [{}] >>>",
							event, stateFrom, context);
				// 设置当前状态
				setCurrentState(stateTo, false, context);
			} else if (transition.isPresent() && stateFrom.isSuspand()) { // 入股流程挂起
																			// 且没有continue令牌
				log.debug("the state [{}] is suspaned [{}] <<<",
						stateFrom.toString(), context);
			} else {
				throw new LogicViolationError("Invalid Event: "
						+ event.getVal() + " triggered while in State: "
						+ context.getState() + " in flow " + flow.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (isTrace())
				log.error("the flow [{}] has error [{}] ", flow.getId(),
						e.toString());
			doOnError(new ExecutionError(stateFrom, event, e,
					"Execution Error in [trigger]", context));
		}
	}
}
