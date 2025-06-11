
package com.baomibing.authority.service;


import com.baomibing.authority.dto.UserExceptEntrustDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;


/**
 * 用户委托排除服务
 * @author zening
 * @version 1.0.0
 */
public interface SysUserExceptEntrustService extends MBaseService<UserExceptEntrustDto> {

	/**
	 * 根据用户ID和业务授权ID获取委托的用户列表
	 * 
	 * @param orgId  组织ID(用户当前选择组织)
	 * @param userId 用户ID
	 * @param permId 权限ID
	 * @return
	 */
	List<String> listEntrustUserIdsByGroupAndUserAndPerm(String orgId, String userId, String permId);
	
	/**
	 * 根据用户ID和业务授权ID获取委托的用户编码列表
	 * 
	 * @param orgId  组织ID(用户当前选择组织)
	 * @param userId 用户ID
	 * @param permId 权限ID
	 * @return
	 */
	List<String> listEntrustUserCodesByGroupAndUserAndPerm(String orgId, String userId, String permId);

	/**
	 * 根据用户ID及业务权限ID删除该用户该功能委托的用户
	 * 
	 * @param orgId   组织ID(用户当前选择组织)
	 * @param userId  用户ID
	 */
	void deleteByGroupAndUserAndPerm(String orgId, String userId, String permId);
}
