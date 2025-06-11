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
 * 用户委托，用来委托某个用户的权限
 * 
 * @author zening
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_user_entrust")
public class SysUserEntrust extends MBaseModel {
	private static final long serialVersionUID = 6768192415708080994L;

	@TableId(type = IdType.ASSIGN_ID)
	private String id;
	@TableField(value = "user_id")
	private String userId;
	@TableField(value = "user_entrust_id")
	private String userEntrustId;
	@TableField(value = "perm_id")
	private String permId;
	@TableField(value = "org_id")
	private String orgId;
}
