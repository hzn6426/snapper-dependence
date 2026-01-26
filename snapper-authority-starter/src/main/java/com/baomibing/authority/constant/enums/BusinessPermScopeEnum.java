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
