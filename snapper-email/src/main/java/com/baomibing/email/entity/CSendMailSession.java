/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.email.entity;

import jodd.mail.Email;
import jodd.mail.MailException;
import jodd.mail.SendMailSession;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

public class CSendMailSession extends SendMailSession {

	public CSendMailSession(Session session, Transport transport) {
		super(session, transport);
	}
	
	public String sendMail(final Email email) {
		try {
			final MimeMessage msg = createMessage(email);
//			if(msg.getContent() instanceof Multipart) {
//				Multipart part = (Multipart)msg.getContent();
//				final MimeBodyPart bodyPart = new MimeBodyPart();
//				EmailMessage emailWithData = email.messages().get(0);
//				bodyPart.setContent(emailWithData.getContent(), emailWithData.getMimeType() + ";charset=" + emailWithData.getEncoding());
//				part.addBodyPart(bodyPart);
//			}
			getService().sendMessage(msg, msg.getAllRecipients());
			return msg.getMessageID();
		} catch (final MessagingException  msgexc) {
			throw new MailException("Failed to send email: " + email, msgexc);
		}
	} 

}
