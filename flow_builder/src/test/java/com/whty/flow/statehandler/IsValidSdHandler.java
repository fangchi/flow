package com.whty.flow.statehandler;

import com.whty.flow.impl.context.FlowMapContext;
import com.whty.flow.impl.context.FlowStateHandler;

public class IsValidSdHandler extends FlowStateHandler{

	@Override
	public String handle(FlowMapContext context) {
		System.out.println("at IsValidSdHandler");
		return "1";
	}
}
