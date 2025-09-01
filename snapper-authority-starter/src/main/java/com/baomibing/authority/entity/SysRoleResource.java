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
 * 角色资源关系 - 每个组织拥有一套资源权限
 * 
 * @author zening
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_role_resource")
public class SysRoleResource extends MBaseModel {
	private static final long serialVersionUID = 6189237982069759070L;

	@TableId(type = IdType.ASSIGN_ID)
	private String id;
	@TableField(value = "role_id")
	private String roleId;
	@TableField(value = "resource_id")
	private String resourceId;
	@TableField(value = "resource_type")
	private String resourceType;
	
}
