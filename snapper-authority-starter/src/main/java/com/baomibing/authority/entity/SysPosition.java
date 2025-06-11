/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
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
 * 职位管理
 * 
 * @author zening
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_position")
public class SysPosition extends MBaseModel {
	private static final long serialVersionUID = -6854274909243324985L;

	@TableId(type = IdType.ASSIGN_ID)
	private String id;
	@TableField(value = "post_name")
	private String postName;
	@TableField(value = "org_id")//所在组织ID
	private String orgId;
	@TableField(value = "perm_scope")
	private String permScope;
	@TableField(value = "state")
	private String state;
	@TableField(value = "note")
	private String note;
	@TableField(value = "group_id")
	private String groupId;
	@TableField(value = "be_manager")
	private Boolean beManager;
	private Boolean beLock;
}
