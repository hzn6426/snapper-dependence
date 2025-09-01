/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.email.listener;

import jodd.mail.MailException;
import jodd.mail.ReceivedEmail;

import java.util.List;



public interface EmailReceiverListener {

	void receiveMessages(List<ReceivedEmail> emails);
	
	void onReceiveException(MailException exception);
}
