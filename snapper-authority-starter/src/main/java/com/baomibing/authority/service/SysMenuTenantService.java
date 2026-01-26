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


import com.baomibing.authority.dto.MenuTenantDto;
import com.baomibing.authority.wrap.MenuWrap;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;


/**
 * 菜单接口
 * @author zening
 * @version 1.0.0
 */
public interface SysMenuTenantService extends MBaseService<MenuTenantDto> {

	/**
	 * 根据id批量删除菜单
	 * @param idList 菜单id列表
	 */
	void deleteByIdList(List<Integer> idList);

	
	/**
	 * 根据菜单ID列表获取资源菜单列表-关联资源
	 * @param menuIds 菜单ID列表
	 * @return
	 */
	List<MenuTenantDto> listAllResourceMenusByIds(Set<String> menuIds);

	/**
	 * 递归获取子节点对应的所有父节点
	 * 
	 * @param menuIds 菜单ID列表
	 * @return
	 */
	List<String> listAllParentByIds(Set<String> menuIds);

	/**
	 * 以树的方式加载所有菜单
	 *
	 * @return
	 */
	List<MenuWrap> treeAllMenus();


	/**
	 * 获取所有可授权的菜单
	 * @return
	 */
	List<MenuTenantDto> listAllMenusForGrant();
	/***
	 * 删除菜单
	 *
	 * @param ids 菜单ID列表
	 */
	void deleteMenu(Set<String> ids);

	/**
	 * 保存或更新菜单
	 *
	 * @param menu 待保存或更新的菜单
	 */
	void saveOrUpdateMenu(MenuTenantDto menu);
}
