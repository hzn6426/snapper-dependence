/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.entity;

import com.baomibing.authority.state.DictionaryState;
import com.baomibing.orm.base.MBaseModel;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 字典项
 * 
 * @author zening
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_dict_child")
public class SysDictChild extends MBaseModel {

    private static final long serialVersionUID = 6828248906226915526L;

    private String id;
    private String dictCode;
    private String dictName;
    private Integer priority;
    private DictionaryState state;
    private Boolean beDelete;
    private String parentId;
    private String groupId;
    private Boolean beLock;
    private transient String parentCode;

}
