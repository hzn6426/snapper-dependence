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

package com.baomibing.authority.service;

import com.baomibing.authority.dto.SysFunctionTenantDto;
import com.baomibing.core.base.MBaseService;
import com.baomibing.core.common.SearchResult;
import com.baomibing.tool.tenant.TenantFunctionConsume;

import java.util.List;
import java.util.Set;

public interface SysFunctionTenantService extends MBaseService<SysFunctionTenantDto> {

    SearchResult<SysFunctionTenantDto> search(SysFunctionTenantDto v, int pageNo, int pageSize);

    void saveFunction(SysFunctionTenantDto functionTenant);

    void updateFunction(SysFunctionTenantDto functionTenant);

    void deleteFunction(Set<String> ids);

    SysFunctionTenantDto getByUrlAndMethod(String url, String method);

    TenantFunctionConsume doConsume(String tenantId, String requestUrl, String requestMethod);

    void recoveryConsume(TenantFunctionConsume consume);

    void doOffline(Set<String> ids);

    void doOnline(Set<String> ids);

    void doOpenFunction(String tenantId, String functionId);

    void doDeferFunction(String tenantId, String functionId);

    void doCloseFunction(String tenantId, String functionId);

    List<SysFunctionTenantDto> listAllOnlineFunction();

    SysFunctionTenantDto getByFunctionId(String functionId);

    // 所有已上线月付的功能
    List<SysFunctionTenantDto> listAllOnlineMonthFunction();
}
