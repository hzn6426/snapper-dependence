/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
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
 * 
 * @author zening
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_resource_api")
public class SysResourceApi extends MBaseModel{

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
	
	
	private transient String roleIds;//获取所有资源对应的角色分组列表，用于权限缓存预热
	private transient String beLoginUnauth;
	
}
