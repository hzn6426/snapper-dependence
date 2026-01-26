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


import com.baomibing.authority.dto.SysTenantUsetRoleDto;
import com.baomibing.core.base.MBaseService;

import java.util.Set;

public interface SysTenantUsetRoleService extends MBaseService<SysTenantUsetRoleDto> {

    /**
     * 保存用户组和角色列表
     * @param tenantId 租户ID
     * @param usetId 用户组ID
     * @param roleIds 角色列表
     */
    void saveFromUset(String tenantId, String usetId, Set<String> roleIds);

    /**
     * 根据用户组获取所有角色ID列表
     * @param tenantId 租户ID
     * @param usetId 用户组ID
     * @return
     */
    Set<String> listRolesByUset(String tenantId, String usetId);

    /**
     * 根据用户组ID删除对应的角色关系
     * @param usetIds 用户组ID列表
     * @param tenantId 租户ID
     */
    void deleteByUsets(Set<String> usetIds, String tenantId);

    /**
     * 根据租户ID列表删除所有角色关系
     * @param tenantIds 租户ID列表
     */
    void deleteByTenant(Set<String> tenantIds);
}
