package com.whty.flow.errorhandler;

import com.whty.flow.err.ExecutionError;
import com.whty.flow.impl.context.DefaultExecutionErrorHandler;
import com.whty.flow.impl.context.FlowMapContext;

public class CustomExecutionErrorHandler extends DefaultExecutionErrorHandler{

	@Override
	public void call(ExecutionError error, FlowMapContext context) {
		context.addParam("error", error);
		context.addParam("error2", error);
	}
}
