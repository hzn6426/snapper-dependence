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
 * 职位列表保存职位角色(多个)
 * 
 * @author zening
 * @since 1.0.0
 */
@Data
public class PositionRoleVo {

	private String positionId;

	private List<String> roleIds;
}
