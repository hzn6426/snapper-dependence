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


import com.baomibing.authority.dto.SysTenantUserGroupDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

/**
 * SysTenantUserGroupService
 *
 * @author zening
 * @version 1.0.0
 **/
public interface SysTenantUserGroupService extends MBaseService<SysTenantUserGroupDto> {

    /**
     * 根据用户ID获取用户组关系列表
     *
     * @param userId 用户ID
     * @return
     */
    List<SysTenantUserGroupDto> listByUser(String tenantId, String userId);

    /**
     * 根据组织ID获取该组织及其子组织的用户组织关联列表
     *
     * @param tenantId 租户ID
     * @param groupId 组织ID
     * @return
     */
    List<SysTenantUserGroupDto> listItAndChildByGroup(String tenantId, String groupId);

//    /**
//     * 切换用户到组织
//     *
//     * @param tenantId 租户ID
//     * @param userId  用户ID
//     * @param groupId 组织ID
//     */
//    void changeUser2Group(String tenantId, String userId, String groupId);

    /**
     * 根据组织id，用户id查询用户组织关系列表
     *
     * @param users
     * @param gid
     * @param tenantId 租户ID
     * @Return: java.util.List<com.baomibing.authority.dto.UserGroupDto>
     */
    List<SysTenantUserGroupDto> listByUsersAndGroup(Set<String> users, String gid, String tenantId);

    /**
     * 根据组织id，用户id删除用户组织关系列表
     *
     * @param users
     * @param gid
     * @param tenantId 租户ID
     * @Return: void
     */
    void deleteByGroupIdAndUsers(Set<String> users, String gid, String tenantId);

    /**
     * 根据组织id删除用户组织关系列表
     *
     * @param gids
     * @param tenantId 租户ID
     * @Return: void
     */
    void deleteByGroups(Set<String> gids, String tenantId);

    /**
     * 根据用户ID列表删除用户组织关系列表
     *
     * @param uids
     * @param tenantId 租户ID
     */
    void deleteByUsers(Set<String> uids, String tenantId);

    /**
     * 根据租户ID删除用户组织关系列表
     * @param tenantIds 租户ID列表
     */
    void deleteByTenant(Set<String> tenantIds);

    /**
     * 用户编号获取组织id
     *
     * @param userNo
     * @param tenantId 租户ID
     * @Return: java.util.List<java.lang.String>
     */
    String getGroupIdByUserNo(String userNo, String tenantId);

    /**
     * 绑定hmac 用户
     * @param tenantId 租户ID
     * @param tenantUserId 租户用户ID
     * @param tenantUserOrgId 租户组织ID
     * @param hmacUserId hmac 用户ID
     */
    void doBindHmacUser(String tenantId, String tenantUserId, String tenantUserOrgId, String hmacUserId);
}
