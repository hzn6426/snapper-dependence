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
import com.baomibing.authority.dto.ResourceApiTenantDto;
import com.baomibing.authority.entity.SysResourceApiTenant;
import com.baomibing.authority.mapper.SysResourceApiTenantMapper;
import com.baomibing.authority.service.SysResourceApiTenantService;
import com.baomibing.authority.state.CommonState;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 资源API服务实现类
 * @author zening
 * @version 1.0.0
 */
@Service
public class SysResourceApiTenantServiceImpl extends MBaseServiceImpl<SysResourceApiTenantMapper, SysResourceApiTenant, ResourceApiTenantDto> implements SysResourceApiTenantService {

	@Override
	public ResourceApiTenantDto getByHttpUrlAndMethod(String url, String method) {
		if (Checker.beEmpty(url) || Checker.beEmpty(method)) return null;
		SysResourceApiTenant api = this.baseMapper.selectOne(lambdaQuery().eq(SysResourceApiTenant::getReqUrl, url).eq(SysResourceApiTenant::getReqMethod, method));
		return Checker.beNull(api) ? null : mapper2v(api);
	}

	@Override
	public List<ResourceApiTenantDto> listAll() {
		LambdaQueryWrapper<SysResourceApiTenant> wrapper = lambdaQuery();
		wrapper.eq(SysResourceApiTenant::getState, CommonState.ACTIVE);
		List<SysResourceApiTenant> list = this.baseMapper.selectList(wrapper);
		return Checker.beEmpty(list) ? Lists.newArrayList() : mapper(list);
	}

	@Override
	public List<ResourceApiTenantDto> listByResourceType(ResourceTypeEnum resourceType) {
		if (Checker.beNull(resourceType)) {
			return Lists.newArrayList();
		}
		LambdaQueryWrapper<SysResourceApiTenant> wrapper = lambdaQuery();
		wrapper.eq(SysResourceApiTenant::getResourceType, resourceType);
		return mapper(this.baseMapper.selectList(wrapper));
	}

	@Override
	public ResourceApiTenantDto getByResouce(String resourceId, ResourceTypeEnum resourceType) {
		if (Checker.beEmpty(resourceId) || Checker.beNull(resourceType)) return null;
		List<SysResourceApiTenant> apis = this.baseMapper.selectList(lambdaQuery().eq(SysResourceApiTenant::getResourceId, resourceId)
				.eq(SysResourceApiTenant::getResourceType,resourceType.name()));
		return Checker.beEmpty(apis) ? null : mapper2v(apis.get(0));
	}

	@Override
	public List<ResourceApiTenantDto> listByResource(Set<String> resourceIds, ResourceTypeEnum resourceType) {
		if (Checker.beEmpty(resourceIds) || Checker.beNull(resourceType)) {
			return Lists.newArrayList();
		}
		List<SysResourceApiTenant> apis = this.baseMapper.selectList(lambdaQuery().in(SysResourceApiTenant::getResourceId, resourceIds)
				.eq(SysResourceApiTenant::getResourceType,resourceType.name()));
		return Checker.beEmpty(apis) ? Lists.newArrayList() : mapper(apis);
	}
}
