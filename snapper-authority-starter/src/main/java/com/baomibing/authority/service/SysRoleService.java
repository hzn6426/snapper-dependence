
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


import com.baomibing.authority.dto.RoleDto;
import com.baomibing.core.base.MBaseService;
import com.baomibing.core.common.SearchResult;

import java.util.List;
import java.util.Set;

public interface SysRoleService extends MBaseService<RoleDto> {
	
	/**
	 * 根据条件分页查询职位列表
	 *
	 * @param v          条件封装
	 * @param pageNumber 页号
	 * @param pageSize   每页数量
	 * @return
	 */
	SearchResult<RoleDto> search(RoleDto v, int pageNumber, int pageSize);


	void saveRole(RoleDto role);

	void updateRole(RoleDto role);

	/**
	 * 获取所有角色列表，注意本方法未过滤组织
	 * 
	 * @return
	 */
	List<RoleDto> listAllRoles();

	/**
	 * 启用
	 * 
	 * @param ids 角色ID列表
	 */
	void use(Set<String> ids);

	/**
	 * 停用
	 * 
	 * @param ids 角色ID列表
	 */
	void stop(Set<String> ids);

	/**
	 * 删除
	 * 
	 * @param ids 角色ID列表
	 */
	void deleteRoles(Set<String> ids);

	/**
	 * 获取默认角色
	 * @param userNo 用户编号
	 * @return
	 */
	RoleDto getDefaultRoleByUser(String userNo);

	/**
	 * 创建默认角色
	 * @param userNo 用户编号
	 * @param groupId 用户组织ID
	 * @return
	 */
	RoleDto makeDefaultRoleByUser(String userNo, String groupId);

	/**
	 * 根据用户ID列表删除对应的默认角色
	 * @param userIds 用户ID列表
	 */
	void deleteDefaultsByUsers(Set<String> userIds);

	/**
	 * 根据用户ID和组织ID获取角色列表
	 * @param userId 租户ID
	 * @param orgId 组织ID
	 * @return
	 */
	List<RoleDto> listRolesByUser(String userId, String orgId);
}
