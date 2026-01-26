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


import com.baomibing.authority.dto.SysTenantUserDto;
import com.baomibing.authority.dto.SysTenantUserGroupDto;
import com.baomibing.authority.dto.SysTenantUserUsetDto;
import com.baomibing.authority.dto.SysTenantUsetDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

public interface SysTenantUserUsetService extends MBaseService<SysTenantUserUsetDto> {

    /**
     * 根据用户ID列表删除对应的用户与组关系
     * @param userIds 用户ID列表
     * @param tenantId 租户ID
     */
    void deleteByUsers(Set<String> userIds, String tenantId);

    /**
     * 根据用户组ID列表删除对应的用户与组关系
     * @param usetIds 用户组ID列表
     * @param tenantId 租户ID
     */
    void deleteByUsets(Set<String> usetIds, String tenantId);

    /**
     * 根据租户ID列表删除对应的用户与组关系
     * @param tenantIds 租户ID列表
     */
    void deleteByTenant(Set<String> tenantIds);

    /**
     * 根据用户组ID获取与组织关联的用户ID信息
     *
     * @param tenantId 租户ID
     * @param usetId 角色ID
     * @return
     */
    List<SysTenantUserDto> listGroupUsersByUset(String tenantId, String usetId);

    /**
     * 根据用户组分配用户列表
     *
     * @param tenantId 租户ID
     * @param usetId 用户组ID
     * @param userGroups 组织及用户列表
     */
    void saveFromUset(String tenantId, String usetId, List<SysTenantUserGroupDto> userGroups);

    /**
     * 根据组织ID及用户ID获取对应的用户组
     *
     * @param tenantId 租户ID
     * @param orgId  组织ID
     * @param userId 用户ID
     * @return
     */
    List<SysTenantUsetDto> listUsetsByGroupAndUser(String tenantId, String orgId, String userId);

    /**
     * 根据组织ID及用户ID列表获取对应的用户组列表
     *
     * @param tenantId 租户ID
     * @param orgId 组织ID
     * @param userIds 用户ID列表
     * @return
     */
    List<SysTenantUserUsetDto> listByGroupAndUsers(String tenantId, String orgId, Set<String> userIds);
}
