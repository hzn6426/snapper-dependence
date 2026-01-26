
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


import com.baomibing.authority.dto.UserEntrustDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

public interface SysUserEntrustService extends MBaseService<UserEntrustDto> {

	/**
	 * 根据用户ID和业务授权ID获取委托的用户列表
	 * 
	 * @param orgId  组织ID(用户当前选择组织)
	 * @param userId 用户ID
	 * @param permId 权限ID
	 * @return
	 */
	List<String> listEntrustUserIdsByGroupAndUserAndPerm(String orgId, String userId, String permId);

	/**
	 * 根据用户ID和业务授权ID获取委托的用户编码列表
	 * 
	 * @param orgId  组织ID(用户当前选择组织)
	 * @param userId 用户ID
	 * @param permId 权限ID
	 * @return
	 */
	List<String> listEntrustUserCodesByGroupAndUserAndPerm(String orgId, String userId, String permId);

	/**
	 * 根据用户ID及业务权限ID删除该用户该功能委托的用户
	 * 
	 * @param orgId   组织ID(用户当前选择组织)
	 * @param userId  用户ID
	 * @param permId   权限ID
	 */
	void deleteByGroupAndUserAndPerm(String orgId, String userId, String permId);

    /**
     * 根据用户 ID 及组织 ID 获取用户的委托信息
     * @param userIds 用户 ID
     * @param orgId  组织 ID
     * @return
     */
    List<UserEntrustDto> listUserEntrusts(Set<String> userIds, String orgId);

    /**
     * 根据用户的 ID 列表及组织信息删除对应的用户委托
     * @param orgId
     * @param userIds
     */
    void deleteUserEntrusts(String orgId, Set<String> userIds);

    /**
     * 复制用户的委托
     * @param uid 用户 ID
     * @param gid 组织 ID
     * @param toUserId 目标用户
     */
    void doCopyUserEntrusts(String uid, String gid, String toUserId);
}
