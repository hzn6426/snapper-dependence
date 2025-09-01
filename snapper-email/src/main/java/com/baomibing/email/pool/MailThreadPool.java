/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.email.pool;

import com.baomibing.email.listener.EmailAttchmentDownloadListener;
import com.baomibing.email.listener.EmailSenderListener;
import jodd.mail.Email;
import jodd.mail.EmailAttachment;
import jodd.mail.ReceivedEmail;
import jodd.mail.SmtpServer;

import javax.activation.DataSource;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MailThreadPool {

	private static final int DEFAULT_QUEUE_SIZE = 50;
	private static final int DEFAULT_CORE_SIZE = 30;
	private static final int DEFAULT_MAX_SIZE = 100;
	private static final int DEFAULT_KEEP_LIVE = 60;

	private static final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(DEFAULT_QUEUE_SIZE);
	private static final MailThreadFactory threadFactory = new MailThreadFactory();
	private static final MailRejectExecutionHandler rejectHandler = new MailRejectExecutionHandler();
	private static final ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(DEFAULT_CORE_SIZE, DEFAULT_MAX_SIZE, DEFAULT_KEEP_LIVE, TimeUnit.SECONDS, queue, threadFactory, rejectHandler);

	public void newAttachmentDownLoadThread(ReceivedEmail email, String downLoadPath, EmailAttachment<? extends DataSource> attchment, EmailAttchmentDownloadListener listener) {
		EmailAttchmentDownLoader downLoader = new EmailAttchmentDownLoader(email, downLoadPath, attchment, listener);
		poolExecutor.submit(downLoader);
	}
	
	public void newMailSenderThread(SmtpServer smtpServer, Email email, EmailSenderListener listener) {
		EmailSender sender = new EmailSender(smtpServer, email, listener);
		poolExecutor.submit(sender);
		
	}
	
}
