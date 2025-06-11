/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.exception;

public class TokenTimeOutException extends RuntimeException {

	private static final long serialVersionUID = 1561567706722495521L;

	public TokenTimeOutException() {
        super();
    }

    public TokenTimeOutException(String message) {
        super(message);
    }

    public TokenTimeOutException(Throwable cause) {
        super(cause);
    }

    public TokenTimeOutException(String message, Throwable cause) {
        super(message, cause);
    }
}
