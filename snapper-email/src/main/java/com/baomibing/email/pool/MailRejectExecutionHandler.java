/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.email.pool;

import com.baomibing.email.exception.MailPoolOutException;
import jodd.mail.ReceivedEmail;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class MailRejectExecutionHandler implements RejectedExecutionHandler {

	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		EmailAttchmentDownLoader downloader = (EmailAttchmentDownLoader) r;
		ReceivedEmail email = downloader.getEmail();
		throw new MailPoolOutException(email, "mail thread pool out exception.");
	}

}
