/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.orm.perm;

import lombok.Data;

/**
 * ActionTableColumn
 *
 * @author zening 2024/1/28 14:00
 * @version 1.0.0
 **/
@Data
public class ActionTableColumn {

    private String columnName;

    private String dataType;

    private String columnComment;

    private String tableName;
    //是否选中的权限列--用于条件过滤
    private Boolean beInPerm;
    //是否排除权限列- 用户选列则排除
    private Boolean beSpecial;
}
