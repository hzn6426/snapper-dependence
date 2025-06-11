
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
