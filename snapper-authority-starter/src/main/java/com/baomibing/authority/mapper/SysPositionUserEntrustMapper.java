/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.mapper;

import com.baomibing.authority.entity.SysPositionUserEntrust;
import com.baomibing.authority.entity.SysUser;
import com.baomibing.orm.base.MBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysPositionUserEntrustMapper extends MBaseMapper<SysPositionUserEntrust> {

	/**
	 * 根据职位ID获取职位委托的组织中用户列表
	 * 
	 * @param positionId 职位ID
	 * @return
	 */
	List<SysUser> listEntrustUsersByPosition(@Param("positionId") String positionId);
}
