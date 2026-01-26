
/*
 * Copyright (c) 2020-2025, zening (316279828@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

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
