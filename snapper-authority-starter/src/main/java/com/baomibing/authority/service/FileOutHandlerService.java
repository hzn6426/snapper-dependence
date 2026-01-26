
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



import com.baomibing.authority.dto.SysTenantUserDto;
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
	 * 创建用户发送密码到邮箱
	 * @param users
	 */
	void sendTenantUserCreatedEmail(List<SysTenantUserDto> users);

	/**
	 * 重置密码发送密码到邮箱
	 * @param users
	 */
	void sendTenantUserRestPasswdEmail(List<SysTenantUserDto> users);

	/**
	 * 发送测试邮件校验用户提交的用户及信息正确
	 * @param emailAddress 邮件地址
	 * @param emailPwd 邮件授权码
	 * @param emailHost 邮件服务器
	 * @param emailProtocol 邮件协议
	 */
	void sendUserValidateEmail(String emailAddress, String emailPwd, String emailHost, String emailProtocol);
}
