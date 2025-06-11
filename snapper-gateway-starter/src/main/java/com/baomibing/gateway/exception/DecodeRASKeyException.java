/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.gateway.exception;

public class DecodeRASKeyException extends RuntimeException {

	private static final long serialVersionUID = -8494267748678601909L;

	public DecodeRASKeyException() {
        super();
    }

    public DecodeRASKeyException(String message) {
        super(message);
    }

    public DecodeRASKeyException(Throwable cause) {
        super(cause);
    }

    public DecodeRASKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
