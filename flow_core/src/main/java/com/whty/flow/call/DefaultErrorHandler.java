package com.whty.flow.call;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.whty.flow.StatefulContext;
import com.whty.flow.err.ExecutionError;

/**
 * 核心默认error handler
 */
public class DefaultErrorHandler implements
		ExecutionErrorHandler<StatefulContext> {
	private static Logger log = LoggerFactory
			.getLogger(DefaultErrorHandler.class);

	@Override
	public void call(ExecutionError error, StatefulContext context) {
		String msg = "Execution Error in StateHolder [" + error.getState()
				+ "] ";
		if (error.getEvent() != null) {
			msg += "on EventHolder [" + error.getEvent() + "] ";
		}
		msg += "with Context [" + error.getContext() + "] ";

		Exception e = new Exception(msg, error);
		log.error("Error", e);
	}
}
