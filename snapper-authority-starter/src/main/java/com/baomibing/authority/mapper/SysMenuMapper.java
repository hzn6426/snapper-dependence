/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.mapper;


import com.baomibing.authority.entity.SysMenu;
import com.baomibing.orm.base.MBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 系统菜单映射
 * @author zening
 * @date 2018年3月16日 上午11:09:43
 * @version 1.0.0
 */
public interface SysMenuMapper extends MBaseMapper<SysMenu> {

	
	/**
	 * 根据菜单ID列表获取资源菜单列表-关联资源
	 * @param menuIds 菜单ID列表
	 * @return
	 */
	List<SysMenu> listAllResourceMenusByIds(@Param("menuIds") Set<String> menuIds);

	/**
	 * 递归获取子节点对应的所有父节点
	 * 
	 * @param menuIds 菜单ID列表
	 * @return
	 */
	List<String> listAllParentByIds(@Param("menuIds") Set<String> menuIds);
}
