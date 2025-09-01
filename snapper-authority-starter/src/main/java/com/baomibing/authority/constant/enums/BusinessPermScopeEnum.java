/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.constant.enums;

import lombok.Getter;

/**
 * 业务数据权限范围类型枚举
 * 
 * @author zening
 * @since 1.0.0
 */
public enum BusinessPermScopeEnum {
	//所有，本公司，本部门，本人，指定公司，指定人
	CURRENT_USER("本人"), 
	CURRENT_GROUP("本部门及子部门"),
	CUSTOMER_SPECIFIED("用户自定义"), SCOPE_ALL("全部");
	@Getter
	private String description;
	
	BusinessPermScopeEnum(String desc) {
		this.description = desc;
	}
}
