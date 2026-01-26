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

package com.baomibing.authority.dto;

import com.baomibing.authority.action.DictionaryAction;
import com.baomibing.authority.state.DictionaryState;
import com.baomibing.core.process.SProcess;
import com.baomibing.core.process.StateProcess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.EnumSet;

/**
 * 字典项
 *
 * @author zening
 * @version : 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DictionaryChildTenantDto extends StateProcess<DictionaryState, DictionaryAction> {
	private static final long serialVersionUID = 8403379467787033246L;

	private String id;

    /**
     * 编号
     */
    @NotBlank
    private String dictCode;

    /**
     * 名称
     */
    @NotBlank
    private String dictName;

    /**
     * 优先级(顺序)
     */
    private Integer priority;

    /**
     * 状态
     */
    private String state;

    /**
     * 是否删除
     */
    private Boolean beDelete;

    /**
     * 父ID
     */
    @NotBlank
    private String parentId;

    /**
     * 组织ID
     */
    private String groupId;
    
    /**
     * 父编码
     */
    private String parentCode;

    @Override
    public SProcess<DictionaryState, DictionaryAction> initProcess() {
        return getBuilder().actions(EnumSet.allOf(DictionaryAction.class)).states(EnumSet.allOf(DictionaryState.class))
                .initState(DictionaryState.ACTIVE).editable(EnumSet.of(DictionaryState.ACTIVE, DictionaryState.STOPPED)).deleteable(EnumSet.of(DictionaryState.ACTIVE, DictionaryState.STOPPED))
                .process().source(DictionaryState.STOPPED).target(DictionaryState.ACTIVE).action(DictionaryAction.USE)
                .and()
                .process().source(DictionaryState.ACTIVE).target(DictionaryState.STOPPED).action(DictionaryAction.STOP)
                .build();
    }
}
