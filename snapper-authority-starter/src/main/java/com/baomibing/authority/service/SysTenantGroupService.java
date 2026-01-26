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


import com.baomibing.authority.dto.SysTenantGroupDto;
import com.baomibing.authority.vo.TenantGroupUserVo;
import com.baomibing.core.wrap.CommonTreeWrap;
import com.baomibing.core.wrap.GroupIntervalWrap;

import java.util.List;
import java.util.Set;

public interface SysTenantGroupService {

    /**
     * 根据ID获取组织
     * @param tenantId 租户ID
     * @param id 组织ID
     * @return
     */
    SysTenantGroupDto getIt(String tenantId, String id);

    /**
     * 获取group列表左右值区间的并集
     *
     * @param groupList 组织列表
     * @return
     */
    List<GroupIntervalWrap> unionGroupInterval(List<SysTenantGroupDto> groupList);


    /**
     * 根据父节点加载子节点
     *
     * @param tenantId 租户ID
     * @param parentId 父节点
     * @return
     */
    List<SysTenantGroupDto> listChildrenByParent(String tenantId, String parentId);


    /**
     * 添加分公司
     *
     * @param groupDto
     * @Return: void
     */
    SysTenantGroupDto saveChild(SysTenantGroupDto groupDto, boolean beCompany);

    /**
     * 添加成员
     *
     * @param tenantId 租户ID
     * @param pid   职位ID
     * @param gid   组织ID
     * @param users 用户ID列表
     * @Return: void
     */
    void addUsers(String tenantId, String pid, String gid, Set<String> users);

    /**
     * 移动成员
     *
     * @param tenantId 租户ID
     * @param ogid  源组织ID
     * @param togid 目标组织ID
     * @param users 移动的用户ID列表
     * @Return: void
     */
    void doMoveGroupUsers(String tenantId, String ogid, String togid, Set<String> users);

    /**
     * 删除成员
     *
     * @param tenantId 租户ID
     * @param gid   组织ID
     * @param users 用户的ID列表
     * @Return: void
     */
    void removeGroupUsers(String tenantId, String gid, Set<String> users);

    /**
     * 修改成员职位
     *
     * @param tenantId 租户ID
     * @param uid 用户ID
     * @param gid 组织ID
     * @param pid 职位ID
     * @Return: void
     */
    void doAssignUserPosition(String tenantId, String uid, String gid, String pid);


    /**
     * 删除组织
     *
     * @param tenantId 租户ID
     * @param gid 组织ID
     * @Return: void
     */
    void deleteGroup(String tenantId, String gid);

    /**
     * 根据组织删除租户
     * @param tenantIds 租户ID列表
     */
    void deleteByTenant(Set<String> tenantIds);

    /**
     * 编辑组织
     *
     * @param group 组织对象
     */
    void updateGroup(SysTenantGroupDto group);

    /**
     * 成员明细
     *
     * @param tenantId 租户ID
     * @param gid
     * @param uid
     * @Return: com.baomibing.authority.vo.GroupUserVo
     */
    TenantGroupUserVo getUserDetail(String tenantId, String uid, String gid);

    /**
     * 用户组织列表
     *
     * @param tenantId 租户ID
     * @param uid 用户ID
     * @Return: java.util.List<com.baomibing.authority.dto.GroupDto>
     */
    List<SysTenantGroupDto> listGroupsByUser(String tenantId, String uid);


    /**
     * 以树的方式封装获取所有组织和用户
     *
     * @param tenantId 租户ID
     * @return
     */
    List<CommonTreeWrap> treeAllGroupsAndUsers(String tenantId);

    /**
     * 以树得分昂视封装所有组织和职位
     *
     * @param tenantId 租户ID
     * @return
     */
    List<CommonTreeWrap> treeAllGroupsAndPositions(String tenantId);

    /**
     * 以树的方式封装所有组织
     *
     * @param tenantId 租户ID
     * @return
     */
    List<CommonTreeWrap> treeAllGroups(String tenantId);

    /**
     * 根据组织ID获取对应的公司
     *
     * @param tenantId 租户ID
     * @param id 组织ID
     * @return
     */
    SysTenantGroupDto getParentCompanyById(String tenantId, String id);

    /**
     * 获取分公司列表
     *
     * @param tenantId 租户ID
     * @param
     * @Return: java.util.List<com.baomibing.authority.dto.GroupDto>
     */
    List<SysTenantGroupDto> listBranchCompanines(String tenantId);

    /**
     * 初始化ROOT节点
     * @param tenantId 租户ID
     */
    SysTenantGroupDto doInitGroup(String tenantId);

    /**
     * 获取租户对应的根组织
     * @param tenantId 租户ID
     * @return
     */
    SysTenantGroupDto getRootGroup(String tenantId);

    /**
     * 获取或创建租户对应的公司
     * @param tenantId 租户ID
     * @return
     */
    SysTenantGroupDto doGetOrMakeCompany(String tenantId);

    /**
     * 以树的方式封装获取所有组织和Tag对应的租户用户
     * @param userTag
     * @return
     */
    List<CommonTreeWrap> treeAllGroupsAndUsersByTag(String tenantId, String userTag);

}
