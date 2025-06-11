
package com.baomibing.authority.service;



import com.baomibing.authority.dto.UserDto;
import com.baomibing.authority.dto.UserGroupDto;
import com.baomibing.authority.dto.UserUsetDto;
import com.baomibing.authority.dto.UsetDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

/**
 * @author : zening
 * @version : 1.0.0
 */
public interface SysUserUsetService extends MBaseService<UserUsetDto> {
    
    /**
     * 根据用户ID列表删除对应的用户与组关系
     * @param userIds
     */
    void deleteByUsers(Set<String> userIds);
    
    /**
     * 根据用户组ID列表删除对应的用户与组关系
     * @param usetIds
     */
    void deleteByUsets(Set<String> usetIds);

	/**
	 * 根据用户组ID获取与组织关联的用户ID信息
	 * 
	 * @param usetId 角色ID
	 * @return
	 */
	List<UserDto> listGroupUsersByUset(String usetId);

	/**
	 * 根据用户组分配用户列表
	 *
	 * @param usetId      用户组ID
	 * @param userGroups 组织及用户列表
	 */
	void saveFromUset(String usetId, List<UserGroupDto> userGroups);

	/**
	 * 根据组织ID及用户ID获取对应的用户组
	 * 
	 * @param orgId  组织ID
	 * @param userId 用户ID
	 * @return
	 */
	List<UsetDto> listUsetsByGroupAndUser(String orgId, String userId);

	/**
	 * 根据组织ID及用户ID列表获取对应的用户组列表
	 *
	 * @param orgId
	 * @param userIds
	 * @return
	 */
	List<UserUsetDto> listByGroupAndUsers(String orgId, Set<String> userIds);

	/**
	 * 根据用户及其组织ID获取用户对应的角色列表
	 * @param userId
	 * @param userGroupId
	 * @return
	 */
	List<String> listUsetRoleIdsByUserAndGroup(String userId, String userGroupId);
}
