/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户组角色
 *
 * @author zening
 * @version 1.0.0
 */
@Data
@Accessors(chain = true)
@TableName("sys_uset_role")
public class SysUsetRole {

    private String id;

    private String usetId;

    private String roleId;
}
