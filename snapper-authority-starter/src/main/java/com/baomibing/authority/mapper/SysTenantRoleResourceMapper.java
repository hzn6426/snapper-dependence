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

package com.baomibing.authority.mapper;

import com.baomibing.authority.entity.SysButtonTenant;
import com.baomibing.authority.entity.SysMenuTenant;
import com.baomibing.authority.entity.SysResourceApiTenant;
import com.baomibing.authority.entity.SysTenantRoleResource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysTenantRoleResourceMapper extends BaseMapper<SysTenantRoleResource> {

    /**
     * 根据角色ID列表及菜单ID获取权限按钮
     * @param roleIdList 角色ID列表
     * @param menuId 菜单ID
     * @return
     */
    List<SysButtonTenant> listPermButtonsByMenuAndRoles(@Param("roleIdList")List<String> roleIdList, @Param("menuId")String menuId, @Param("tenantId") String tenantId);

    /**
     * 根据角色ID列表获取权限按钮
     * @param roleIdList 角色ID列表
     * @param tenantId 租户ID
     * @return
     */
    List<SysButtonTenant> listPermButtonsByRoles(@Param("roleIdList")List<String> roleIdList, @Param("tenantId") String tenantId);

    /**
     * 根据角色ID列表获取权限API列表
     * @param roleIdList 角色ID列表
     * @return
     */
    List<SysResourceApiTenant> listResourceApiByRoles(@Param("roleIdList")List<String> roleIdList, @Param("tenantId") String tenantId);
    /**
     * 根据角色ID列表获取所有权限菜单列表
     * @param roleIdList
     * @return
     */
    List<SysMenuTenant> listAllPermMenusByRoles(@Param("roleIdList")List<String> roleIdList, @Param("tenantId") String tenantId);

    /**
     * 根据角色ID列表获取所有权限对应的端菜单列表
     * @param roleIdList 角色列表
     * @param menuType 菜单类型
     * @return
     */
    List<SysMenuTenant> listAllPermPointMenusByRoles(@Param("roleIdList")List<String> roleIdList, @Param("menuType")String menuType, @Param("tenantId") String tenantId);


    /**
     * 展示所有菜单及菜单下的按钮-为分配权限
     * @return
     */
    List<SysMenuTenant> listAllMenusAndButtonsForGrant(@Param("tenantId") String tenantId);

    /**
     * 展示所有菜单
     *
     * @return
     */
    List<SysMenuTenant> listAllMenusForGrant(@Param("roleIdList")List<String> roleIdList, @Param("tenantId") String tenantId);

    /**
     * 获取所有已授权按钮对应的角色列表-用于授权缓存预热
     *
     * @return
     */
    List<SysResourceApiTenant> listAllPermButtonsForGroupRoleIds(@Param("tenantId") String tenantId);

    /**
     * 获取所有未授权按钮对应的角色列表-用于授权缓存预热
     * @return
     */
    List<SysResourceApiTenant> listAllUnPermButtonsForGroupRoleIds(@Param("tenantId") String tenantId);

    /**
     * 根据用户及组织获取所有权限按钮及其对应的业务权限范围
     *
     * @param userId 用户ID
     * @param orgId  组织ID
     * @return
     */
    List<SysButtonTenant> listPermButtonsForBusinessPermByGroupAndUser(@Param("tenantId") String tenantId, @Param("orgId") String orgId, @Param("userId") String userId);

    /**
     * 根据用户组获取所有权限按钮及对应的业务操作范围
     * @param usetId 用户组
     * @return
     */
    List<SysButtonTenant> listPermButtonsForBusinessPermByUset(@Param("tenantId") String tenantId, @Param("usetId") String usetId);

    /**
     * 根据URL获取租户所需要访问URL对应的角色列表
     * @param tenantId 租户ID
     * @param url URL
     * @return
     */
    List<String> listRolesByUrl(@Param("tenantId") String tenantId, @Param("url") String url, @Param("reqMethod") String reqMethod);
}
