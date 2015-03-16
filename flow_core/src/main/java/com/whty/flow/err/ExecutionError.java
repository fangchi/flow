package com.whty.flow.err;

import com.whty.flow.Event;
import com.whty.flow.State;
import com.whty.flow.StatefulContext;

/**
 * 流程执行异常，所有流程执行均需要及城管该类别
 */
public class ExecutionError extends Exception {
	private static final long serialVersionUID = 4362053831847081229L;
	private State state;
	private Event event;
	private StatefulContext context;

	public ExecutionError(State state, Event event, Exception error,
			String message, StatefulContext context) {
		super(message, error);
		this.state = state;
		this.event = event;
		this.context = context;
	}

	public State getState() {
		return state;
	}

	public Event getEvent() {
		return event;
	}

	@SuppressWarnings("unchecked")
	public <C extends StatefulContext> C getContext() {
		return (C) context;
	}
}
