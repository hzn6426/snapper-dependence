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

import com.baomibing.authority.action.FunctionTenantAction;
import com.baomibing.authority.state.FunctionTenantState;
import com.baomibing.core.process.SProcess;
import com.baomibing.core.process.StateProcess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * SysFunctionTenantDto
 *
 * @author zening
 * @version 1.0.0
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysFunctionTenantDto extends StateProcess<FunctionTenantState, FunctionTenantAction> {

    private String id;
    private String functionName;
    private String requestUrl;
    private String requestMethod;
    private String feeType;
    private BigDecimal unitPrice;
    private String state;
    private Date expireTime;
    private String functionId;

    private String createUserCnName;
    private Date createTime;
    private String tenantId;

    @Override
    public SProcess<FunctionTenantState, FunctionTenantAction> initProcess() {
        return getBuilder().initState(FunctionTenantState.OFFLINE)
                .process().source(FunctionTenantState.OFFLINE).target(FunctionTenantState.ONLINE).action(FunctionTenantAction.ONLINE)
                .and()
                .process().source(FunctionTenantState.ONLINE).target(FunctionTenantState.OFFLINE).action(FunctionTenantAction.OFFLINE)
                .build();
    }
}
