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


import com.baomibing.authority.dto.SysTenantRoleDto;
import com.baomibing.authority.dto.SysTenantUserDto;
import com.baomibing.authority.dto.SysTenantUserRoleDto;
import com.baomibing.authority.dto.UserGroupDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

public interface SysTenantUserRoleService extends MBaseService<SysTenantUserRoleDto> {

    /**
     * 根据用户名获取组织对应的角色列表
     *
     * @param tenantId 租户ID
     * @param orgId 组织ID
     * @param userName 用户名
     * @return 用户角色列表，或空列表
     */
    List<SysTenantRoleDto> listRolesByGroupAndUserName(String tenantId, String orgId, String userName);


    /**
     * 根据用户ID列表删除用户角色关联
     *
     * @param tenantId 租户ID
     * @param orgId 组织ID
     * @param uids 用户ID列表
     */
    void deleteByGroupAndUsers(String tenantId, String orgId, Set<String> uids);

    /**
     * 根据租户ID列表删除用户角色关联
     * @param tenantIds 租户列表
     */
    void deleteByTenant(Set<String> tenantIds);

    /**
     * 根据用户id获取角色列表
     *
     * @param tenantId 租户ID
     * @param orgId 组织ID
     * @param userId 用户ID
     * @return
     */
    List<SysTenantRoleDto> listRolesByGroupAndUser(String tenantId, String orgId, String userId);

    /**
     * 根据用户列表及对应组织获取用户角色关联
     * @param tenantId 租户ID
     * @param orgId
     * @param userIds
     * @return
     */
    List<SysTenantUserRoleDto> listByGroupAndUsers(String tenantId, String orgId, Set<String> userIds);

    /**
     * 根据用户分配角色列表
     *
     * @param tenantId 租户ID
     * @param orgId 组织ID
     * @param userId 用户ID
     * @param roleIdList 角色ID列表
     */
    void saveFromUser(String tenantId, String orgId, String userId, List<String> roleIdList);

    /**
     * 根据角色分配用户列表
     *
     * @param tenantId 租户ID
     * @param roleId     角色ID
     * @param userGroups 组织及用户列表
     */
    void saveFromRole(String tenantId, String roleId, List<UserGroupDto> userGroups);

    /**
     * 根据角色ID查询用户
     *
     * @param tenantId 租户ID
     * @param orgId 组织ID
     * @param roleId 角色ID
     */
    List<SysTenantUserDto> listUsersByGroupAndRole(String tenantId, String orgId, String roleId);

    /**
     * 根据角色ID删除所有关联该角色的用户角色关联信息
     *
     * @param tenantId 租户ID
     * @param orgId 组织ID
     * @param roleId 角色ID
     */
    void deleteByGroupAndRole(String tenantId, String orgId, String roleId);

    /**
     * 根据角色ID列表删除所有关联该角色的用户角色关联信息
     *
     * @param tenantId 租户ID
     * @param orgId 组织ID
     * @param roleIds 角色ID列表
     */
    void deleteByGroupAndRoles(String tenantId, String orgId, Set<String> roleIds);

    /**
     * 根据角色ID列表删除所有与改角色关联的用户和组织
     *
     * @param roleIds
     * @param tenantId 租户ID
     */
    void deleteByRoles(Set<String> roleIds, String tenantId);

    /**
     * 根据用户ID列表
     * @param userIds
     * @param tenantId 租户ID
     */
    void deleteByUsers(Set<String> userIds, String tenantId);

    /**
     * 根据角色ID获取与组织关联的用户ID信息
     *
     * @param tenantId 租户ID
     * @param roleId 角色ID
     * @return
     */
    List<SysTenantUserDto> listGroupUsersByRoles(String tenantId, String roleId);
}
