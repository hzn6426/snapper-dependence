/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.mapper;

import com.baomibing.authority.entity.SysPositionRole;
import com.baomibing.authority.entity.SysRole;
import com.baomibing.orm.base.MBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysPositionRoleMapper extends MBaseMapper<SysPositionRole> {

	/**
	 * 根据职位ID获取用户角色列表
	 * 
	 * @param positionId 职位ID
	 * @return
	 */
	List<SysRole> listRolesByPosition(@Param("positionId") String positionId);
}
