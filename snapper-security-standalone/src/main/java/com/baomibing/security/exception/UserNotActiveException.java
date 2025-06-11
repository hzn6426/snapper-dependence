/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.security.exception;

import org.springframework.security.core.AuthenticationException;

public class UserNotActiveException extends AuthenticationException {
	private static final long serialVersionUID = 1L;

	public UserNotActiveException(String msg) {
		super(msg);
	}

	public UserNotActiveException(String msg, Throwable t) {
		super(msg, t);
	}
}
