
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



import com.baomibing.authority.dto.UserDto;
import com.baomibing.authority.dto.UserGroupDto;
import com.baomibing.authority.dto.UserUsetDto;
import com.baomibing.authority.dto.UsetDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

/**
 * @author : zening
 * @version : 1.0.0
 */
public interface SysUserUsetService extends MBaseService<UserUsetDto> {
    
    /**
     * 根据用户ID列表删除对应的用户与组关系
     * @param userIds
     */
    void deleteByUsers(Set<String> userIds);
    
    /**
     * 根据用户组ID列表删除对应的用户与组关系
     * @param usetIds
     */
    void deleteByUsets(Set<String> usetIds);

	/**
	 * 根据用户组ID获取与组织关联的用户ID信息
	 * 
	 * @param usetId 角色ID
	 * @return
	 */
	List<UserDto> listGroupUsersByUset(String usetId);

	/**
	 * 根据用户组分配用户列表
	 *
	 * @param usetId      用户组ID
	 * @param userGroups 组织及用户列表
	 */
	void saveFromUset(String usetId, List<UserGroupDto> userGroups);

	/**
	 * 根据组织ID及用户ID获取对应的用户组
	 * 
	 * @param orgId  组织ID
	 * @param userId 用户ID
	 * @return
	 */
	List<UsetDto> listUsetsByGroupAndUser(String orgId, String userId);

	/**
	 * 根据组织ID及用户ID列表获取对应的用户组列表
	 *
	 * @param orgId
	 * @param userIds
	 * @return
	 */
	List<UserUsetDto> listByGroupAndUsers(String orgId, Set<String> userIds);

	/**
	 * 根据用户及其组织ID获取用户对应的角色列表
	 * @param userId
	 * @param userGroupId
	 * @return
	 */
	List<String> listUsetRoleIdsByUserAndGroup(String userId, String userGroupId);

    /**
     * 删除用户组中对应组织的用户列表
     *
     * @param orgId 用户组织 ID
     * @param userIds 用户 ID 列表
     */
    void deleteByGroupUsers( String orgId, Set<String> userIds);

    /**
     * 复制组织用户的用户组
     * @param uid  待复制的用户
     * @param gid 待复制的用户组织
     * @param toUserId 目标用户
     */
    void doCopyUserUset(String uid, String gid, String toUserId);
}
