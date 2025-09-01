/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
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
