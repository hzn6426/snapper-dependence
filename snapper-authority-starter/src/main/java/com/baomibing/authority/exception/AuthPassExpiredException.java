/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.exception;

/**
 * 授权码已过期异常
 * @author zening
 * @date May 11, 2019 8:04:24 PM
 * @version 1.0.0
 */
public class AuthPassExpiredException extends RuntimeException {


	private static final long serialVersionUID = 1979021093622903592L;

	public AuthPassExpiredException() {
        super();
    }

    public AuthPassExpiredException(String message) {
        super(message);
    }

    public AuthPassExpiredException(Throwable cause) {
        super(cause);
    }

    public AuthPassExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
