
package com.baomibing.authority.service;



import com.baomibing.authority.dto.MenuDto;
import com.baomibing.authority.wrap.MenuWrap;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

public interface SysMenuService extends MBaseService<MenuDto> {

	/**
	 * 根据id批量删除菜单
	 * 
	 * @param idList 菜单id列表
	 */
//	void deleteByIdList(List<Integer> idList);


	/**
	 * 根据菜单ID列表获取资源菜单列表-关联资源
	 * 
	 * @param menuIds 菜单ID列表
	 * @return
	 */
	List<MenuDto> listAllResourceMenusByIds(Set<String> menuIds);

	/**
	 * 递归获取子节点对应的所有父节点
	 * 
	 * @param menuIds 菜单ID列表
	 * @return
	 */
	List<String> listAllParentByIds(Set<String> menuIds);

	/**
	 * 以树的方式加载所有菜单
	 *
	 * @return
	 */
	List<MenuWrap> treeAllMenus();


	/***
	 * 删除菜单
	 *
	 * @param ids 菜单ID列表
	 */
	void deleteMenu(Set<String> ids);

	/**
	 * 保存或更新菜单
	 *
	 * @param menu 待保存或更新的菜单
	 */
	void saveOrUpdateMenu(MenuDto menu);
}
