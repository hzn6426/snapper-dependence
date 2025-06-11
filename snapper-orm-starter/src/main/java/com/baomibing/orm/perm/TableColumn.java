/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.orm.perm;

import lombok.Data;

/**
 * TableColumn
 *
 * @author zening 2023/4/20 14:43
 * @version 1.0.0
 **/
@Data
public class TableColumn {

    private String columnName;

    private String dataType;

    private String columnComment;

    private String tableName;
    //是否选中的权限列--用于条件过滤
    private Boolean beInPerm;
    //是否排除权限列- 用户选列则排除
    private Boolean beSpecial;
}
