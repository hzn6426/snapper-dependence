
package com.baomibing.authority.service.impl;

import com.baomibing.authority.bo.EmailBO;
import com.baomibing.authority.bo.MailServerBo;
import com.baomibing.authority.common.EmailMaker;
import com.baomibing.authority.constant.ParamConst;
import com.baomibing.authority.dto.ParamDto;
import com.baomibing.authority.dto.UserDto;
import com.baomibing.authority.service.FileOutBusinessService;
import com.baomibing.authority.service.FileOutHandlerService;
import com.baomibing.authority.service.SysParamService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.ContextServiceImpl;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FileOutHandlerServiceImpl extends ContextServiceImpl implements FileOutHandlerService {

	@Autowired
	private FileOutBusinessService outBusinessService;
	@Autowired
	private SysParamService paramService;

	private MailServerBo getEmailServer() {
		List<ParamDto> params = paramService.listByCodes(Lists.newArrayList(ParamConst.EMAIL_USER_NAME, ParamConst.EMAIL_USER_PAWD,
				ParamConst.SMTP_HOST, ParamConst.EMAIL_TITILE_NAME, ParamConst.SMTP_PROTOCOL,
				ParamConst.EMAIL_TITILE_PREFIX, ParamConst.WEBSITE_URL));
		Map<String, String> codeValueMap = params.stream().collect(Collectors.toMap(ParamDto::getParamCode, ParamDto::getParamValue, (v1, v2) -> v1));
		MailServerBo server = new MailServerBo();
		server.setFromTitle(codeValueMap.get(ParamConst.EMAIL_TITILE_NAME)).setHost(codeValueMap.get(ParamConst.SMTP_HOST))
				.setAddress(codeValueMap.get(ParamConst.EMAIL_USER_NAME)).setPasswd(codeValueMap.get(ParamConst.EMAIL_USER_PAWD))
				.setProtocol(codeValueMap.get(ParamConst.SMTP_PROTOCOL)).setTitlePrefix(codeValueMap.get(ParamConst.EMAIL_TITILE_PREFIX))
				.setWebsiteUrl(codeValueMap.get(ParamConst.WEBSITE_URL));
		return server;
	}

	@Async
	@Override
	public void sendUserCreatedEmail(List<UserDto> users) {
		MailServerBo server = getEmailServer();
		for (UserDto user : users) {
			Assert.CheckArgument(user);
			Assert.CheckArgument(user.getUserEmail());
			Assert.CheckArgument(user.getUnencryptPassword());
			user.setTitilePrefix(server.getTitlePrefix()).setWebsiteUrl(server.getWebsiteUrl());
			EmailBO email = EmailMaker.makeUserActiveEmail(user);
			outBusinessService.sendUserCreatedEmail(Lists.newArrayList(email), server);
		}
	}

	@Override
	public void sendUserRestPasswdEmail(List<UserDto> users) {
		MailServerBo server = getEmailServer();
		for (UserDto user : users) {
			Assert.CheckArgument(user);
			Assert.CheckArgument(user.getUserEmail());
			Assert.CheckArgument(user.getUnencryptPassword());
			user.setTitilePrefix(server.getTitlePrefix()).setWebsiteUrl(server.getWebsiteUrl());
			EmailBO email = EmailMaker.makeUserResetPasswdEmail(user);
			outBusinessService.sendUserRestPasswdEmail(Lists.newArrayList(email), server);
		}
	}


	@Override
	public void sendUserValidateEmail(String emailAddress, String emailPwd, String emailHost, String emailProtocol) {
		List<ParamDto> params = paramService.listByCodes(Lists.newArrayList(ParamConst.EMAIL_TITILE_PREFIX, ParamConst.WEBSITE_URL));
		Map<String, String> codeValueMap = params.stream().collect(Collectors.toMap(ParamDto::getParamCode, ParamDto::getParamValue, (v1, v2) -> v1));
		Assert.CheckArgument(emailAddress, emailPwd, emailHost, emailProtocol);
		MailServerBo server = new MailServerBo();
		server.setAddress(emailAddress).setHost(emailHost).setPasswd(emailPwd).setProtocol(emailProtocol);
		EmailBO email = EmailMaker.makeUserValidateEmail(emailAddress, codeValueMap.get(ParamConst.EMAIL_TITILE_PREFIX), codeValueMap.get(ParamConst.WEBSITE_URL));
		outBusinessService.sendUserValidateEmail(Lists.newArrayList(email), server);
	}
}
