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

import com.baomibing.authority.action.TenantUsetAction;
import com.baomibing.authority.state.TenantUsetState;
import com.baomibing.core.process.SProcess;
import com.baomibing.core.process.StateProcess;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.EnumSet;

/**
 * SysTenantUset
 *
 * @author zening
 * @version 1.0.0
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysTenantUsetDto extends StateProcess<TenantUsetState, TenantUsetAction> {

    private String id;

    private String tenantId;

    private String usetName;

    private String note;

    private String state;

    private String groupId;

    private String createUserCnName;

    private Date createTime;

    @Override
    public SProcess<TenantUsetState, TenantUsetAction> initProcess() {
        return getBuilder()
                .initState(TenantUsetState.ACTIVE).deleteable(EnumSet.of(TenantUsetState.UNACTIVE))
                .process().source(TenantUsetState.UNACTIVE).target(TenantUsetState.ACTIVE).action(TenantUsetAction.USE)
                .and()
                .process().source(TenantUsetState.ACTIVE).target(TenantUsetState.UNACTIVE).action(TenantUsetAction.STOP)
                .build();
    }
}
