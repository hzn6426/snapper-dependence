
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

import com.baomibing.authority.entity.SysUserExceptEntrust;
import com.baomibing.orm.base.MBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 委托用户Mapper
 * @author zening
 * @date 2019-06-04 09:35:21
 * @version 1.0.0
 */
public interface SysUserExceptEntrustMapper extends MBaseMapper<SysUserExceptEntrust> {

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
	void deleteByGroupAndUserAndPerm(@Param("orgId") String orgId, @Param("userId") String userId, @Param("permId") String permId);
	
	
}
