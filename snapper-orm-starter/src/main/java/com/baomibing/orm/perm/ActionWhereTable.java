/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.orm.perm;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * ActionWhereTable
 *
 * @author zening 2024/1/28 14:02
 * @version 1.0.0
 **/
@Data
@Accessors(chain = true)
public class ActionWhereTable {

    private String tableComment;

    private String table;

    private String alias;
}
