/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.entity;

import com.baomibing.authority.constant.enums.DictionaryTypeEnum;
import com.baomibing.authority.state.DictionaryState;
import com.baomibing.orm.base.MBaseModel;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 系统字典
 * 
 * @author zening
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_dictionary")
public class SysDictionary extends MBaseModel {

    private static final long serialVersionUID = -3511292728711014248L;
    private String id;
    private String dictCode;
    private String dictName;
    private DictionaryTypeEnum dictType;
    private Integer priority;

    private DictionaryState state;
    private Boolean beDelete;
    private String groupId;
    private Boolean beLock;
}
