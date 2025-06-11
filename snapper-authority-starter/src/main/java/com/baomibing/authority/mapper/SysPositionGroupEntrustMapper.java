/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.mapper;

import com.baomibing.authority.entity.SysGroup;
import com.baomibing.authority.entity.SysPositionGroupEntrust;
import com.baomibing.orm.base.MBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysPositionGroupEntrustMapper extends MBaseMapper<SysPositionGroupEntrust> {

	/**
	 * 根据职位ID获取职位委托组织列表
	 * @param positionId 职位ID
	 * @return
	 */
	List<SysGroup> listEntrustGroupsByPosition(@Param("positionId") String positionId);
}
