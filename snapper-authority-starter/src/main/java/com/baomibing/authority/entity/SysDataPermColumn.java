/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.entity;

import com.baomibing.orm.base.MBaseModel;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * SysDataPermColumn
 *
 * @author zening 2023/5/6 11:15
 * @version 1.0.0
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_data_perm_column")
public class SysDataPermColumn extends MBaseModel {

    private String id;
    private String tableName;
    private String tableComment;
    private String columnName;
    private String columnComment;
    private String columnType;
    private String groupId;
}
