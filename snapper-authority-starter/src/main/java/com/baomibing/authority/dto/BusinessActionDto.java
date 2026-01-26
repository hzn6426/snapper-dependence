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

import com.baomibing.authority.action.BusinessActionAction;
import com.baomibing.authority.state.BusinessActionState;
import com.baomibing.core.process.SProcess;
import com.baomibing.core.process.StateProcess;
import lombok.Data;

import java.util.EnumSet;

import static com.baomibing.authority.action.BusinessActionAction.STOP;
import static com.baomibing.authority.action.BusinessActionAction.USE;
import static com.baomibing.authority.state.BusinessActionState.ACTIVE;
import static com.baomibing.authority.state.BusinessActionState.UNACTIVE;


/**
 * BusinessActionDto
 *
 * @author zening (316279828@qq.com) 2025/6/26 09:29
 * @version 1.0.0
 **/
@Data
public class BusinessActionDto extends StateProcess<BusinessActionState, BusinessActionAction> {

    private String id;

    private String permId;

    private String actionValue;

    private Boolean actionIgnoreUserScope;

    private Boolean actionIgnoreGroupScope;

    private String actionIgnoreCompanyScopeTags;

    private String actionOnlyFilterCompanyTags;

    private String connectValue;

    private String connectIgnoreUserScopeTags;

    private String connectIgnoreGroupScopeTags;

    private Boolean connectBeAlwaysFilterCreateUserColumn;

    private String connectUserAuthColumn;

    private String connectGroupAuthColumn;

    private String connectTableNameWithAuthInject;

    private String connectTableNameWithColumnInject;

    private String state;

    private String note;

    private Boolean beLock;

    @Override
    public SProcess<BusinessActionState, BusinessActionAction> initProcess() {
        return builder
                .initState(UNACTIVE).deleteable(EnumSet.of(UNACTIVE))
                .process().source(UNACTIVE).target(ACTIVE).action(USE)
                .and()
                .process().source(ACTIVE).target(UNACTIVE).action(STOP)
                .build();
    }
}
