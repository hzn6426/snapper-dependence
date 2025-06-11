/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.exception;

public class CreateRSAKeyException extends RuntimeException {

	private static final long serialVersionUID = 6870764020292291404L;

	public CreateRSAKeyException() {
        super();
    }

    public CreateRSAKeyException(String message) {
        super(message);
    }

    public CreateRSAKeyException(Throwable cause) {
        super(cause);
    }

    public CreateRSAKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
