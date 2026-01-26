
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

import com.baomibing.authority.entity.SysGroup;
import com.baomibing.authority.entity.SysGroupExceptEntrust;
import com.baomibing.orm.base.MBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户组织委托Mapper
 * @author zening
 * @date 2019-06-04 09:37:09
 * @version 1.0.0
 */
public interface SysGroupExceptEntrustMapper extends MBaseMapper<SysGroupExceptEntrust> {
	
	/**
	 * 根据用户ID和业务权限ID获取对应的排除委托组织列表
	 * 
	 * @param orgId  组织ID(用户当前选择组织)
	 * @param userId 用户ID
	 * @param permId 权限ID
	 * @return
	 */
	List<SysGroup> listEntrustGroupsByGroupAndUserAndPerm(@Param("orgId") String orgId, @Param("userId") String userId,
														  @Param("permId") String permId);
	
	/**
	 * 根据用户ID和业务权限ID删除该用户对于该功能的排除的委托组织
	 * 
	 * @param orgId  组织ID(用户当前选择组织)
	 * @param userId 用户ID
	 * @param permId 权限ID
	 */
	void deleteByGroupAndUserAndPerm(@Param("orgId") String orgId, @Param("userId") String userId, @Param("permId") String permId);
}
