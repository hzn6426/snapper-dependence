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


import com.baomibing.authority.dto.ButtonTenantDto;
import com.baomibing.core.base.MBaseService;
import com.baomibing.core.common.SearchResult;

import java.util.List;
import java.util.Set;

/**
 * 按钮接口
 * @author zening
 * @date 2018年2月7日 上午10:53:07
 * @version 1.0.0
 */
public interface SysButtonTenantService extends MBaseService<ButtonTenantDto> {
	
	/**
	 * 根据菜单查询按钮
	 * @param menuId 菜单ID
	 * @return
	 */
	List<ButtonTenantDto> listByMenu(String menuId);

	/**
	 * 根据菜单ID列表删除按钮列表
	 * @param menuIds 菜单ID列表
	 * @return
	 */
	List<ButtonTenantDto> listByMenus(Set<String> menuIds);

	/**
	 * 根据菜单查询按钮-不包含无授权的
	 * 
	 * @param menuId 菜单ID
	 * @return
	 */
	List<ButtonTenantDto> listByMenuForGrant(String menuId);

	/**
	 * 查询按钮信息及其对应的API信息
	 *
	 * @param v 查询条件封装
	 * @param pageNumber 页号
	 * @param pageSize 页数据量
	 * @return
	 */
	SearchResult<ButtonTenantDto> searchButtonsAndApiByMenu(ButtonTenantDto v, int pageNumber, int pageSize);

	/**
	 * 保存或更新按钮
	 *
	 * @param button 待保存或更新的按钮
	 */
	void saveOrUpdateButton(ButtonTenantDto button);

	/**
	 * 删除按钮
	 *
	 * @param ids 带删除的按钮ID列表
	 */
	void deleteButtons(Set<String> ids);

	/**
	 * 关键字模糊匹配按钮
	 * @param keyWord 查询关键字
	 * @return
	 */
	List<ButtonTenantDto> listByKeyWord(String keyWord);

	ButtonTenantDto getButtonAndApiById(String id);
}
