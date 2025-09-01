/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.feign.exception;

import lombok.Getter;
import lombok.Setter;

public class FeignRequestException extends RuntimeException {
	
	private static final long serialVersionUID = 4010303731306377127L;
	
	@Setter @Getter
	protected int code = -1;
	@Setter @Getter
	protected String codeMessage;
	
	public FeignRequestException(int code, String message) {
		super(message);
		this.code = code;
		this.codeMessage = message;
	}
	
	public FeignRequestException() {
        super();
    }

    public FeignRequestException(String message) {
        super(message);
    }

    public FeignRequestException(Throwable cause) {
        super(cause);
    }

    public FeignRequestException(String message, Throwable cause) {
        super(message, cause);
    }

}
