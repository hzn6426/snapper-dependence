
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

import static com.baomibing.authority.action.DictionaryAction.STOP;
import static com.baomibing.authority.action.DictionaryAction.USE;
import static com.baomibing.authority.state.DictionaryState.ACTIVE;
import static com.baomibing.authority.state.DictionaryState.STOPPED;

/**
 * 字典项
 * 
 * @author zening
 * @since 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DictionaryChildDto extends StateProcess<DictionaryState, DictionaryAction> {

	private String id;
    @NotBlank
    private String dictCode;
    @NotBlank
    private String dictName;
    private Integer priority;
    private String state;
    private Boolean beDelete;
    @NotBlank
    private String parentId;
    private String groupId;
    private String parentCode;
    private String parentName;
    private Boolean beLock;

    @Override
	public SProcess<DictionaryState, DictionaryAction> initProcess() {
		return builder.initState(ACTIVE).editable(EnumSet.of(ACTIVE, STOPPED)).deleteable(EnumSet.of(ACTIVE, STOPPED))
				.process().source(STOPPED).target(ACTIVE).action(USE)
                .and()
				.process().source(ACTIVE).target(STOPPED).action(STOP)
                .build();
    }
}
