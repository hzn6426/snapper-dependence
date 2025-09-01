/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.orm.base;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class MBaseModel implements Serializable {
	private static final long serialVersionUID = 3106470533624582061L;
	
	@TableField("create_time")
    protected Date createTime;
    @TableField("update_time")
    protected Date updateTime;
    @TableField("create_user")
    protected String createUser;
    @TableField("update_user")
    protected String updateUser;
    @TableField("create_user_cn_name")
    protected String createUserCnName;
    @TableField("update_user_cn_name")
    protected String updateUserCnName;
    
}
