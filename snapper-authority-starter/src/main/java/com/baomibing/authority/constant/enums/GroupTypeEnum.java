/*
 * Copyright (c) 2020-2025, zening (316279828@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
