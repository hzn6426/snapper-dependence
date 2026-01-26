
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

package com.baomibing.authority.service.impl;

import com.baomibing.authority.bo.EmailBO;
import com.baomibing.authority.bo.MailServerBo;
import com.baomibing.authority.common.EmailSender;
import com.baomibing.authority.service.FileOutBusinessService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileOutBusinessServiceImpl implements FileOutBusinessService {

	@Override
	public void sendUserCreatedEmail(List<EmailBO> emails, MailServerBo server) {
		EmailSender.send2Email(emails, server);
	}

	@Override
	public void sendUserRestPasswdEmail(List<EmailBO> emails, MailServerBo server) {
		EmailSender.send2Email(emails, server);
	}

	@Override
	public void sendUserValidateEmail(List<EmailBO> emails, MailServerBo server) {
		EmailSender.send2Email(emails, server);
	}
}
