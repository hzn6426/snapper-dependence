
/*
 * Copyright (c) 2020-2025, zening (316279828@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

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
