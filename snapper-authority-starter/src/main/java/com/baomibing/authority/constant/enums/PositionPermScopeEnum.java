/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.constant.enums;

import lombok.Getter;

/**
 * 职位权限范围
 * 
 * @author zening
 * @since 1.0.0
 */
public enum PositionPermScopeEnum {

	CUSTOMER_SPECIFIED("用户自定义"), CURRENT_GROUP("本组织"), UNDERLING_GROUP("下属");
	
	
	@Getter
	private String description;
	
	PositionPermScopeEnum(String desc) {
		this.description = desc;
	}
}
