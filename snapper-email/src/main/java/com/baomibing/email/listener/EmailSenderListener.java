/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.email.listener;


import jodd.mail.Email;
import jodd.mail.MailException;

public interface EmailSenderListener {
	/**
	 * 发送完后调用
	 * @param mail 发送的邮件
	 * @param filePath 文件下载的路径，多个路径以;间隔
	 */
	void onComplete(Email mail, String filePath);
	/**
	 * 发送异常后调用
	 * @param mail 发送的邮件
	 * @param filePath 文件下载的路径，多个路径以;间隔
	 * @param exception 异常信息
	 */
	void onException(Email mail, String filePath, MailException exception);
	
}
