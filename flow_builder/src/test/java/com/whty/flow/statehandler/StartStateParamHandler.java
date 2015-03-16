package com.whty.flow.statehandler;

import com.whty.flow.impl.context.FlowMapContext;
import com.whty.flow.impl.context.FlowStateHandler;

public class StartStateParamHandler extends FlowStateHandler{

	@Override
	public String handle(FlowMapContext context) {
		if(!context.getState().getParam("code").equals("start_state_code")){
			throw new RuntimeException("start_state_code");
		}
		context.addParam("data", "GetAppBaseInfoHandler");
		return "3";
	}
}
