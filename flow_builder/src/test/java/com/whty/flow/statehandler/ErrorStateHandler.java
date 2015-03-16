package com.whty.flow.statehandler;

import com.whty.flow.impl.context.FlowMapContext;
import com.whty.flow.impl.context.FlowStateHandler;

public class ErrorStateHandler extends FlowStateHandler{

	@Override
	public String handle(FlowMapContext context) {
		int i  = 1;
		if(1 == i){
			throw new RuntimeException("some error");
		}
		return "1";
	}
}
