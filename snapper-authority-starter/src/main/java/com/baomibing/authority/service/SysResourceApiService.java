
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
import com.baomibing.authority.dto.ResourceApiDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

public interface SysResourceApiService extends MBaseService<ResourceApiDto> {

	/**
	 * 根据http请求url,method获取资源API
	 * 
	 * @param url    http 请求url
	 * @param method http 请求method
	 * @return
	 */
//	ResourceApiDto getByHttpUrlAndMethod(String url, String method);

	/**
	 * 获取所有资源API
	 * 
	 * @return
	 */
//	List<ResourceApiDto> listAll();

	/**
	 * 根据资源类型获取对应的API
	 * 
	 * @param resourceType 资源类型
	 * @return
	 */
	List<ResourceApiDto> listByResourceType(ResourceTypeEnum resourceType);

	/**
	 * 根据资源ID和类型获取对应的API
	 *
	 * @param resourceId 资源ID
	 * @param resourceType 资源类型
	 * @return
	 */
	ResourceApiDto getByResouce(String resourceId, ResourceTypeEnum resourceType);

	/**
	 * 根据资源IDS列表及资源类型获取对应的API信息
	 *
	 * @param resourceIds 资源ID列表
	 * @param resourceType 资源类型
	 * @return
	 */
	List<ResourceApiDto> listByResource(Set<String> resourceIds, ResourceTypeEnum resourceType);


}
