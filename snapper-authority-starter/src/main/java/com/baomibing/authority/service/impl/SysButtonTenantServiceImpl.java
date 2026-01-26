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
import com.baomibing.authority.dto.BusinessPermTenantDto;
import com.baomibing.authority.dto.ButtonTenantDto;
import com.baomibing.authority.dto.MenuTenantDto;
import com.baomibing.authority.dto.ResourceApiTenantDto;
import com.baomibing.authority.entity.SysButtonTenant;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.mapper.SysButtonTenantMapper;
import com.baomibing.authority.service.*;
import com.baomibing.authority.state.CommonState;
import com.baomibing.core.common.Assert;
import com.baomibing.core.common.SearchResult;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.constant.TenantRedisKeyConstant;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.baomibing.tool.util.PageUtil.offsetCurrent;

@Service
public class SysButtonTenantServiceImpl extends MBaseServiceImpl<SysButtonTenantMapper, SysButtonTenant, ButtonTenantDto> implements SysButtonTenantService {

	@Autowired private SysMenuTenantService menuService;
	@Autowired private SysTenantButtonService tenantButtonService;
	@Autowired private SysResourceApiTenantService resourceApiService;
	@Autowired private SysTenantRoleResourceService roleResourceService;
	@Autowired private SysBusinessPermTenantService businessPermService;
	
	@Override
	public List<ButtonTenantDto> listByMenu(String menuId) {
		if (Checker.beEmpty(menuId)) {
			return Lists.newArrayList();
		}
		return mapper(this.baseMapper.selectList(lambdaQuery().eq(SysButtonTenant::getMenuId, menuId)));
	}


	@Override
	public List<ButtonTenantDto> listByMenus(Set<String> menuIds) {
		if (Checker.beEmpty(menuIds)) {
			return Lists.newArrayList();
		}
		return mapper(this.baseMapper.selectList(lambdaQuery().in(SysButtonTenant::getMenuId, menuIds)));
	}

	@Override
	public List<ButtonTenantDto> listByMenuForGrant(String menuId) {
		return mapper(this.baseMapper.selectList(lambdaQuery().eq(SysButtonTenant::getMenuId, menuId).eq(SysButtonTenant::getBeUnauth, false)));
	}


	@Override
	public void saveIt(ButtonTenantDto v) {
		Assert.CheckArgument(v, v.getMenuId());
		// 检查关联的菜单是否存在
		Assert.CheckNotNull(menuService.getIt(v.getMenuId()), AuthorizationExceptionEnum.MENU_OF_BUTTON_REFERENCE_NOT_EXIST);
		super.saveIt(v);
		// 检查资源API是否存在,不存在则创建
		ResourceApiTenantDto api = resourceApiService.getByHttpUrlAndMethod(v.getReqUrl(), v.getReqMethod());
		if (Checker.beNull(api)) {
			api = new ResourceApiTenantDto();
			api.setReqMethod(v.getReqMethod()).setReqUrl(v.getReqUrl()).setResourceId(v.getId()).setResourceType(ResourceTypeEnum.BUTTON.toString()).setState(CommonState.ACTIVE);
			resourceApiService.saveIt(api);
		}

	}

	@Override
	public void updateIt(ButtonTenantDto v) {
		Assert.CheckArgument(v, v.getMenuId());
		// 检查关联的菜单是否存在
		Assert.CheckNotNull(menuService.getIt(v.getMenuId()), AuthorizationExceptionEnum.MENU_OF_BUTTON_REFERENCE_NOT_EXIST);
		super.updateIt(v);
	}

	@Override
	public SearchResult<ButtonTenantDto> searchButtonsAndApiByMenu(ButtonTenantDto v, int pageNumber, int pageSize) {
		if (Checker.beNull(v)) {
			return new SearchResult<>(0, Lists.newArrayList());
		}
		int offset = offsetCurrent(pageNumber,pageSize);
		List<ButtonTenantDto> buttons = mapper(baseMapper.listButtonAndApiByMenu(v.getMenuId(),v.getKeyword(), pageSize, offset));
		int count = baseMapper.countButtonAndApiByMenu(v.getMenuId());
		return new SearchResult<>(count, buttons);
	}

	private void saveButton(ButtonTenantDto button) {
		Assert.CheckArgument(button);
		Assert.CheckArgument(button.getMenuId());
		ResourceApiTenantDto resource = resourceApiService.getByResouce(button.getId(), ResourceTypeEnum.BUTTON);
		boolean beNotExist = resource == null;
		if (beNotExist) {
			resource = new ResourceApiTenantDto();
		}
		resource.setResourceId(button.getId()).setResourceType(ResourceTypeEnum.BUTTON.name()).setState(CommonState.ACTIVE)
				.setReqUrl(button.getReqUrl()).setReqMethod(button.getReqMethod());
		if (beNotExist) {
			resourceApiService.saveIt(resource);
		} else {
			resourceApiService.updateIt(resource);
		}
		super.saveIt(button);
	}

	private void updateButton(ButtonTenantDto button) {
		Assert.CheckArgument(button);
		ResourceApiTenantDto resource = resourceApiService.getByResouce(button.getId(), ResourceTypeEnum.BUTTON);
		boolean beNotExist = resource == null;
		if (beNotExist) {
			resource = new ResourceApiTenantDto();
		}
		resource.setResourceId(button.getId()).setResourceType(ResourceTypeEnum.BUTTON.name()).setState(CommonState.ACTIVE)
				.setReqUrl(button.getReqUrl()).setReqMethod(button.getReqMethod());
		if (beNotExist) {
			resourceApiService.saveIt(resource);
		} else {
			resourceApiService.updateIt(resource);
		}
		super.updateIt(button);
	}

	@Override
	public void saveOrUpdateButton(ButtonTenantDto button) {
		Assert.CheckArgument(button);
		ButtonTenantDto dbButton = super.getIt(button.getId());
		if (Checker.beEmpty(button.getSubMenu())) {
			MenuTenantDto menu = menuService.getIt(button.getMenuId());
			if (Checker.beNotNull(menu)) {
				button.setSubMenu(menu.getName());
			}
		}
		if (Checker.beNull(dbButton)) {
			saveButton(button);
		} else {
			updateButton(button);
		}

		if (Checker.beNotEmpty(button.getPermAction())) {
			//保存或更新Perm
			BusinessPermTenantDto perm = new BusinessPermTenantDto();
			perm.setId(button.getPermId()).setButtonId(button.getId()).setMenuId(button.getMenuId())
					.setReqUrl(button.getReqUrl()).setReqMethod(button.getReqMethod()).setPermName(button.getButtonName())
					.setPermAction(button.getPermAction());
			businessPermService.saveOrUpdatePerm(perm);
		}
	}

	@Override
	public void deleteButtons(Set<String> ids) {
		Assert.CheckArgument(ids);
		List<ResourceApiTenantDto> resources = resourceApiService.listByResource(ids, ResourceTypeEnum.BUTTON);
		if (Checker.beNotEmpty(resources)) {
			resourceApiService.deleteItBatch(resources);
			for (ResourceApiTenantDto a : resources) {
				cacheService.del(TenantRedisKeyConstant.CACHE_API_PREFIX + a.getReqMethod() + Strings.DOUBLE_AT + a.getReqUrl());
			}
		}
		tenantButtonService.deleteByButtons(ids);
		roleResourceService.deleteByResources(ids, ResourceTypeEnum.BUTTON);
		super.deletes(ids);
	}

	@Override
	public List<ButtonTenantDto> listByKeyWord(String keyWord) {
		if (Checker.beEmpty(keyWord)) {
			return Lists.newArrayList();
		}
		return mapper(baseMapper.listButtonByKeyWork(keyWord));
	}

	@Override
	public ButtonTenantDto getButtonAndApiById(String id) {
		if (Checker.beEmpty(id)) {
			return null;
		}
		SysButtonTenant button = baseMapper.getButtonAndApiById(id);
		return Checker.beNull(button) ? null : mapper2v(button);
	}
}
