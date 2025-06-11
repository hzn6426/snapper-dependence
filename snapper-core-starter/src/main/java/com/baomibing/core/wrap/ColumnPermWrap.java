/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.wrap;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * ColumnPermWrap
 *
 * @author zening 2023/6/9 16:13
 * @version 1.0.0
 **/
@Data @Accessors(chain = true)
public class ColumnPermWrap {

    private String tableName;

    private String columnName;

    //列权限-特殊列(各种聚合函数 或者ifNull, switch case...生成的列)
    private Boolean beSpecial;
}
