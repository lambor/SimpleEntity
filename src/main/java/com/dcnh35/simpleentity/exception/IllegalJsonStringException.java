package com.dcnh35.simpleentity.exception;

public class IllegalJsonStringException extends Exception {

	@SuppressWarnings("unused")
	public IllegalJsonStringException() {
		super();
	}

	@SuppressWarnings("unused")
	public IllegalJsonStringException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	@SuppressWarnings("unused")
	public IllegalJsonStringException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalJsonStringException(String message) {
		super(message);
	}

	@SuppressWarnings("unused")
	public IllegalJsonStringException(Throwable cause) {
		super(cause);
	}
	
}
