
package com.baomibing.authority.service;


import com.baomibing.authority.dto.ButtonDto;
import com.baomibing.core.base.MBaseService;
import com.baomibing.core.common.SearchResult;

import java.util.List;
import java.util.Set;

public interface SysButtonService extends MBaseService<ButtonDto> {




	/**
	 * 根据菜单ID列表删除按钮列表
	 * @param menuIds 菜单ID列表
	 * @return
	 */
	List<ButtonDto> listByMenus(Set<String> menuIds);


	/**
	 * 查询按钮信息及其对应的API信息
	 *
	 * @param v 查询条件封装
	 * @param pageNumber 页号
	 * @param pageSize 页数据量
	 * @return
	 */
	SearchResult<ButtonDto> searchButtonsAndApiByMenu(ButtonDto v, int pageNumber, int pageSize);

	/**
	 * 保存或更新按钮
	 *
	 * @param button 待保存或更新的按钮
	 */
	void saveOrUpdateButton(ButtonDto button);

	/**
	 * 删除按钮
	 *
	 * @param ids 带删除的按钮ID列表
	 */
	void deleteButtons(Set<String> ids);

	/**
	 * 关键字模糊匹配按钮
	 * @param keyWord 查询关键字
	 * @return
	 */
	List<ButtonDto> listByKeyWord(String keyWord);

	/**
	 * 根据ID获取按钮及API信息
	 * @param id 按钮ID
	 * @return
	 */
	ButtonDto getButtonAndApiById(String id);
}
