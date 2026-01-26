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


import com.baomibing.authority.constant.enums.MenuTypeEnum;
import com.baomibing.authority.constant.enums.ResourceTypeEnum;
import com.baomibing.authority.dto.ButtonTenantDto;
import com.baomibing.authority.dto.MenuTenantDto;

import java.util.List;
import java.util.Set;

public interface SysTenantRoleResourceService {




    /**
     * 根据角色列表及资源类型获取权限对应的资源ID列表
     * @param roleIds 角色列表
     * @param resourceType 资源类型 {@link ResourceTypeEnum}
     * @return
     */
    List<String> listPermResourceIdsByRoles(Set<String> roleIds, ResourceTypeEnum resourceType, String tenantId);


    /**
     * 根据角色列表和菜单ID获取对应的权限按钮列表-包括无权限的按钮
     * @param roleIds 角色ID列表
     * @param menuId 菜单ID
     * @param tenantId 租户ID
     * @return
     */
    List<ButtonTenantDto> listPermButtonsByRolesAndMenu(Set<String> roleIds, String menuId, String tenantId);

    /**
     * 获取角色列表及租户ID获取当前租户下所有菜单列表
     * @param roleIds 角色ID列表
     * @param tenantId 租户ID
     * @return
     */
    List<MenuTenantDto> listAllMenusForGrant(Set<String> roleIds, String tenantId);

//    /**
//     * 获取角色列表及租户ID及菜单对应的按钮列表
//     * @param roleIds
//     * @param tenantId
//     * @param menuId
//     * @return
//     */
//    List<ButtonTenantDto> listAllButtonsByMenuForGrant(Set<String> roleIds, String tenantId, String menuId);

    /**
     * 根据角色保存按钮的权限
     * @param tenantId 租户ID
     * @param roleId       角色ID
     * @param menuId       菜单ID
     * @param buttonIds 按钮ID列表
     */
    void saveButtonPermsByMenuAndRole(String tenantId, String roleId, String menuId, Set<String> buttonIds);

    /**
     * 根据角色保存按钮的权限
     * @param tenantId  租户ID
     * @param roleId    角色ID
     * @param buttonIds 按钮ID列表
     */
    void saveButtonPermsByRole(String tenantId, String roleId, Set<String> buttonIds);

    /**
     * 根据角色保存菜单的权限
     * @param tenantId 租户ID
     * @param roleId 角色ID
     * @param menuIds 菜单ID列表
     */
    void saveMenusPermsByRole(String tenantId, String roleId, Set<String> menuIds);

    /**
     * 获取角色列表对应的某个类型的权限菜单-包括无权限的菜单
     * @param roleIds 角色列表
     * @param type 菜单类型
     * @param tenantId 租户ID
     * @return
     */
    List<MenuTenantDto> listAllPermPointMenusByRoles(Set<String> roleIds, MenuTypeEnum type, String tenantId);


    /**
     * 根据用户获取所有权限按钮及其对应的业务权限范围
     *
     * @param userId 用户ID
     * @return
     */
    List<ButtonTenantDto> listPermButtonsForBusinessPermByGroupAndUser(String tenantId, String orgId, String userId);

    /**
     * 根据用户ID获取其组织对应权限的菜单和按钮列表及其对应的数据权限范围
     *
     * @param userId
     * @return
     */
    List<MenuTenantDto> listPermMenusAndButtonsForBusinessPermByUser(String tenantId, String orgId, String userId);

    /**
     * 根据角色删除对应的资源权限
     *
     * @param roles
     */
    void deleteByRoles(Set<String> roles, String tenantId);

    /**
     * 根据资源ID列表及对应的资源类型删除资源角色关系
     *
     * @param resourceIds 资源ID列表
     * @param resourceType 资源类型
     */
    void deleteByResources(Set<String> resourceIds, ResourceTypeEnum resourceType, String tenantId);

    /**
     * 删除资源ID列表及对应的资源类型删除资源角色关系
     * @param resourceIds
     * @param resourceType
     */
    void deleteByResources(Set<String> resourceIds, ResourceTypeEnum resourceType);

    /**
     * 根据用户组ID获取权限的菜单和按钮列表及其对应的数据权限范围
     * @param usetId 用户组ID
     * @return
     */
    List<MenuTenantDto> listPermMenusAndButtonsForBusinessPermByUset(String tenantId, String usetId);

    /**
     * 根据URL获取租户对应的角色列表
     * @param tenantId 租户ID
     * @param reqUrl 按钮URL
     * @param reqMethod 请求方法
     * @return
     */
    String getRolesByUrl(String tenantId, String reqUrl, String reqMethod);

    /**
     * 针对角色添加按钮权限，如果角色没有此按钮权限就添加
     * @param tenantId 租户ID
     * @param roleId 角色ID
     * @param buttonIds 按钮ID列表
     */
    void addButtonPermIfNotExistByRole(String tenantId, String roleId, Set<String> buttonIds);

    /**
     * 角色删除对应按钮权限
     * @param tenantId 租户ID
     * @param roleId 角色ID
     * @param buttonIds 按钮ID列表
     */
    void deleteButtonPermByRole(String tenantId, String roleId, Set<String> buttonIds);
}
