package com.whty.flow.builder.exception;

/**
 * 校验异常
 */
public class ValidateException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ValidateException() {
		super();
	}

	public ValidateException(String message) {
		super(message);
	}

	public ValidateException(String message, Throwable cause) {
		super(message, cause);
	}

	public ValidateException(Throwable cause) {
		super(cause);
	}
}
