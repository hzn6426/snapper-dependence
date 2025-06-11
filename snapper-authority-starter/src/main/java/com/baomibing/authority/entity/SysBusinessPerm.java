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
 * 逻辑权限实体类-用来定义逻辑数据权限（可视范围和操作范围）
 * 
 * @author zening
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_business_perm")
public class SysBusinessPerm extends MBaseModel {
	private static final long serialVersionUID = -6906633087467453834L;

	@TableId(type = IdType.ASSIGN_ID)
	private String id;
	@TableField(value = "menu_id")
	private String menuId;
	@TableField(value = "perm_name")
	private String permName;
	@TableField(value = "perm_action")
	private String permAction;
	@TableField(value = "req_url")
	private String reqUrl;
	@TableField(value = "req_method")
	private String reqMethod;
	@TableField(value = "note")
	private String note;
	@TableField(value = "button_id")
	private String buttonId;
	private String actionRef;
	
}
