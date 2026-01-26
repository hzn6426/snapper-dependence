
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

import com.baomibing.authority.entity.SysButton;
import com.baomibing.authority.entity.SysMenu;
import com.baomibing.authority.entity.SysResourceApi;
import com.baomibing.authority.entity.SysRoleResource;
import com.baomibing.orm.base.MBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface SysRoleResourceMapper extends MBaseMapper<SysRoleResource> {

	/**
	 * 根据角色ID列表及菜单ID获取权限按钮
	 * 
	 * @param roleIdList 角色ID列表
	 * @param menuId     菜单ID
	 * @return
	 */
	List<SysButton> listPermButtonsByMenuAndRoles(@Param("roleIdList") List<String> roleIdList,
												  @Param("menuId") String menuId);

	/**
	 * 根据角色ID列表获取权限API列表
	 * 
	 * @param roleIdList 角色ID列表
	 * @return
	 */
	List<SysResourceApi> listResourceApiByRoles(@Param("roleIdList") List<String> roleIdList);

	/**
	 * 根据角色ID列表获取所有权限菜单列表
	 * 
	 * @param roleIdList
	 * @return
	 */
	List<SysMenu> listAllPermMenusByRoles(@Param("roleIdList") List<String> roleIdList);

	/**
	 * 根据角色ID列表获取所有权限对应的端菜单列表
	 * 
	 * @param roleIdList 角色列表
	 * @param menuType   菜单类型
	 * @return
	 */
	List<SysMenu> listAllPermPointMenusByRoles(@Param("roleIdList") List<String> roleIdList,
			@Param("menuType") String menuType);

	/**
	 * 超级管理员获取菜单列表
	 * 
	 * @param menuType
	 * @return
	 */
	List<SysMenu> listAllPermMenusForSuper(@Param("menuType") String menuType);

	/**
	 * 展示所有菜单及菜单下的按钮-为分配权限
	 * 
	 * @return
	 */
	List<SysMenu> listAllMenusAndButtonsForGrant();

	/**
	 * 展示所有菜单
	 * 
	 * @return
	 */
	List<SysMenu> listAllMenusForGrant();

	/**
	 * 展示角色对应的菜单
	 *
	 * @return
	 */
	List<SysMenu> listAllRoleMenusForGrant(@Param("roleIds") Set<String> roleIds);

	/**
	 * 根据菜单ID及角色列表获取对应的按钮列表
	 * @param menuId
	 * @param roleIds
	 * @return
	 */
	List<SysButton> listAllRoleButtonsForGrant(@Param("menuId") String menuId, @Param("roleIds") Set<String> roleIds);

	/**
	 * 根据菜单ID获取对应的按钮列表
	 * @param menuId
	 * @return
	 */
	List<SysButton> listAllButtonsForGrant(@Param("menuId") String menuId);
	/**
	 * 获取所有已授权按钮对应的角色列表-用于授权缓存预热
	 * 
	 * @return
	 */
	List<SysResourceApi> listAllPermButtonsForGroupRoleIds(@Param("buttonIds") Set<String> buttonIds);

	/**
	 * 获取所有未授权按钮对应的角色列表-用于授权缓存预热
	 * 
	 * @return
	 */
	List<SysResourceApi> listAllUnPermButtonsForGroupRoleIds(@Param("buttonIds") Set<String> buttonIds);

	/**
	 * 根据用户及组织获取所有权限按钮及其对应的业务权限范围
	 * 
	 * @param userId 用户ID
	 * @param orgId  组织ID
	 * @return
	 */
	List<SysButton> listPermButtonsForBusinessPermByGroupAndUser(@Param("orgId") String orgId,
			@Param("userId") String userId);

	/**
	 * 根据用户组获取所有权限按钮及对应的业务操作范围
	 * @param usetId 用户组
	 * @return
	 */
	List<SysButton> listPermButtonsForBusinessPermByUset(@Param("usetId") String usetId);

	/**
	 * 根据角色列表获取权限按钮
	 * @param roleIdList 角色列表
	 * @return
	 */
	List<SysButton> listPermButtonsByRoles(@Param("roleIdList") List<String> roleIdList);

	/**
	 * 根据用户及权限获取对应的权限范围信息
	 * @param orgId  用户所在组织ID
	 * @param userId 用户ID
	 * @param permId 权限ID
	 * @return
	 */
	List<SysButton> listUserPermScopeByPerm(@Param("orgId") String orgId, @Param("userId") String userId, @Param("permId") String permId);

	/**
	 * 根据用户组列表及权限获取对应的权限范围列表
	 * @param usetIdList 用户组列表
	 * @param permId 权限ID
	 * @return
	 */
	List<SysButton> listUsetPermScopeByPerm(@Param("usetIdList") Set<String> usetIdList, @Param("permId") String permId);
}
