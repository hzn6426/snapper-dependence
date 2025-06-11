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
 * 用户委托组，用来设置数据权限时用户委托的组
 * 
 * @author zening
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_group_entrust")
public class SysGroupEntrust extends MBaseModel {
	private static final long serialVersionUID = -8636165525773027785L;

	@TableId(type = IdType.ASSIGN_ID)
	private String id;
	@TableField(value = "user_id")
	private String userId;
	@TableField(value = "group_entrust_id")
	private String groupEntrustId;
	@TableField(value = "perm_id")
	private String permId;
	@TableField(value = "org_id")
	private String orgId;
}
