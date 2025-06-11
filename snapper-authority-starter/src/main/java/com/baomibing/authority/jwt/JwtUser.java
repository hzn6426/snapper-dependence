/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.jwt;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 系统封装Jwt用户信息
 * 
 * @author zening
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public class JwtUser implements Serializable {
	private static final long serialVersionUID = -7985514181420811550L;

	private String userId;

	private String userNo;

	private String groupId;

	private String groupName;

	private String companyId;

	private String companyName;

	private String positionId;

	private String userRealCnName;

	private String userRealEnName;

	private String userTag;

	private String userEmail;

	private String systemTag;

	private String departmentId;

	//多个角色ID用","分隔
	private String roles;
	
	
	
}
