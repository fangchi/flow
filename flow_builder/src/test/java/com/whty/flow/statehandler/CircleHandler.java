package com.whty.flow.statehandler;

import com.whty.flow.impl.context.FlowMapContext;
import com.whty.flow.impl.context.FlowStateHandler;

public class CircleHandler extends FlowStateHandler {

	static int times = 1;

	@Override
	public String handle(FlowMapContext context) {
		times++;
		if (times > 5) {
			return "1";
		} else {
			return "3";
		}
	}
}
