/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.gateway.exception;

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