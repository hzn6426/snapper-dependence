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

import java.util.Date;

/**
 * SysUsetColumnPerm
 *
 * @author zening 2023/5/16 15:23
 * @version 1.0.0
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_uset_column_perm")
public class SysUsetColumnPerm extends MBaseModel {

    private String id;

    private String usetId;

    private String permId;

    private Date permStartTime;

    private Date permEndTime;

    private String columnExpress;
}
