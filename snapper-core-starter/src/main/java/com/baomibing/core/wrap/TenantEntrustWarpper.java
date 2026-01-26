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

package com.baomibing.core.wrap;

import com.baomibing.tool.constant.PermConstant;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * TenantEntrustWrapper
 *
 * @author zening
 * @version 1.0.0
 **/
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Accessors(chain = true)
public class TenantEntrustWarpper extends EntrustWarpper {

    //租户列
    private String[] tenantColumn = {PermConstant.TENANT_ID};
    //租户ID，在租户模式下必须有值
    private String tenantId = null;

    private Boolean beOnlyFilterTenant = Boolean.FALSE;

    //租户插入的位置-指定的表名或别名同级
    private String tableNameWithTenantAppend = null;
}
