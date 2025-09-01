/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户与用户组关联
 * @author : zening
 * @version : 1.0.0
 */
@Data
@Accessors(chain = true)
@TableName("sys_user_uset")
public class SysUserUset {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String usetId;
    
    private String userId;
    
    private String orgId;
}
