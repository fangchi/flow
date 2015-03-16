package com.whty.flow.container;

import java.util.HashMap;
import java.util.Map;

import com.whty.flow.EasyFlow;
import com.whty.flow.builder.exception.BuildException;
import com.whty.flow.builder.xml.XMLFlowBuilder;
import com.whty.flow.impl.context.FlowMapContext;

/**
 * 流程容器
 */
public class FlowContainer {

	// 容器Map
	private static Map<String, EasyFlow<FlowMapContext>> flowsMap = new HashMap<String, EasyFlow<FlowMapContext>>();
	// 初始化标识符
	private static boolean isInit = false;
	// 文件路径
	private static String filePath;

	// getters and setters
	public static String getFilePath() {
		return filePath;
	}

	public static void setFilePath(String filePath) {
		FlowContainer.filePath = filePath;
	}

	/**
	 * 初始化容器
	 * @throws BuildException
	 */
	public static void init() throws BuildException {
		if (isInit) {
			throw new BuildException("the FLowContainer is already inited !");
		} else {
			new XMLFlowBuilder().build(filePath);
			isInit = true;
		}
	}

	/**
	 * 获取flow
	 * @param flowId
	 * @return
	 */
	public static EasyFlow<FlowMapContext> getFlow(String flowId) {
		return flowsMap.get(flowId);
	}

	/**
	 * 设置flow
	 * @param flow
	 */
	public static void setFlow(EasyFlow<FlowMapContext> flow) {
		flowsMap.put(flow.getId(), flow);
	}

	/**
	 * 清理
	 */
	public static void clear() {
		flowsMap.clear();
	}
}
