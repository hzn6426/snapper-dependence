
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

import com.baomibing.authority.action.UsetAction;
import com.baomibing.authority.state.UsetState;
import com.baomibing.core.process.SProcess;
import com.baomibing.core.process.StateProcess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.EnumSet;

/**
 * 用户组
 * @author : zening
 * @since: 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class UsetDto extends StateProcess<UsetState, UsetAction> implements Serializable {

	private String id;
    
    private String usetName;
    
    private String note;
    
    private String state;
    
    private String groupId;

    private Boolean beLock;

    private Date createTime;

    private String createUserCnName;

    @Override
    public SProcess<UsetState, UsetAction> initProcess() {
        return getBuilder()
			.initState(UsetState.ACTIVE).deleteable(EnumSet.of(UsetState.UNACTIVE))
            .process().source(UsetState.UNACTIVE).target(UsetState.ACTIVE).action(UsetAction.USE)
            .and()
            .process().source(UsetState.ACTIVE).target(UsetState.UNACTIVE).action(UsetAction.STOP)
            .build();
    }
}
