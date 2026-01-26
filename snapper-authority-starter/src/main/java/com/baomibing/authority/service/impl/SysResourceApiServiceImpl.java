
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

package com.baomibing.authority.service.impl;

import com.baomibing.authority.constant.enums.ResourceTypeEnum;
import com.baomibing.authority.dto.ResourceApiDto;
import com.baomibing.authority.entity.SysResourceApi;
import com.baomibing.authority.mapper.SysResourceApiMapper;
import com.baomibing.authority.service.SysResourceApiService;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 资源API服务实现类
 * 
 * @author zening
 * @since 1.0.0
 */
@Service
public class SysResourceApiServiceImpl extends MBaseServiceImpl<SysResourceApiMapper, SysResourceApi, ResourceApiDto> implements SysResourceApiService {


	@Override
	public List<ResourceApiDto> listByResourceType(ResourceTypeEnum resourceType) {
		if (Checker.beNull(resourceType)) {
			return Lists.newArrayList();
		}
		QueryWrapper<SysResourceApi> wrapper = new QueryWrapper<>();
		wrapper.eq("resource_type", resourceType);
		return mapper(this.baseMapper.selectList(wrapper));
	}

	@Override
	public ResourceApiDto getByResouce(String resourceId, ResourceTypeEnum resourceType) {
		if (Checker.beEmpty(resourceId) || Checker.beNull(resourceType)) return null;
		List<SysResourceApi> apis = this.baseMapper.selectList(lambdaQuery().eq(SysResourceApi::getResourceId, resourceId)
				.eq(SysResourceApi::getResourceType,resourceType.name()));
		return Checker.beEmpty(apis) ? null : mapper2v(apis.get(0));
	}

	@Override
	public List<ResourceApiDto> listByResource(Set<String> resourceIds, ResourceTypeEnum resourceType) {
		if (Checker.beEmpty(resourceIds) || Checker.beNull(resourceType)) {
			return Lists.newArrayList();
		}
		List<SysResourceApi> apis = this.baseMapper.selectList(lambdaQuery().in(SysResourceApi::getResourceId, resourceIds)
				.eq(SysResourceApi::getResourceType,resourceType.name()));
		return Checker.beEmpty(apis) ? Lists.newArrayList() : mapper(apis);
	}

}
