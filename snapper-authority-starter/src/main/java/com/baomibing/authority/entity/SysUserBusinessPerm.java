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

import java.util.Date;

/**
 * 用户的业务权限
 * 
 * @author zening
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_user_business_perm")
public class SysUserBusinessPerm extends MBaseModel {
    private static final long serialVersionUID = -3970114349242862705L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    @TableField("user_id")
    private String userId;
    @TableField("perm_id")
    private String permId;
    @TableField("perm_scope")
    private String permScope;
	@TableField("perm_start_time")
	private Date permStartTime;
	@TableField("perm_end_time")
	private Date permEndTime;
	@TableField("org_id")
	private String orgId;
    
    private transient String menuId;
    private transient String menuName;
    private transient String permName;
    private transient String parentId;

}
