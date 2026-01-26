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
