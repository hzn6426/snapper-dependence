/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.email.pool;

import com.baomibing.email.listener.EmailAttchmentDownloadListener;
import jodd.mail.EmailAttachment;
import jodd.mail.MailException;
import jodd.mail.ReceivedEmail;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.activation.DataSource;
import java.io.*;

public class EmailAttchmentDownLoader implements Runnable {
	private static final int ZERO = 0;
	
//	private String fileName;
	@Getter
	private ReceivedEmail email;
	private String downLoadPath;
	private EmailAttchmentDownloadListener listener;
	private EmailAttachment<? extends DataSource> attchment;
	public EmailAttchmentDownLoader(ReceivedEmail mail, String downloadPath, EmailAttachment<? extends DataSource> attchment, EmailAttchmentDownloadListener listener) {
		if (mail == null) {
			throw new MailException("Receive mail can not be null.");
		}
		if (StringUtils.isEmpty(downloadPath)) {
			throw new MailException("download path can not be null.");
		}
		if (attchment == null) {
			throw new MailException("mail attchment can not be null.");
		}
		this.email = mail;
		this.downLoadPath = downloadPath;
		this.attchment = attchment;
//		this.fileName = attchment.getName();
		this.listener = listener;
	}
	
	@Override
	public void run() {
		String fileName =  attchment.getName();
		String separator = "";
		if (!downLoadPath.endsWith(File.separator)) {
			separator = File.separator;
		}
		File downLoadFile = new File(downLoadPath + separator + fileName);
		try (InputStream input = attchment.getDataSource().getInputStream();
			
			OutputStream output = new FileOutputStream(downLoadFile);
			BufferedOutputStream bos = new BufferedOutputStream(output)){
			int totalSize = input.available();
			int numToRead = 16384;
			byte[] buffer = new byte[numToRead];

			int totalRead = ZERO;
			int read;

			while ((read = input.read(buffer, ZERO, numToRead)) >= ZERO) {
				bos.write(buffer, ZERO, read);
				totalRead = totalRead + read;
				if (listener != null) {
					listener.process(fileName, totalRead, totalSize);
				}
			}
			output.flush();
			if (listener != null) {
				listener.onComplete(email, downLoadFile);
			}
		} catch (IOException e) {
			MailException mex = new MailException("mail attchment download fail.", e.getCause());
			if (listener != null) {
				listener.onException(email, mex);
			} else {
				throw mex;
			}
		}
		
		
	}
}
