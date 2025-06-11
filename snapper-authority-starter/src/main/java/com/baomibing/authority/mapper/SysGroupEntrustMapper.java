/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.mapper;

import com.baomibing.authority.entity.SysGroup;
import com.baomibing.authority.entity.SysGroupEntrust;
import com.baomibing.orm.base.MBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysGroupEntrustMapper extends MBaseMapper<SysGroupEntrust> {
	
	/**
	 * 根据用户ID和业务权限ID获取对应的委托组织列表
	 * 
	 * @param orgId  组织ID(用户当前选择组织)
	 * @param userId 用户ID
	 * @param permId 权限ID
	 * @return
	 */
	List<SysGroup> listEntrustGroupsByGroupAndUserAndPerm(@Param("orgId") String orgId, @Param("userId") String userId,
			@Param("permId") String permId);
	
	/**
	 * 根据用户ID和业务权限ID删除该用户对于该功能的委托的组织
	 * 
	 * @param orgId  组织ID(用户当前选择组织)
	 * @param userId 用户ID
	 * @param permId 权限ID
	 */
	void deleteByGroupAndUserAndPerm(@Param("orgId") String orgId, @Param("userId") String userId, @Param("permId") String permId);
}
