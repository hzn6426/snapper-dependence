/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.security.exception;

import org.springframework.security.core.AuthenticationException;

public class NotSupportPointException extends AuthenticationException {
	private static final long serialVersionUID = 1L;

	public NotSupportPointException(String msg) {
		super(msg);
	}

	public NotSupportPointException(String msg, Throwable t) {
		super(msg, t);
	}
}
