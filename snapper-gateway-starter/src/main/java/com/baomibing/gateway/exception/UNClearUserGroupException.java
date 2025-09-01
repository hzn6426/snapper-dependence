/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.gateway.exception;

public class UNClearUserGroupException extends RuntimeException {
	private static final long serialVersionUID = 3607309666114103152L;

	public UNClearUserGroupException() {
        super();
    }

    public UNClearUserGroupException(String message) {
        super(message);
    }

    public UNClearUserGroupException(Throwable cause) {
        super(cause);
    }

    public UNClearUserGroupException(String message, Throwable cause) {
        super(message, cause);
    }
}
