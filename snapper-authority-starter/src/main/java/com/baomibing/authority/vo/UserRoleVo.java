/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.vo;

import lombok.Data;

import java.util.List;

/**
 * 用户列表保存用户角色(多个)
 * 
 * @author zening
 * @since 1.0.0
 */
@Data
public class UserRoleVo {

	//组织ID
	private String orgId;

	private String userId;
	
	private List<String> roleIds;
}
