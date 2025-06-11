/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.mapper;

import com.baomibing.authority.entity.SysUserBusinessPerm;
import com.baomibing.orm.base.MBaseMapper;
import org.apache.ibatis.annotations.Param;

public interface SysUserBusinessPermMapper extends MBaseMapper<SysUserBusinessPerm> {

	/**
	 * 根据用户及请求的方法和URL获取业务权限范围
	 * 
	 * @param orgId  组织ID(用户当前选择组织)
	 * @param userId 用户ID
	 * @param method 请求方法
	 * @param url    请求URL
	 * @return
	 */
	String getUserBusinessPermScopeByMethodAndUrl(@Param("orgId") String orgId, @Param("userId") String userId,
			@Param("method") String method, @Param("url") String url);

	/**
	 * 根据用户及权限动作Action获取业务权限范围
	 * 
	 * @param orgId  组织ID(用户当前选择组织)
	 * @param userId 用户ID
	 * @param action 业务权限动作
	 * @return
	 */
	String getUserBusinessPermScopeByAction(@Param("orgId") String orgId, @Param("userId") String userId,
			@Param("action") String action);

}
