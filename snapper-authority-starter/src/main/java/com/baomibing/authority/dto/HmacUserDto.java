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

package com.baomibing.authority.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统hamc用户（用来做外部系统对接）
 * @author zening
 * @version 1.0.0
 */
@JsonInclude(Include.NON_NULL)
@Data @Accessors(chain = true) 
public class HmacUserDto implements Serializable {
	private static final long serialVersionUID = -230362162809285966L;
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

	private String orgId;
    /**
     * 对应的用户id
     */
	private String userId;
	/**
     * 白名单ip列表
     */
	private String whiteIps;
    /**
     * 状态
     */
    private String state;
	/**
	 * 业务ID
	 */
	private String businessId;

	private String tenantId;

	private String tenantName;
	/**
	 * 用户编码
	 */
	private String userNo;

	/**
	 * 组织ID
	 */
	private String groupId;

	/**
	 * 部门名称
	 */
	private String orgName;

	/**
	 * 分公司名称
	 */
	private String groupName;

	/**
	 * 职位ID
	 */
	private String positionId;

	/**
	 * 用户TAG
	 */
	private String userTag;

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

	/***
	 * 绑定类型
	 */
	private String bindType;

	/**
	 * 绑定用户
	 */
	private String bindUser;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 创建人
	 */
	private String createUserCnName;
}
