package com.whty.flow.impl.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.whty.flow.call.ExecutionErrorHandler;
import com.whty.flow.err.ExecutionError;

/**
 * 默认执行错误Handler
 */
public class DefaultExecutionErrorHandler implements
		ExecutionErrorHandler<FlowMapContext> {

	private static Logger log = LoggerFactory
			.getLogger(DefaultExecutionErrorHandler.class);

	@Override
	public void call(ExecutionError error, FlowMapContext context) {
		String msg = "Execution Error in StateHolder [" + error.getState()
				+ "] ";
		if (error.getEvent() != null) {
			msg += "on EventHolder [" + error.getEvent() + "] ";
		}
		msg += "with Context [" + error.getContext() + "] ";
		Exception e = new Exception(msg, error);
		log.error("Error", e);
		//向流程上下文中设置error
		context.addParam("error", error);
	}

}
