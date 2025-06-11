
package com.baomibing.authority.service;



import com.baomibing.authority.dto.GroupDto;
import com.baomibing.authority.dto.GroupExceptEntrustDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;


/**
 * 组织委托服务类
 * @author zening
 * @version 1.0.0
 */
public interface SysGroupExceptEntrustService extends MBaseService<GroupExceptEntrustDto> {

	/**
	 * 根据用户ID和业务权限ID获取该用户委托的组织列表
	 * 
	 * @param orgId  组织ID(用户当前选择组织)
	 * @param userId 用户ID
	 * @param permId 业务权ID
	 * @return
	 */
	List<GroupDto> listEntrustGroupsByGroupAndUserAndPerm(String orgId, String userId, String permId);
	
	/**
	 * 根据用户ID和业务权限ID删除该用户对于该功能委托的组织
	 * 
	 * @param orgId  组织ID(用户当前选择组织)
	 * @param userId 用户ID
	 * @param permId 业务权限ID
	 */
	void deleteByGroupAndUserAndPerm(String orgId, String userId, String permId);
}
