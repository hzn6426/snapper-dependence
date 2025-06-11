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
 * 系统参数
 * 
 * @author zening
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_param")
public class SysParam extends MBaseModel {
	private static final long serialVersionUID = -7832248064765006414L;

	@TableId(type = IdType.ASSIGN_ID)
	private String id;
	@TableField(value = "param_name")
    private String paramName;
	@TableField(value = "param_code")
    private String paramCode;
	@TableField(value = "param_value")
    private String paramValue;
	@TableField(value = "be_delete")
    private Boolean beDelete;
	@TableField(value = "group_id")
    private String groupId;
	private String note;
	private Boolean beLock;
}
