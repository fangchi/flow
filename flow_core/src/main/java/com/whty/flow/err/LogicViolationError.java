package com.whty.flow.err;

/**
 * 流程执行逻辑异常
 */
public class LogicViolationError extends Exception {
	private static final long serialVersionUID = 904045792722645067L;

	public LogicViolationError(String message) {
		super(message);
	}
}
