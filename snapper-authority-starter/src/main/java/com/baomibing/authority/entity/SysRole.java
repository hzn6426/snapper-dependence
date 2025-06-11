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
 * 角色管理,每个组织拥有自己的角色,角色只能修改自己组织及下级的角色
 * 
 * @author zening
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_role")
public class SysRole extends MBaseModel {
	private static final long serialVersionUID = -8582288342480798194L;

	@TableId(type = IdType.ASSIGN_ID)
    private String id;
    @TableField("role_name")
    private String roleName;
    private String note;
    @TableField("group_id")
    private String groupId;
    @TableField("state")
    private String state;
    @TableField("be_super")
    private Boolean beSuper;
    private Boolean beDefault;
    private Boolean beLock;
    
    
    private transient String companyName;
    private transient String companyId;

    private transient String usetNames;

    private transient String postNames;

}
