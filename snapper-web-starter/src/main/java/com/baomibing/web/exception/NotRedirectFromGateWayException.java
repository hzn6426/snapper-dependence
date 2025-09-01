/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.web.exception;

/**
 * 未从网关转发异常
 * 
 * @author zening
 * @since 1.0.0
 */
public class NotRedirectFromGateWayException extends RuntimeException {

	private static final long serialVersionUID = -1L;

	public NotRedirectFromGateWayException() {
		super();
	}

	public NotRedirectFromGateWayException(String message) {
		super(message);
	}

	public NotRedirectFromGateWayException(Throwable cause) {
		super(cause);
	}

	public NotRedirectFromGateWayException(String message, Throwable cause) {
		super(message, cause);
	}
}
