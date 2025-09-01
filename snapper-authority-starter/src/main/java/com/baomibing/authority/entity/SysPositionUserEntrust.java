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
 * 职位用户委托
 * 
 * @author zening
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_position_user_entrust")
public class SysPositionUserEntrust extends MBaseModel {
	private static final long serialVersionUID = -8482183481639181007L;

	@TableId(type = IdType.ASSIGN_ID)
	private String id;
	@TableField(value = "user_id")
	private String userId;
	@TableField(value = "position_id")
	private String positionId;

}
