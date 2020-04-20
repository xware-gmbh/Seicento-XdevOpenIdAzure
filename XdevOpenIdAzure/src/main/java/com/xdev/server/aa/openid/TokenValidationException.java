package com.xdev.server.aa.openid;

public class TokenValidationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1250032900590691118L;

	public TokenValidationException() {
	}

	public TokenValidationException(String message) {
		super(message);
	}

	public TokenValidationException(Throwable cause) {
		super(cause);
	}

	public TokenValidationException(String message, Throwable cause) {
		super(message, cause);
	}

	public TokenValidationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
