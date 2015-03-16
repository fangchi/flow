package com.whty.util.xml.parsing;

/**
 * 解析异常
 * @since 0.0.1
 */
public class ParsingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ParsingException() {
		super();
	}

	public ParsingException(String message) {
		super(message);
	}

	public ParsingException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParsingException(Throwable cause) {
		super(cause);
	}
}
