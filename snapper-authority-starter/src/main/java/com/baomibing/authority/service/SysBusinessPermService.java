
package com.baomibing.authority.service;


import com.baomibing.authority.dto.BusinessPermDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

public interface SysBusinessPermService extends MBaseService<BusinessPermDto> {

	/**
	 * 保存或更新PERM
	 * @param perm
	 */
	void saveOrUpdatePerm(BusinessPermDto perm);
	/**
	 * 根据资源url和资源method获取业务权限
	 * 
	 * @param url    资源URL
	 * @param method 资源method
	 * @return
	 */
	BusinessPermDto getByUrlAndMethod(String url, String method);

	/**
	 * 根据权限动作获取业务权限
	 * 
	 * @param action 权限动作
	 * @return
	 */
	BusinessPermDto getByAction(String action);

	/**
	 * 根据Action列表获取对应的业务权限
	 * @param actions
	 * @return
	 */
	List<BusinessPermDto> listByActions(Set<String> actions);

	/**
	 * 根据资源ID获取对应的业务权限列表
	 * @param ids
	 * @return
	 */
	void deleteByButtons(Set<String> ids);
}
