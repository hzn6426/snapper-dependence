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


import com.baomibing.authority.dto.SysTenantUsetUserExceptEntrustDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

public interface SysTenantUsetUserExceptEntrustService extends MBaseService<SysTenantUsetUserExceptEntrustDto> {

    /**
     * 根据用户组ID和权限ID获取用户组委托的用户列表
     * @param usetIds 用户组列表
     * @param permId 权限ID
     * @param tenantId 租户ID
     * @return
     */
    List<String> listEntrustUserCodesByUsetAndPerm(Set<String> usetIds, String permId, String tenantId);

    /**
     * 根据用户组ID和权限ID获取用户组委托的用户列表
     * @param usetIds 用户组列表
     * @param permId 权限ID
     * @param tenantId 租户ID
     * @return
     */
    List<String> listEntrustUserIdsByUsetAndPerm(Set<String> usetIds, String permId, String tenantId);

    /**
     * 根据用户组ID和权限ID删除对应用户组的权限
     * @param tenantId 租户ID
     * @param usetId 用户组ID
     * @param permId 权限ID
     */
    void deleteByUsetAndPerm(String tenantId, String usetId, String permId);
}
