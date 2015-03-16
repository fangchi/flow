package com.whty.flow.statehandler;

import com.whty.flow.impl.context.FlowMapContext;
import com.whty.flow.impl.context.FlowStateHandler;

public class GetAppBaseInfoHandler extends FlowStateHandler{

	@Override
	public String handle(FlowMapContext context) {
		context.addParam("data",  "GetAppBaseInfoHandler");
		return "1";
	}
}
