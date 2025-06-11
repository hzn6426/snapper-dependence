
package com.baomibing.authority.service;


import com.baomibing.authority.constant.enums.BusinessPermScopeEnum;
import com.baomibing.authority.dto.UserBusinessPermDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

public interface SysUserBusinessPermService extends MBaseService<UserBusinessPermDto> {

	/**
	 * 根据组织及用户及权限动作获取业务权限范围
	 * 
	 * @param orgId  组织ID(用户当前选择组织)
	 * @param userId 用户ID
	 * @param action 业务权限
	 * @return
	 */
//	BusinessPermScopeEnum getUserBusinessPermScopeByAction(String orgId, String userId, String action);

	/**
	 * 根据组织及用户及请求的方法和URL获取业务权限范围
	 * 
	 * @param orgId  组织ID(用户当前选择组织)
	 * @param userId 用户ID
	 * @param method 请求方法
	 * @param url    请求URL
	 * @return
	 */
//	BusinessPermScopeEnum getUserBusinessByMethodAndUrl(String orgId, String userId, String method, String url);

	/**
	 * 根据组织及用户及业务权限ID获取业务权限范围
	 * 
	 * @param orgId  组织ID(用户当前选择组织)
	 * @param userId 用户ID
	 * @param permId 业务权限ID
	 * @return
	 */
	BusinessPermScopeEnum getUserBusiness(String orgId, String userId, String permId);

	/**
	 * 保存用户业务权限信息
	 * 
	 * @param perm 用户业务权限
	 */
	void saveUserBusinessPerm(UserBusinessPermDto perm);

	/**
	 * 根据组织及权限ID，用户ID获取用户委托的用户列表
	 * 
	 * @param orgId  组织ID(用户当前选择组织)
	 * @param userId 用户ID
	 * @param permId 权限ID
	 * @return
	 */
	List<String> listUserEntrustIdsByPerm(String orgId, String userId, String permId);

	/**
	 * 根据组织及权限ID,用户ID 获取用户委托的排除用户列表
	 * @param orgId 组织ID(用户当前选择组织)
	 * @param userId 用户ID
	 * @param permId 权限ID
	 * @return
	 */
	List<String> listUserExceptEntrustIdsByPerm(String orgId, String userId, String permId);

	/**
	 * 根据组织及权限ID，用户ID获取用户委托的组织（部门）列表
	 * 
	 * @param orgId  组织ID(用户当前选择组织)
	 * @param userId 用户ID
	 * @param permId 权限ID
	 * @return
	 */
	List<String> listGroupEntrustIdsByPerm(String orgId, String userId, String permId);

	/**
	 * 根据组织及权限ID，用户ID获取用户委托的排除组织（部门）列表
	 *
	 * @param orgId  组织ID(用户当前选择组织)
	 * @param userId 用户ID
	 * @param permId 权限ID
	 * @return
	 */
	List<String> listGroupExceptEntrustIdsByPerm(String orgId, String userId, String permId);

	/**
	 * 根据组织及用户ID列表删除对应的用户委托组织关系列表
	 * 
	 * @param orgId 组织ID(用户当前选择组织)
	 * @param uids  用户ID列表
	 */
	void deleteByGroupAndUsers(String orgId, Set<String> uids);

	/**
	 * 根据用户ID列表删除对应的用户委托组织关系列表
	 * 
	 * @param uids 用户ID列表
	 */
	void deleteByUsers(Set<String> uids);

	/**
	 * 根据组织及用户和业务权限删除用户对应的业务权限
	 * 
	 * @param orgId  组织ID(用户当前选择组织)
	 * @param userId 用户ID
	 * @param permId 权限ID
	 */
	void deleteGroupUserPerm(String orgId, String userId, String permId);
}
