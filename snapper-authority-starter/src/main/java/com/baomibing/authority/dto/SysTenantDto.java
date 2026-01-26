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

import com.baomibing.authority.action.TenantAction;
import com.baomibing.authority.state.TenantState;
import com.baomibing.core.process.SProcess;
import com.baomibing.core.process.StateProcess;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 租户
 *
 * @author zening
 * @version 1.0.0
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysTenantDto extends StateProcess<TenantState, TenantAction> {

    private String id;
    private String name;
    private String simpleName;
    private String note;
    private String state;
    private String root;
    private String businessId;
    private String tenantRank;
    private Long score;
    private BigDecimal balance;


    private Boolean beHaveSuper;
    private String createUserCnName;
    private Date createTime;
    private BigDecimal money;

    @Override
    public SProcess<TenantState, TenantAction> initProcess() {
        return getBuilder().initState(TenantState.ACTIVE)
                .process().source(TenantState.ACTIVE).target(TenantState.LOCKED).action(TenantAction.LOCK)
                .and()
                .process().source(TenantState.LOCKED).target(TenantState.ACTIVE).action(TenantAction.UNLOCK)
                .and()
                .process().source(TenantState.ACTIVE).target(TenantState.COMMIT).action(TenantAction.COMMIT)
                .and()
                .process().source(TenantState.COMMIT).target(TenantState.APPROVE).action(TenantAction.APPROVE)
                .and()
                .process().source(TenantState.COMMIT).target(TenantState.REJECT).action(TenantAction.REJECT)
                .and()
                .process().source(TenantState.REJECT).target(TenantState.COMMIT).action(TenantAction.COMMIT)
                .and()
                .process().source(TenantState.COMMIT).target(TenantState.ACTIVE).action(TenantAction.CANCEL)
                .build();
    }
}
