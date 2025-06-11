/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
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
