/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
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
