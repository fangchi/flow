package com.whty.flow.statehandler;

import com.whty.flow.impl.context.FlowMapContext;
import com.whty.flow.impl.context.FlowStateHandler;

public class IsValidSdParamHandler extends FlowStateHandler{

	@Override
	public String handle(FlowMapContext context) {
		if(!context.getState().getParam("code").equals("IsValidSd_code")){
			throw new RuntimeException("checkIfDownLoad_code");
		}
		return "1";
	}
}
