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

package com.baomibing.authority.entity;

import com.baomibing.orm.base.MBaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 资源API - 所有组织拥有一套资源,只有开发管理员才能配置修改
 * @author zening
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_resource_api_tenant")
public class SysResourceApiTenant extends MBaseModel {
	private static final long serialVersionUID = 8446522238061237291L;
	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private String id;
	@TableField(value = "resource_id")
	private String resourceId;
	@TableField(value = "resource_type")
	private String resourceType;
	@TableField(value = "req_url")
	private String reqUrl;
	@TableField(value = "req_method")
	private String reqMethod;
	@TableField(value = "state")
	private String state;
	private Boolean beCopyOrigin;
	
	
	private transient String roleIds;//获取所有资源对应的角色分组列表，用于权限缓存预热
	
}
