
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


import com.baomibing.authority.dto.PositionDto;
import com.baomibing.authority.dto.UserPositionDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

public interface SysUserPositionService extends MBaseService<UserPositionDto> {

	/**
	 * 根据用户ID，组织ID获取用户对应组织职位的角色列表
	 * @param userId 用户ID
	 * @param groupId 组织ID
	 * @return
	 */
	List<String> listPositionRoleIdsByUserAndGroup(String userId, String groupId);
	
	/**
	 * 根据用户ID，组织ID获取用户对应组织职位(约定：一个用户在一个组织只能有一个职位)
	 * @param userId 用户ID
	 * @param groupId 组织ID
	 * @return
	 */
	PositionDto getPositionByUserAndGroup(String userId, String groupId);

	/**
	 * 根据用户ID，组织ID获取用户对应组织职位
	 * @param users 用户ID
	 * @param pid 职位ID
	 * @return
	 */
//	List<UserPositionDto> getPositionByUsersAndPosition(Set<String> users, String pid);

	/**
	 * 根据用户ID，组织ID删除用户对应组织职位
	 * @param users 用户ID
	 * @param gid 组织ID
	 * @return
	 */
	void removePositionUsers(String gid, Set<String> users);

	/**
	 * 根据职位ID列表删除用户对应组织职位
	 * @param pids 职位ID列表
	 * @return
	 */
	void deleteByPositions(Set<String> pids);
	
	/**
	 * 根据用户ID列表删除用户对应的组织职位
	 * 
	 * @param uids 用户ID列表
	 */
	void deleteByUsers(Set<String> uids);
	
	/**
	 * 根据组织ID及用户列表删除用户组织下对应的所有职位
	 * 
	 * @param gid 组织ID
	 * @param users 用户ID列表
	 */
	void deleteByGroupAndUsers(String gid, Set<String> users);

	/**
	 * 查找职位对应的用户Id
	 *
	 * @param positionId 职位ID
	 * @Return: java.util.List<java.lang.String>
	 */
	List<String> listUserByPosition(String positionId);
}
