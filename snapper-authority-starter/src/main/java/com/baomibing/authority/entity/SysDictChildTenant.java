/*
 * Copyright (c) 2020-2025, zening (316279828@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.baomibing.authority.entity;

import com.baomibing.authority.state.DictionaryState;
import com.baomibing.orm.base.MBaseModel;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
/**
 * 租户字典
 * @author zening
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_dict_child_tenant")
public class SysDictChildTenant extends MBaseModel {


    private String id;

    /**
     * 编号
     */
    private String dictCode;

    /**
     * 名称
     */
    private String dictName;

    /**
     * 优先级(顺序)
     */
    private Integer priority;

    /**
     * 状态
     */
    private DictionaryState state;

    /**
     * 是否删除
     */
    private Boolean beDelete;

    /**
     * 父ID
     */
    private String parentId;

    /**
     * 组织ID
     */
    private String groupId;
    
    /**
     * 父编码
     */
    private transient String parentCode;

}
