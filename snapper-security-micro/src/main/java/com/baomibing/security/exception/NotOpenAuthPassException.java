package com.baomibing.security.exception;

/**
 * 授权码已过期异常
 * @author zening
 * @date May 11, 2019 8:04:24 PM
 * @version 1.0.0
 */
public class NotOpenAuthPassException extends RuntimeException {


	private static final long serialVersionUID = 1979021093622903592L;

	public NotOpenAuthPassException() {
        super();
    }

    public NotOpenAuthPassException(String message) {
        super(message);
    }

    public NotOpenAuthPassException(Throwable cause) {
        super(cause);
    }

    public NotOpenAuthPassException(String message, Throwable cause) {
        super(message, cause);
    }
}
