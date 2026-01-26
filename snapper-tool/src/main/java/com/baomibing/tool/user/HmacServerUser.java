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

@Data
public class HmacServerUser {

	/**
	 * ID
	 */
	private String id;
	/**
	 * 外部系统名称
	 */
	private String systemName;
	/**
	 * 加密KEY
	 */
	private String appKey;
	/**
	 * 过期时间
	 */
	private String expireDate;
	/**
	 * 颁发的系统ID
	 */
	private String appId;
	/**
	 * 对应的用户id
	 */
	private String userId;

	/**
	 * 组织ID
	 */
	private String orgId;
	/**
	 * 白名单ip列表
	 */
	private String whiteIps;
	/**
	 * 状态
	 */
	private String state;
	/**
	 * 绑定类型
	 */
	private String bindType;
	/**
	 * 业务ID
	 */
	private String businessId;
	/**
	 * 用户编码
	 */
	private String userNo;
	/**
	 * 用户真实姓名
	 */
	private String userRealCnName;
	/**
	 * 用户真实英文名
	 */
	private String userRealEnName;
	/**
	 * 角色ID列表以逗号间隔
	 */
	private String roleIds;

	/**
	 * 部门名称
	 */
	private String orgName;

	/**
	 * 用户TAG
	 */
	private String userTag;

	/**
	 * 分公司ID
	 */
	private String groupId;

	/**
	 * 分公司名称
	 */
	private String groupName;

	/**
	 * 职位ID
	 */
	private String positionId;

	/**
	 * 租户ID
	 */
	private String tenantId;

	/**
	 * 租户名称
	 */
	private String tenantName;
}
