/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.email.pool;

import com.baomibing.email.listener.EmailSenderListener;
import com.google.common.base.Joiner;
import jodd.mail.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;

import javax.activation.DataSource;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EmailDownloadAndSender implements Runnable {

	private Email email;
	private EmailSenderListener listener;
	private SmtpServer smtpServer;
	private String downloadDir;
	public EmailDownloadAndSender(SmtpServer smtpServer, Email email, String downloadDir, EmailSenderListener listener) {
		Validate.notNull(smtpServer);
		Validate.notNull(email);
		Validate.notBlank(downloadDir);
		this.smtpServer = smtpServer;
		this.email = email;
		this.downloadDir = downloadDir;
		this.listener = listener;
	}
	
	private String downloadAttchments(EmailAttachment<? extends DataSource> attchment) {
		int zero = 0;
		String fileName = System.currentTimeMillis() + attchment.getName();
		String filePath = downloadDir + File.separator + fileName;
		File downLoadFile = new File(filePath);
		try (InputStream input = attchment.getDataSource().getInputStream();
			
			OutputStream output = new FileOutputStream(downLoadFile);
			BufferedOutputStream bos = new BufferedOutputStream(output)){
			int numToRead = 16384;
			byte[] buffer = new byte[numToRead];

			int totalRead = zero;
			int read;

			while ((read = input.read(buffer, zero, numToRead)) >= zero) {
				bos.write(buffer, zero, read);
				totalRead = totalRead + read;
			}
			output.flush();
			return filePath;
		} catch (IOException e) {
			MailException mex = new MailException("mail attchment download fail.", e.getCause());
			throw mex;
		}
	}
	@Override
	public void run() {
		List<String> pathList = new ArrayList<>();
		List<EmailAttachment<? extends DataSource>> attchmentList = email.attachments();
		if (CollectionUtils.isNotEmpty(attchmentList)) {
			for (EmailAttachment<? extends DataSource> attchment : attchmentList) {
				String filePath = downloadAttchments(attchment);
				pathList.add(filePath);
			}
		}
		
		try (SendMailSession session = smtpServer.createSession();){
		    session.open();
		    session.sendMail(email);
		    if (listener != null) {
		    	listener.onComplete(email, pathList.isEmpty() ? "" : Joiner.on(";").join(pathList));
		    }
		}catch (MailException exception) {
	    	exception.printStackTrace();
	    	if (listener != null) {
	    		listener.onException(email, pathList.isEmpty() ? "" : Joiner.on(";").join(pathList), exception);
	    	}
	    }
	}
}