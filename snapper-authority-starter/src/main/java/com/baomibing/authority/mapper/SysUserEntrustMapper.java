/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.mapper;

import com.baomibing.authority.entity.SysUserEntrust;
import com.baomibing.orm.base.MBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserEntrustMapper extends MBaseMapper<SysUserEntrust> {

	/**
	 * 根据用户ID和业务权限ID获取用户委托的用户ID列表
	 * 
	 * @param orgId  组织ID(用户当前选择组织)
	 * @param userId 用户ID
	 * @param permId 权限ID
	 * @return
	 */
	List<String> listEntrustUserIdsByGroupAndUserAndPerm(@Param("orgId") String orgId, @Param("userId") String userId,
			@Param("permId") String permId);

	/**
	 * 根据用户ID和业务权限ID获取用户委托的用户名列表
	 * 
	 * @param orgId  组织ID(用户当前选择组织)
	 * @param userId 用户ID
	 * @param permId 权限ID
	 * @return
	 */
	List<String> listEntrustUserCodesByGroupAndUserAndPerm(@Param("orgId") String orgId, @Param("userId") String userId,
			@Param("permId") String permId);

	/**
	 * 根据用户ID及业务权限ID删除该用户该功能委托的用户
	 * 
	 * @param orgId  组织ID(用户当前选择组织)
	 * @param userId 用户ID
	 * @param permId 权限ID
	 */
	void deleteByGroupAndUserAndPerm(@Param("orgId") String orgId, @Param("userId") String userId,
			@Param("permId") String permId);

}
