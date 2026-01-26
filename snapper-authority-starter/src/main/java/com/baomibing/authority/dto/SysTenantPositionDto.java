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

import com.baomibing.authority.action.TenantPositionAction;
import com.baomibing.authority.state.TenantPositionState;
import com.baomibing.core.process.SProcess;
import com.baomibing.core.process.StateProcess;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.EnumSet;
import java.util.List;

/**
 * SysTenantPositionDto
 *
 * @author zening
 * @version 1.0.0
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysTenantPositionDto extends StateProcess<TenantPositionState, TenantPositionAction> {

    private String id;
    private String tenantId;
    private String postName;
    private String orgId;
    private Boolean beManager;
    private String permScope;
    private String state;
    private String note;
    private String groupId;

    //当权限选择范围为自定义时，传入的自定义委托信息
    private List<String> entrusts;

    @Override
    public SProcess<TenantPositionState, TenantPositionAction> initProcess() {
        return getBuilder().initState(TenantPositionState.ACTIVE)
                .editable(EnumSet.of(TenantPositionState.ACTIVE,TenantPositionState.UNACTIVE))
                .process().source(TenantPositionState.ACTIVE).target(TenantPositionState.UNACTIVE).action(TenantPositionAction.STOP)
                .and().process().source(TenantPositionState.UNACTIVE).target(TenantPositionState.ACTIVE).action(TenantPositionAction.USE)
                .build();
    }
}
