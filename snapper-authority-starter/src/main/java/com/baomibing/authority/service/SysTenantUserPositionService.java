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


import com.baomibing.authority.dto.SysTenantPositionDto;
import com.baomibing.authority.dto.SysTenantUserPositionDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

public interface SysTenantUserPositionService extends MBaseService<SysTenantUserPositionDto> {

    /**
     * 根据用户ID，组织ID获取用户对应组织职位的角色列表
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @param groupId 组织ID
     * @return
     */
    List<String> listPositionRoleIdsByUserAndGroup(String tenantId, String userId, String groupId);

    /**
     * 根据用户ID，组织ID获取用户对应组织职位(约定：一个用户在一个组织只能有一个职位)
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @param groupId 组织ID
     * @return
     */
    SysTenantPositionDto getPositionByUserAndGroup(String tenantId, String userId, String groupId);

    /**
     * 根据用户ID，组织ID获取用户对应组织职位
     * @param users 用户ID
     * @param pid 职位ID
     * @param tenantId 租户ID
     * @return
     */
    List<SysTenantUserPositionDto> getPositionByUsersAndPosition(Set<String> users, String pid, String tenantId);

    /**
     * 根据用户ID，组织ID删除用户对应组织职位
     * @param tenantId 租户ID
     * @param users 用户ID
     * @param gid 组织ID
     * @return
     */
    void removePositionUsers(String tenantId, String gid, Set<String> users);

    /**
     * 根据职位ID列表删除用户对应组织职位
     * @param pids 职位ID列表
     * @param tenantId 租户ID
     * @return
     */
    void deleteByPositions(Set<String> pids, String tenantId);

    /**
     * 根据用户ID列表删除用户对应的组织职位
     *
     * @param uids 用户ID列表
     * @param tenantId 租户ID
     */
    void deleteByUsers(Set<String> uids, String tenantId);

    /**
     * 根据组织ID及用户列表删除用户组织下对应的所有职位
     *
     * @param tenantId 租户ID
     * @param gid 组织ID
     * @param users 用户ID列表
     */
    void deleteByGroupAndUsers(String tenantId, String gid, Set<String> users);

    /**
     * 根据租户列表删除用户对应的组织职位
     * @param tenantIds 租户ID列表
     */
    void deleteByTenant(Set<String> tenantIds);

    /**
     * 查找职位对应的用户Id
     *
     * @param positionId 职位ID
     * @param tenantId 租户ID
     * @Return: java.util.List<java.lang.String>
     */
    List<String> listUserByPosition(String positionId, String tenantId);
}
