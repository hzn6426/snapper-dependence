
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
