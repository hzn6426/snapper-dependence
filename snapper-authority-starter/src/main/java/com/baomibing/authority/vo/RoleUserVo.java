/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
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
 * @since 1.0.0
 */
@Data
public class RoleUserVo {
	private String roleId;
	private List<GroupUserVo> groupUsers;

}

