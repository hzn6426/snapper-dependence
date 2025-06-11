/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.orm.perm;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 按钮权限action对应的表格信息
 *
 * @author zening 2023/4/20 11:25
 * @version 1.0.0
 **/
@Data @Accessors(chain = true)
public class ActionMapper {

    private String mapper;

    private String action;

    //用来展示where条件部分对应的表格
    private List<ActionWhereTable> whereTables;
    //用来展示select列部分
    private List<ActionSelectTable> selectTables;
}
