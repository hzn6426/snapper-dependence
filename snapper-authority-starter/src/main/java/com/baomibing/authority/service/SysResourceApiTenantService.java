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


import com.baomibing.authority.constant.enums.ResourceTypeEnum;
import com.baomibing.authority.dto.ResourceApiTenantDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;


/**
 * 资源API服务
 * @author zening
 * @version 1.0.0
 */
public interface SysResourceApiTenantService extends MBaseService<ResourceApiTenantDto> {

	/**
	 * 根据http请求url,method获取资源API
	 * @param url http 请求url
	 * @param method http 请求method
	 * @return
	 */
	ResourceApiTenantDto getByHttpUrlAndMethod(String url, String method);
	
	/**
	 * 获取所有资源API
	 * @return
	 */
	List<ResourceApiTenantDto> listAll();
	
	/**
	 * 根据资源类型获取对应的API
	 * 
	 * @param resourceType 资源类型
	 * @return
	 */
	List<ResourceApiTenantDto> listByResourceType(ResourceTypeEnum resourceType);

	/**
	 * 根据资源ID和类型获取对应的API
	 *
	 * @param resourceId 资源ID
	 * @param resourceType 资源类型
	 * @return
	 */
	ResourceApiTenantDto getByResouce(String resourceId, ResourceTypeEnum resourceType);

	/**
	 * 根据资源IDS列表及资源类型获取对应的API信息
	 *
	 * @param resourceIds 资源ID列表
	 * @param resourceType 资源类型
	 * @return
	 */
	List<ResourceApiTenantDto> listByResource(Set<String> resourceIds, ResourceTypeEnum resourceType);

}
