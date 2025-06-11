/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.constant.enums;

import lombok.Getter;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * 组织的类型
 * 
 * @author zening
 * @since 1.0.0
 */
public enum GroupTypeEnum {
	//顶层, 平台，集团，公司，部门，团队
	ROOT(1), PLATFORM(2), GROUP(3), COMPANY(4),  DEPARTMENT(5), TEAM(6);
	@Getter
	private int level;
	
	GroupTypeEnum(int level) {
		this.level = level;
	}
	//查找下级
	public GroupTypeEnum nextLevel() {
		int l = this.getLevel() + 1;
		Optional<GroupTypeEnum> optional = Stream.of(GroupTypeEnum.values()).filter(gt -> gt.level == l).findFirst();
		return optional.get();
	}
}
