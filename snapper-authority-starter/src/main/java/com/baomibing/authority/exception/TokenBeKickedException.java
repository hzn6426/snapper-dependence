package com.baomibing.authority.exception;

public class TokenBeKickedException extends RuntimeException {
	private static final long serialVersionUID = -425880626341094686L;

	public TokenBeKickedException() {
        super();
    }

    public TokenBeKickedException(String message) {
        super(message);
    }

    public TokenBeKickedException(Throwable cause) {
        super(cause);
    }

    public TokenBeKickedException(String message, Throwable cause) {
        super(message, cause);
    }
}