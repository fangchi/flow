package com.whty.flow;

import java.util.Map;

/**
 * 流程模板构建器,用于生成流程模板
 * @param <C>
 */
public class FlowBuilder<C extends StatefulContext> {

	/**
	 * from 
	 * @param flow_id 流程编号
	 * @param flow_name 流程名称
	 * @param id 状态ID
	 * @param name 状态名称
	 * @param params 状态参数
	 * @return
	 */
	public EasyFlow<C> from(String flow_id, String flow_name, String id,
			String name, Map<String, Object> params) {
		State startState = new State(id, name, params);
		EasyFlow<C> flow = new EasyFlow<C>(flow_id, flow_name, startState);
		flow.addState(startState);
		return flow;
	}

	/**
	 * 构建事件handler
	 * @param event
	 * @param flow
	 * @return
	 */
	public ToHolder on(Event event, EasyFlow<C> flow) {
		return new ToHolder(event, flow);
	}

	/**
	 * 构建transitions
	 * @param flow
	 * @param transitions
	 * @return
	 */
	public <C1 extends StatefulContext> EasyFlow<C1> transit(EasyFlow<C> flow,
			Transition... transitions) {
		return transit(flow, false, transitions);
	}

	/**
	 * transit 
	 * @param flow 流程
	 * @param skipValidation 跳过验证
	 * @param transitions 子transitions
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <C1 extends StatefulContext> EasyFlow<C1> transit(EasyFlow flow,
			boolean skipValidation, Transition... transitions) {
		for (Transition transition : transitions) {
			transition.setStateFrom(flow.getStartState());
		}
		//处理所有Transitions
		flow.processAllTransitions(skipValidation);
		return flow;
	}
}
