/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.email.pool;

import com.baomibing.email.listener.EmailSenderListener;
import jodd.mail.Email;
import jodd.mail.MailException;
import jodd.mail.SendMailSession;
import jodd.mail.SmtpServer;
import org.apache.commons.lang3.Validate;
public class EmailSender implements Runnable {

	private final Email email;
	private final EmailSenderListener listener;
	private final SmtpServer smtpServer;
	public EmailSender(SmtpServer smtpServer, Email email, EmailSenderListener listener) {
		Validate.notNull(smtpServer);
		Validate.notNull(email);
		this.smtpServer = smtpServer;
		this.email = email;
		this.listener = listener;
	}
	
	
	@Override
	public void run() {
		try (SendMailSession session = smtpServer.createSession()){
			session.open();
			session.sendMail(email);
		    if (listener != null) {
		    	listener.onComplete(email, "");
		    }
		   
		} catch (MailException exception) {
	    	if (listener != null) {
	    		listener.onException(email, "", exception);
	    	}
	    }
	}
}
