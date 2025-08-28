
package com.baomibing.authority.service;


import com.baomibing.authority.dto.GroupDto;
import com.baomibing.authority.dto.PositionGroupEntrustDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

public interface SysPositionGroupEntrustService extends MBaseService<PositionGroupEntrustDto> {

	/**
	 * 根据职位ID获取职位对应的组织委托
	 * 
	 * @param positionId 职位ID
	 * @return
	 */
	List<GroupDto> listEntrustGroupsByPosition(String positionId);

	/**
	 * 根据职位ID删除职位对应的组织委托
	 * 
	 * @param positionId 职位ID
	 */
	void deleteByPosition(String positionId);

	/**
	 * 根据职位ID集合删除职位对应的组织委托
	 *
	 * @param positionIds 职位ID列表
	 */
	void deleteByPositions(Set<String> positionIds);
}
