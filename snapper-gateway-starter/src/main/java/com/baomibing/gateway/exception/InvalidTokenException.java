/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.gateway.exception;

/**
 * 非法的token异常
 * @author zening
 * @date May 11, 2019 8:04:24 PM
 * @version 1.0.0
 */
public class InvalidTokenException extends RuntimeException {

	
	private static final long serialVersionUID = 1979021093622903592L;

	public InvalidTokenException() {
        super();
    }

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(Throwable cause) {
        super(cause);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
