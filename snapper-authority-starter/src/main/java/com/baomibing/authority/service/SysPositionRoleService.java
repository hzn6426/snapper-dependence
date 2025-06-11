
package com.baomibing.authority.service;


import com.baomibing.authority.dto.PositionRoleDto;
import com.baomibing.authority.dto.RoleDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

public interface SysPositionRoleService extends MBaseService<PositionRoleDto> {

	/**
	 * 根据职位ID获取角色列表
	 * 
	 * @param positionId
	 * @return
	 */
	List<RoleDto> listRolesByPosition(String positionId);

	/**
	 * 保存职位角色（多个角色）
	 * 
	 * @param positionId 职位ID
	 * @param roles      角色ID列表
	 */
	void savePositionRoles(String positionId, Set<String> roles);

	/**
	 * 根据职位删除对应的职位角色关系
	 * 
	 * @param positionId 职位ID
	 */
	void deleteByPosition(String positionId);

	/**
	 * 根据角色ID列表删除角色职位关联
	 *
	 * @param roles 角色ID列表
	 */
	void deleteByRoles(Set<String> roles);
}
