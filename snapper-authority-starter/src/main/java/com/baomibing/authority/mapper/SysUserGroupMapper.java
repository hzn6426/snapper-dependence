
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

import com.baomibing.authority.entity.SysUserGroup;
import com.baomibing.orm.base.MBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserGroupMapper extends MBaseMapper<SysUserGroup> {

	/**
	 * 根据用户ID查询用户组织列表
	 *
	 * @param userId 用户ID
	 * @return
	 */
	List<SysUserGroup> listByUser(@Param("userId") String userId);

	/**
	 * 根据组织ID查询用户组织列表
	 *
	 * @param groupId 组织ID
	 * @return
	 */
	List<SysUserGroup> listItAndChilldByGroup(@Param("groupId") String groupId);

	/**
	 * 用户编号获取组织id
	 *
	 * @param userNo
	 * @Return: java.util.List<java.lang.String>
	 */
	List<String> listGroupIdByUserNo(@Param("userNo") String userNo);

	/**
	 * 根据用户名列表查询用户组织列表
	 *
	 * @param userNos 用户名列表
	 * @return
	 */
	List<SysUserGroup> listByUserNos(@Param("userNos") List<String> userNos);
}
