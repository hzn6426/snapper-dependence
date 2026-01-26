
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
import com.baomibing.authority.dto.UserDto;
import com.baomibing.authority.dto.UserGroupDto;
import com.baomibing.authority.dto.UserRoleDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

public interface SysUserRoleService extends MBaseService<UserRoleDto> {

	/**
	 * 根据用户名获取组织对应的角色列表
	 *
	 * @param orgId    组织ID
	 * @param userName 用户名
	 * @return 用户角色列表，或空列表
	 */
//	List<RoleDto> listRolesByGroupAndUserName(String orgId, String userName);


	/**
	 * 根据用户ID列表删除用户角色关联
	 *
	 * @param orgId 组织ID
	 * @param uids  用户ID列表
	 */
	void deleteByGroupAndUsers(String orgId, Set<String> uids);

	/**
	 * 根据用户id获取角色列表
	 *
	 * @param orgId  组织ID
	 * @param userId 用户ID
	 * @return
	 */
	List<RoleDto> listRolesByGroupAndUser(String orgId, String userId);

	/**
	 * 根据用户分配角色列表
	 *
	 * @param orgId      组织ID
	 * @param userId     用户ID
	 * @param roleIdList 角色ID列表
	 */
	void saveFromUser(String orgId, String userId, List<String> roleIdList);

	/**
	 * 根据角色分配用户列表
	 *
	 * @param orgId      组织ID
	 * @param userGroups 组织及用户列表
	 */
	void saveFromRole(String roleId, List<UserGroupDto> userGroups);

	/**
	 * 根据角色ID查询用户
	 * 
	 * @param orgId  组织ID
	 * @param roleId 角色ID
	 */
//	List<UserDto> listUsersByGroupAndRole(String orgId, String roleId);

	/**
	 * 根据角色ID删除所有关联该角色的用户角色关联信息
	 * 
	 * @param roleId 角色ID
	 */
//	void deleteByGroupAndRole(String orgId, String roleId);

	/**
	 * 根据角色ID列表删除所有关联该角色的用户角色关联信息
	 * 
	 * @param roleIds 角色ID列表
	 */
//	void deleteByGroupAndRoles(String orgId, Set<String> roleIds);

	/**
	 * 根据角色ID列表删除所有与改角色关联的用户和组织
	 *
	 * @param roleIds
	 */
	void deleteByRoles(Set<String> roleIds);

	/**
	 * 根据用户ID列表
	 * 
	 * @param userIds
	 */
	void deleteByUsers(Set<String> userIds);

	/**
	 * 根据角色ID获取与组织关联的用户ID信息
	 * 
	 * @param roleId 角色ID
	 * @return
	 */
	List<UserDto> listGroupUsersByRoles(String roleId);

	/**
	 * 根据用户列表及对应组织获取用户角色关联
	 * @param orgId
	 * @param userIds
	 * @return
	 */
	List<UserRoleDto> listByGroupAndUsers(String orgId, Set<String> userIds);

    /**
     * 复制组织用户的角色到目标用户 必须是同一组织
     * @param uid 待复制的用户 ID
     * @param gid 待复制的用户所在组织 ID
     * @param toUserId 目标用户
     */
    void doCopyUserRole(String uid, String gid, String toUserId);
}
