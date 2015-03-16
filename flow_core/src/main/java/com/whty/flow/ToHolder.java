package com.whty.flow;

import java.util.Map;

/**
 * ToHolder bean
 * @author andrey
 */
public class ToHolder {
	private Event event;

	private EasyFlow<? extends StatefulContext> flow;

	public ToHolder(Event event, EasyFlow<? extends StatefulContext> flow) {
		this.event = event;
		this.flow = flow;
	}
	
	public Transition to(State state) {
		state.setFlow(flow);
		flow.addState(state);
		return new Transition(event, state, false);
	}
	
	public Transition to(String id, String name, boolean suspand,
			Map<String, Object> params) {
		State state = new State(id, name, flow, suspand, params);
		flow.addState(state);
		return new Transition(event, state, false);
	}

	public Transition to(String id, String name,Map<String, Object> params) {
		State state = new State(id, name, false,params);
		flow.addState(state);
		return new Transition(event, state, false);
	}

	public Transition to(String id, String name,
			EasyFlow<? extends StatefulContext> subflow,Map<String, Object> params) {
		State state = new State(id, name, flow, subflow, false,params);
		flow.addState(state);
		return new Transition(event, state, false);
	}

	public Transition to(String id, String name, boolean suspanded,
			EasyFlow<? extends StatefulContext> subflow,Map<String, Object> params) {
		State state = new State(id, name, flow, subflow, suspanded,params);
		flow.addState(state);
		return new Transition(event, state, false);
	}
	
	public Transition to(State state,
			EasyFlow<? extends StatefulContext> subflow) {
		state.setFlow(flow);
		state.setSubflow(subflow);
		flow.addState(state);
		return new Transition(event, state, false);
	}

	public Transition finish(String id, String name,Map<String, Object> params) {
		State state = new State(id, name,params);
		state.setFlow(flow);
		flow.addState(state);
		return new Transition(event, state, true);
	}
	
	public Transition finish(State state) {
		state.setFlow(flow);
		flow.addState(state);
		return new Transition(event, state, true);
	}

	// public Transition toFlow(EasyFlow<? extends StatefulContext> subflow) {
	// return new Transition(event, subflow.getStartState(), false);
	// }
}