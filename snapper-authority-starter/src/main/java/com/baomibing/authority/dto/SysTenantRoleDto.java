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

import com.baomibing.authority.action.TenantRoleAction;
import com.baomibing.authority.state.TenantRoleState;
import com.baomibing.core.process.SProcess;
import com.baomibing.core.process.StateProcess;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.EnumSet;

/**
 * SysTenantRole
 *
 * @author zening
 * @version 1.0.0
 **/
@Data
@Accessors(chain = true)
public class SysTenantRoleDto extends StateProcess<TenantRoleState, TenantRoleAction> {

    private String id;
    private String tenantId;
    private String roleName;
    private String note;
    private String groupId;
    private String state;
    private Boolean beRoot;
    private Date createTime;
    private String createUserCnName;

    @Override
    public SProcess<TenantRoleState, TenantRoleAction> initProcess() {
        return getBuilder().initState(TenantRoleState.ACTIVE).deleteable(EnumSet.of(TenantRoleState.UNACTIVE))
            .process().source(TenantRoleState.UNACTIVE).target(TenantRoleState.ACTIVE).action(TenantRoleAction.USE)
            .and()
            .process().source(TenantRoleState.ACTIVE).target(TenantRoleState.UNACTIVE).action(TenantRoleAction.STOP)
            .build();
    }
}
