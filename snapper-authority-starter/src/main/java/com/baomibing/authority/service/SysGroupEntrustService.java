
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


import com.baomibing.authority.dto.GroupDto;
import com.baomibing.authority.dto.GroupEntrustDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

public interface SysGroupEntrustService extends MBaseService<GroupEntrustDto> {

	/**
	 * 根据用户ID和业务权限ID获取该用户委托的组织列表
	 * 
	 * @param orgId  组织ID(用户当前选择组织)
	 * @param userId 用户ID
	 * @param permId 业务权ID
	 * @return
	 */
	List<GroupDto> listEntrustGroupsByGroupAndUserAndPerm(String orgId, String userId, String permId);

	/**
	 * 根据用户ID和业务权限ID删除该用户对于该功能委托的组织
	 * 
	 * @param orgId  组织ID(用户当前选择组织)
	 * @param userId 用户ID
	 * @param permId 业务权限ID
	 */
	void deleteByGroupAndUserAndPerm(String orgId, String userId, String permId);

    /**
     * 根据用户 ID 组织 ID获取用户的组织委托
     * @param userIds 用户 ID
     * @param orgId  组织 ID
     * @return
     */
    List<GroupEntrustDto> listUserGroupEntrusts(Set<String> userIds, String orgId);

    /**
     * 根据用户 ID 列表及组织信息 删除用户的组织委托
     * @param orgId 组织 ID
     * @param userIds 用户 ID 列表
     */
    void deleteUserGroupEntrusts(String orgId, Set<String> userIds);

    /**
     * 根据用户 ID 及组织 ID 复制组织委托信息
     * @param uid 用户 ID
     * @param gid 组织 ID
     * @param toUserId 目标用户 ID
     */
    void doCopyUserGroupEntrusts(String uid, String gid, String toUserId);
}
