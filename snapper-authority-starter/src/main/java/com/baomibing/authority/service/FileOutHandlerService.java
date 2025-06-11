
package com.baomibing.authority.service;



import com.baomibing.authority.dto.UserDto;

import java.util.List;

/**
 * 输入输出服务，包含对外发送邮件，对外上传FTP等业务
 * @author zening
 * @date 2021年3月26日 上午9:15:51
 * @version 1.0.0
 */
public interface FileOutHandlerService {

	/**
	 * 创建用户发送密码到邮箱
	 * @param users
	 */
	void sendUserCreatedEmail(List<UserDto> users);
	
	/**
	 * 重置密码发送密码到邮箱
	 * @param users
	 */
	void sendUserRestPasswdEmail(List<UserDto> users);

	/**
	 * 发送测试邮件校验用户提交的用户及信息正确
	 * @param emailAddress 邮件地址
	 * @param emailPwd 邮件授权码
	 * @param emailHost 邮件服务器
	 * @param emailProtocol 邮件协议
	 */
	void sendUserValidateEmail(String emailAddress, String emailPwd, String emailHost, String emailProtocol);
}
