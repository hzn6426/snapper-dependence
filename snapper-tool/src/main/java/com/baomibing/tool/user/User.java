/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.user;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 登录用户
 * @author zening
 * @version 1.0.0
 */
@Data
@Accessors(chain = true)
public class User implements Serializable {
	private static final long serialVersionUID = 813429791993510856L;
	private String id;
	private String userName;
	private String currentGroupId;
	private String currentPositionId;
	private String userCnName;
	private String userEnName;
	private String currentGroupName;
	private String companyId;
	private String companyName;
	private Set<String> roles;
	//	private String roleName;
//	private String roleType;
	private String gateWayTag;
	private String userTag;
	private String token;
	private String params;

	private String systemTag;
	private String outerSystemName;

	private String hmacUserName;

	private String hmacUserCnName;

	private String hmacGroupId;

	private String hmacGroupName;

	private String hmacBusinessId;
	private Map<String, String> paramMap = new HashMap<>();
}
