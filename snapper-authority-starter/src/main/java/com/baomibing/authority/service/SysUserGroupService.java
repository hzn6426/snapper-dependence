
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


import com.baomibing.authority.dto.UserGroupDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

public interface SysUserGroupService extends MBaseService<UserGroupDto> {

	/**
	 * 根据用户ID获取用户组关系列表
	 *
	 * @param userId 用户ID
	 * @return
	 */
	List<UserGroupDto> listByUser(String userId);

	/**
	 * 根据组织ID获取该组织及其子组织的用户组织关联列表
	 *
	 * @param groupId 组织ID
	 * @return
	 */
	List<UserGroupDto> listItAndChilldByGroup(String groupId);

	/**
	 * 切换用户到组织
	 *
	 * @param userId  用户ID
	 * @param groupId 组织ID
	 */
//	void changeUser2Group(String userId, String groupId);

	/**
	 * 根据组织id，用户id查询用户组织关系列表
	 *
	 * @param users
	 * @param gid
	 * @Return: java.util.List<com.baomibing.authority.dto.UserGroupDto>
	 */
	List<UserGroupDto> listByUsersAndGroup(Set<String> users, String gid);

	/**
	 * 根据组织id，用户id删除用户组织关系列表
	 *
	 * @param users
	 * @param gid
	 * @Return: void
	 */
	void deleteByGroupIdAndUsers(Set<String> users, String gid);

	/**
	 * 根据组织id删除用户组织关系列表
	 *
	 * @param gids
	 * @Return: void
	 */
	void deleteByGroups(Set<String> gids);

	/**
	 * 根据用户ID列表删除用户组织关系列表
	 *
	 * @param uids
	 */
	void deleteByUsers(Set<String> uids);

	/**
	 * 用户编号获取组织id
	 *
	 * @param userNo
	 * @Return: java.util.List<java.lang.String>
	 */
	String getGroupIdByUserNo(String userNo);

	/**
	 * 根据用户名列表获取用户组织列表
	 * @param userNos
	 * @return
	 */
	List<UserGroupDto> listByUserNos(Set<String> userNos);
}
