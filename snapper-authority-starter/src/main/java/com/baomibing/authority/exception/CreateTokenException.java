/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.exception;

public class CreateTokenException extends RuntimeException {

	private static final long serialVersionUID = 3620238652090747862L;

	public CreateTokenException() {
        super();
    }

    public CreateTokenException(String message) {
        super(message);
    }

    public CreateTokenException(Throwable cause) {
        super(cause);
    }

    public CreateTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
