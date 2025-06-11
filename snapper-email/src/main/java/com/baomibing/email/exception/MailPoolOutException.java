/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.email.exception;

import jodd.mail.MailException;
import jodd.mail.ReceivedEmail;
import lombok.Getter;

/**
 * 线程池满抛出异常
 * @author zening
 * @date 2018年7月3日 上午10:17:25
 * @version 1.0.0
 */
public class MailPoolOutException extends MailException {
	private static final long serialVersionUID = -4376054149055861611L;
	@Getter
	private ReceivedEmail email;
	
	public MailPoolOutException(ReceivedEmail mail , final String message) {
		super(message);
		this.email = mail;
	}

	public MailPoolOutException(ReceivedEmail mail, final String message, final Throwable t) {
		super(message, t);
		this.email = mail;
	}

}
