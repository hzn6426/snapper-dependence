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


import com.baomibing.authority.entity.SysMenuTenant;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 系统菜单映射
 * @author zening
 * @date 2018年3月16日 上午11:09:43
 * @version 1.0.0
 */
public interface SysMenuTenantMapper extends BaseMapper<SysMenuTenant> {

	
	/**
	 * 根据菜单ID列表获取资源菜单列表-关联资源
	 * @param menuIds 菜单ID列表
	 * @return
	 */
	List<SysMenuTenant> listAllResourceMenusByIds(@Param("menuIds") Set<String> menuIds);

	/**
	 * 递归获取子节点对应的所有父节点
	 * 
	 * @param menuIds 菜单ID列表
	 * @return
	 */
	List<String> listAllParentByIds(@Param("menuIds") Set<String> menuIds);

	/**
	 * 获取所有可授权的菜单列表
	 * @return
	 */
	List<SysMenuTenant> listAllMenusForGrant();
}
