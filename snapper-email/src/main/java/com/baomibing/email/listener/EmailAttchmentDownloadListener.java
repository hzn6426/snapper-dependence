/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.email.listener;

import jodd.mail.MailException;
import jodd.mail.ReceivedEmail;

import java.io.File;

public interface EmailAttchmentDownloadListener {

	void onComplete(ReceivedEmail mail, File file);
	
	void onException(ReceivedEmail mail, MailException exception);
	
	void process(String fileName, long current, long total);
}
