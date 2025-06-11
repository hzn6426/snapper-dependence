/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.mapper;

import com.baomibing.authority.entity.SysPosition;
import com.baomibing.authority.entity.SysUserPosition;
import com.baomibing.orm.base.MBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserPositionMapper extends MBaseMapper<SysUserPosition> {

	/**
	 * 根据用户ID，组织ID获取用户组织职位对应的角色列表
	 * 
	 * @param userId  用户ID
	 * @param groupId 组织ID
	 * @return
	 */
	List<String> listPositionRoleIdsByUserAndGroup(@Param("userId") String userId, @Param("groupId") String groupId);

	/**
	 * 根据用户ID，组织ID获取用户组织对应的职位
	 * 
	 * @param userId  用户ID
	 * @param groupId 组织ID
	 * @return
	 */
	List<SysPosition> listPositionByUserAndGroup(@Param("userId") String userId, @Param("groupId") String groupId);
}
