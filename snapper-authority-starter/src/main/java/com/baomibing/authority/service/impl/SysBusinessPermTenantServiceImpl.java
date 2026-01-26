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

import com.baomibing.authority.dto.BusinessPermTenantDto;
import com.baomibing.authority.entity.SysBusinessPermTenant;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.mapper.SysBusinessPermTenantMapper;
import com.baomibing.authority.service.SysBusinessPermTenantService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;


/**
 * 业务权限服务实现类
 * @author zening
 * @version 1.0.0
 */
@Service
public class SysBusinessPermTenantServiceImpl extends
		MBaseServiceImpl<SysBusinessPermTenantMapper, SysBusinessPermTenant, BusinessPermTenantDto> implements SysBusinessPermTenantService {

	@Override
	public BusinessPermTenantDto getByUrlAndMethod(String url, String method) {
		if (Checker.beEmpty(url) || Checker.beEmpty(method)) return null;
		LambdaQueryWrapper<SysBusinessPermTenant> wrapper = lambdaQuery();
		wrapper.eq(SysBusinessPermTenant::getReqUrl, url).eq(SysBusinessPermTenant::getReqMethod, method);
		List<BusinessPermTenantDto> list = mapper(this.baseMapper.selectList(wrapper));
		return Checker.beEmpty(list) ? null : list.get(0);
	}

	@Override
	public BusinessPermTenantDto getByAction(String action) {
		if (Checker.beEmpty(action)) return null;
		List<BusinessPermTenantDto> list = mapper(this.baseMapper.getByAction(action));
		BusinessPermTenantDto perm =  Checker.beEmpty(list) ? null : list.get(0);
		if (Checker.beNull(perm)) {
			return null;
		}
		if (Checker.beNotEmpty(perm.getActionRef())) {
			LambdaQueryWrapper<SysBusinessPermTenant> wrapper = lambdaQuery();
			wrapper.eq(SysBusinessPermTenant::getPermAction, perm.getActionRef());
			List<BusinessPermTenantDto> targets = mapper(baseMapper.selectList(wrapper));
			return Checker.beEmpty(targets) ? null : targets.get(0);
		}
		return perm;
	}

	@Override
	public List<BusinessPermTenantDto> listByActions(Set<String> actions) {
		if (Checker.beEmpty(actions)) {
			return Lists.newArrayList();
		}
		return mapper(baseMapper.selectList(lambdaQuery().in(SysBusinessPermTenant::getPermAction, actions)));
	}

	@Override
	public void saveOrUpdatePerm(BusinessPermTenantDto perm) {
		Assert.CheckArgument(perm);
		Assert.CheckArgument(perm.getPermAction());
		SysBusinessPermTenant dbPerm = baseMapper.selectOne(lambdaQuery().eq(SysBusinessPermTenant::getPermAction, perm.getPermAction()));
		if (Checker.beNotEmpty(perm.getId())) {
			if (Checker.beNotNull(dbPerm)) {
				Assert.CheckEqual(dbPerm.getButtonId(), perm.getId(), AuthorizationExceptionEnum.BUSINESS_PERM_ACTION_DUPLICATE);
			}
			updateIt(perm);
		} else {
			Assert.CheckNull(dbPerm, AuthorizationExceptionEnum.BUSINESS_PERM_ACTION_DUPLICATE);
			saveIt(perm);
		}
	}

	@Override
	public void deleteByButtons(Set<String> ids) {
		Assert.CheckArgument(ids);
		baseMapper.delete(lambdaQuery().in(SysBusinessPermTenant::getButtonId, ids));
	}
}
