
package com.baomibing.authority.service;



import com.baomibing.authority.bo.EmailBO;
import com.baomibing.authority.bo.MailServerBo;

import java.util.List;

/**
 * 业务输出服务
 *
 * @author zening
 * @date Mar 29, 2021 1:51:44 PM
 * @version 1.0.0
 */
public interface FileOutBusinessService {

	/**
	 * 发送用户创建的邮件（账号密码）
	 * @param emails 待发送的邮件信息
	 * @param server
	 */
	void sendUserCreatedEmail(List<EmailBO> emails, MailServerBo server);
	
	/**
	 * 发送用户重置的邮件
	 * @param emails 待发送的邮件信息
	 * @param server
	 */
	void sendUserRestPasswdEmail(List<EmailBO> emails, MailServerBo server);

	/**
	 * 发送用户验证的邮件
	 * @param emails 待发送的邮件信息
	 * @param server
	 */
	void sendUserValidateEmail(List<EmailBO> emails, MailServerBo server);
}
