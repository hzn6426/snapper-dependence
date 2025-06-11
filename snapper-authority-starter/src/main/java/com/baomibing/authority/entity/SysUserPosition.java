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
 * 用户职位（一个用户可以用多个职位）
 * 
 * @author zening
 * @version  1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_user_position")
public class SysUserPosition extends MBaseModel {
	private static final long serialVersionUID = -3472163236462019346L;
	
	@TableId(type = IdType.ASSIGN_ID)
	private String id;
	@TableField(value = "user_id")
	private String userId;
	@TableField(value = "position_id")
	private String positionId;
	@TableField(value = "group_id")
	private String groupId;
}
