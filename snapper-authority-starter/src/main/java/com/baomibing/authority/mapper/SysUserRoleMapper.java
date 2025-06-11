/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.mapper;

import com.baomibing.authority.entity.SysRole;
import com.baomibing.authority.entity.SysUser;
import com.baomibing.authority.entity.SysUserRole;
import com.baomibing.orm.base.MBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户角色映射
 * 
 * @author zening
 * @date 2018年2月5日 下午1:48:38
 * @version 1.0.0
 */
public interface SysUserRoleMapper extends MBaseMapper<SysUserRole> {

	/**
	 * 删除用户id为<code>userId</code>的组织对应角色关系信息
	 *
	 * @param orgId  组织ID
	 * @param userId 用户ID
	 * @return
	 */
	int deleteByGroupAndUser(@Param("orgId") String orgId, @Param("userId") String userId);

	/**
	 * 根据组织ID和用户ID列表删除用户角色关系
	 *
	 * @param orgId   组织ID
	 * @param userIds 用户ID列表
	 */
	void deleteByGroupAndUsers(@Param("orgId") String orgId, @Param("userIds") Set<String> userIds);

	/**
	 * 根据用户名和组织列出用户所有角色列表
	 * 
	 * @param orgId    组织ID
	 * @param userName 用户名
	 * @return
	 */
	List<SysRole> listRolesByGroupAndUserName(@Param("orgId") String orgId, @Param("userName") String userName);

	/**
	 * 根据组织和用户列出角色列表
	 * 
	 * @param orgId  组织ID
	 * @param userId 用户ID
	 * @return
	 */
	List<SysRole> listRolesByGroupAndUser(@Param("orgId") String orgId, @Param("userId") String userId);

	/**
	 * 根据用户id列表和角色id列表封装的map删除用户角色关系映射
	 * 
	 * @param map
	 * @return
	 */
	int deleteByMapUserIdAndRoleIdList(Map<String, Object> map);

	/**
	 * 根据角色id列表和用户id列表封装的map删除用户角色关系映射
	 * 
	 * @param map
	 * @return
	 */
	int deleteByMapRoleIdAndUserIdList(Map<String, Object> map);

	/**
	 * 根据组织ID和角色ID查询用户
	 *
	 * @param orgId  组织ID
	 * @param roleId 角色ID
	 * @return
	 */
	List<SysUser> listUserByGroupAndRole(@Param("orgId") String orgId, @Param("roleId") String roleId);

	/**
	 * 删除角色id为<code>roleId</code>的组织用户角色关系信息
	 * 
	 * @param roleId
	 * @return
	 */
	int deleteByGroupAndRole(@Param("orgId") String orgId, @Param("roleId") String roleId);

	/**
	 * 根据角色删除对应的用户组织角色关系
	 * 
	 * @param roleId 角色ID
	 * @return
	 */
	int deleteByRole(@Param("roleId") String roleId);

	/**
	 * 删除角色ID列表对应的用户组织角色关系信息
	 * 
	 * @param roleIds 角色列表
	 * @return
	 */
	int deleteByGroupAndRoles(@Param("orgId") String orgId, @Param("roleIdList") Set<String> roleIds);

	/**
	 * 删除角色ID及组织列表对应的用户组织关系
	 * 
	 * @param roleId   角色ID
	 * @param groupIds 组织ID列表
	 * @return
	 */
	int deleteByRoleAndGroups(@Param("roleId") String roleId, @Param("groupIdList") Set<String> groupIds);

	/**
	 * 根据角色ID获取与组织关联的用户ID信息
	 * 
	 * @param roleId 角色ID
	 * @return
	 */
	List<SysUser> listGroupUsersByRole(@Param("rid") String roleId);
}
