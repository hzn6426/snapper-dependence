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
package com.baomibing.security.jwt;

import com.baomibing.authority.constant.enums.TokenExpirePolicyEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * 自定义用户封装,封装用户token的额外信息
 * 
 * @author zening
 * @since 1.0.0
 */
@Setter @Getter
@Accessors(chain = true)
public class SecurityUser extends User {
	private static final long serialVersionUID = -8040281214999978053L;
	private String userId;
	private String groupId;
	private String positionId;
	private String userCnName;
	private String userEnName;
	private String groupName;
	private String companyId;
	private String companyName;
	private String userEmail;
	private String userEmailPwd;
	private String userEmailHost;
	private String userEmailProtocol;
	private String userTag;
	private boolean beMultiLogin = Boolean.FALSE;
	private boolean beNeed2ChangeDepartment = Boolean.FALSE;
	private String expirePolicy = TokenExpirePolicyEnum.FIXED.name();
	public SecurityUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
	}
	
	

}
