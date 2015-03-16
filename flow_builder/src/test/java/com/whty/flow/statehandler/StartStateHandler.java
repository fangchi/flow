package com.whty.flow.statehandler;

import com.whty.flow.impl.context.FlowMapContext;
import com.whty.flow.impl.context.FlowStateHandler;

public class StartStateHandler extends FlowStateHandler{

	@Override
	public String handle(FlowMapContext context) {
		return "3";
	}
}
