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
 * 角色列表保存角色用户(多个)
 * 
 * @author zening
 * @date May 21, 2021 2:38:39 PM
 * @version 1.0.0
 */
@Data
public class UsetUserVo {


	private String usetId;
	
	private List<GroupUserVo> groupUsers;

//	private String orgId;
//	private List<String> userIds;
}

