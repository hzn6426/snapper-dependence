
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

import com.baomibing.authority.entity.SysUser;
import com.baomibing.authority.entity.SysUserUset;
import com.baomibing.authority.entity.SysUset;
import com.baomibing.orm.base.MBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface SysUserUsetMapper extends MBaseMapper<SysUserUset> {

	/**
	 * 根据用户组ID获取用户对应的组织及用户信息
	 * 
	 * @param usetId
	 * @return
	 */
	List<SysUser> listGroupUsersByUset(@Param("usetId") String usetId);

	/**
	 * 根据组织和用户列出用户组列表
	 * 
	 * @param orgId  组织ID
	 * @param userId 用户ID
	 * @return
	 */
	List<SysUset> listUsetsByGroupAndUser(@Param("orgId") String orgId, @Param("userId") String userId);

	/**
	 * 根据用户ID，组织ID获取用户组对应的角色列表
	 *
	 * @param userId  用户ID
	 * @param groupId 组织ID
	 * @return
	 */
	List<String> listUsetRoleIdsByUserAndGroup(@Param("userId") String userId, @Param("groupId") String groupId);
}
