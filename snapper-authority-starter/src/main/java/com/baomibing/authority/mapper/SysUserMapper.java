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

import com.baomibing.authority.dto.UserDto;
import com.baomibing.authority.entity.SysUser;
import com.baomibing.orm.base.MBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface SysUserMapper extends MBaseMapper<SysUser> {


	List<SysUser> searchByCondition(@Param("user") UserDto user, @Param("limit") int limit, @Param("offset") int offset);

	int countByCondition(@Param("user") UserDto user);

	/**
	 * 根据组织获取用户信息
	 *
	 * @param user 用户信息
	 * @param limit   偏移量
	 * @param offset  当前页数
	 * @return
	 */
	List<SysUser> searchByGroup(@Param("user") UserDto user, @Param("limit") int limit,
			@Param("offset") int offset);

	/**
	 * 根据组织获取用户信息的数量
	 *
	 * @param user 用户信息
	 * @return
	 */
	int countByGroup(@Param("user") UserDto user);

	/**
	 * 获取未分配组织的用户列表
	 * 
	 * @param limit
	 * @param offset
	 * @return
	 */
	List<SysUser> listForNotAssignGroup(@Param("limit") int limit, @Param("offset") int offset);

	/**
	 * 获取未分配组织的用户条数
	 * 
	 * @return
	 */
	int countForNotAssignGroup();

	/**
	 * 根据用户工号、姓名、电话、邮箱获取用户信息的数量
	 *
	 * @param gid 机构编号
	 * @return
	 */
	List<SysUser> listUsersByGroupId(@Param("gid") String gid);

	/**
	 * 通过用户标签和关键字来匹配
	 *
	 * @param userTag 用户标签
	 * @param KeyWord 关键字
	 * @Return: java.util.List<com.baomibing.authority.entity.SysUser>
	 */
	List<SysUser> listByKeyWordAndType(@Param("userTag") String userTag, @Param("KeyWord") String KeyWord);

	/**
	 * 获取所有建立组织关系的成员列表
	 * 
	 * @return java.util.List<com.baomibing.authority.entity.SysUser>
	 */
	List<SysUser> listAllGroupUsers();

	/**
	 * 获取所有建立组织关系的对应TAG的成员列表
	 *
	 * @return java.util.List<com.baomibing.authority.entity.SysUser>
	 */
	List<SysUser> listAllGroupUsersByTag(@Param("userTag") String userTag);

	/**
	 * 通过用户标签和关键字来匹配对应分公司的用户
	 *
	 * @param userTag
	 * @param KeyWord
	 * @param companyId
	 * @return
	 */
	List<SysUser> listByKeyWordAndTypeOfCompany(@Param("userTag") String userTag, @Param("KeyWord") String KeyWord, @Param("companyId") String companyId);

	List<SysUser> listByUserCnNames(@Param("cnNames") Set<String> cnNames);

}
