package com.baomibing.authority.exception;

/**
 * UserNameNotFoundException
 *
 * @author zening 2024/1/8 16:36
 * @version 1.0.0
 **/
public class UserNameNotFoundException extends RuntimeException {

    public UserNameNotFoundException() {
        super();
    }

    public UserNameNotFoundException(String message) {
        super(message);
    }

    public UserNameNotFoundException(Throwable cause) {
        super(cause);
    }

    public UserNameNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
