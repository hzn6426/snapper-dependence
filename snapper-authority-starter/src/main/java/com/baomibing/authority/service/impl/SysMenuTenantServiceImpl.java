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
import com.baomibing.authority.dto.ButtonTenantDto;
import com.baomibing.authority.dto.MenuTenantDto;
import com.baomibing.authority.dto.ResourceApiTenantDto;
import com.baomibing.authority.entity.SysMenuTenant;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.mapper.SysMenuTenantMapper;
import com.baomibing.authority.service.*;
import com.baomibing.authority.state.CommonState;
import com.baomibing.authority.wrap.MenuWrap;
import com.baomibing.core.common.Assert;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SysMenuTenantServiceImpl extends MBaseServiceImpl<SysMenuTenantMapper, SysMenuTenant, MenuTenantDto> implements SysMenuTenantService {

	@Autowired private SysButtonTenantService buttonService;
	@Autowired private SysResourceApiTenantService apiService;
	@Autowired private SysTenantRoleResourceService roleResourceService;
	@Autowired private SysTenantMenuService tenantMenuService;

	@Transactional
	@Override
	public void deleteByIdList(List<Integer> idList) {
		Assert.CheckArgument(idList);
		this.baseMapper.deleteBatchIds(idList);
	}

	@Transactional
	@Override
	public void deleteIt(MenuTenantDto v) {
		//删除菜单同时删除对应按钮
		List<ButtonTenantDto> list = buttonService.listByMenu(v.getId());
		if (Checker.beNotEmpty(list)) {
			List<String> idList = list.stream().map(ButtonTenantDto::getId).collect(Collectors.toList());
			buttonService.deletes(Sets.newHashSet(idList));
		}
		super.deleteIt(v);
		
	} 


	@Transactional
	@Override
	public void saveIt(MenuTenantDto v) {
		super.saveIt(v);
	}

	@Transactional
	@Override
	public void updateIt(MenuTenantDto v) {
		super.updateIt(v);
	}

	@Override
	public List<MenuTenantDto> listAllResourceMenusByIds(Set<String> menuIds) {
		if (Checker.beEmpty(menuIds)) return Lists.newArrayList();
		return mapper(this.baseMapper.listAllResourceMenusByIds(menuIds));
	}

	@Override
	public List<String> listAllParentByIds(Set<String> menuIds) {
		if (Checker.beEmpty(menuIds)) {
			return Lists.newArrayList();
		}
		return this.baseMapper.listAllParentByIds(menuIds);
	}

	@Override
	public List<MenuWrap> treeAllMenus() {
		List<MenuTenantDto> menus = mapper(baseMapper.selectList(lambdaQuery().orderByAsc(SysMenuTenant::getMenuType,SysMenuTenant::getPriority)));
		Set<String> menuIds = menus.stream().map(m -> m.getId()).collect(Collectors.toSet());
		List<ResourceApiTenantDto> apis = apiService.listByResource(menuIds, ResourceTypeEnum.MENU);
		Map<String, ResourceApiTenantDto> apiMap = apis.stream().collect(Collectors.toMap(ResourceApiTenantDto::getResourceId,Function.identity(), (v1,v2)->v2));
		List<MenuWrap> tlist = Lists.newArrayList();

		List<MenuWrap> treeList = menus.stream().map(menu -> new MenuWrap().setKey(menu.getId()).setTitle(menu.getName())
				.setTag(menu.getMenuType()).setIsLeaf(true).setParentId(menu.getParent()).setMenuType(menu.getMenuType())
				.setBeHidden(menu.getBeHidden()).setBeUnauth(menu.getBeUnauth()).setPriority(menu.getPriority())
				.setReqMethod(Checker.beNull(apiMap.get(menu.getId())) ? null : apiMap.get(menu.getId()).getReqMethod())
				.setReqUrl(Checker.beNull(apiMap.get(menu.getId())) ? null : apiMap.get(menu.getId()).getReqUrl())
				.setIconCls(menu.getIcon())).collect(Collectors.toList());
		// 创建map结构用来临时存储对象
		Map<String, MenuWrap> gMap = treeList.stream().collect(Collectors.toMap(MenuWrap::getKey, Function.identity(), (key1, key2) -> key2));
		// 将列表转化成树结构
		treeList.forEach(menu -> {
			String pid = menu.getParentId();
			if (Checker.beNotNull(gMap.get(pid))) {
				MenuWrap pGroup = gMap.get(pid);
				if (Checker.beEmpty(pGroup.getChildren())) {
					pGroup.setIsLeaf(false);
					pGroup.setChildren(Lists.newArrayList());
				}
				menu.setParentGroupName(pGroup.getTitle());
				menu.setParentMenuType(pGroup.getMenuType());
				pGroup.getChildren().add(menu);

			} else {
				menu.setIsLeaf(false);
				tlist.add(menu);
			}
		});

		return tlist;
	}

	@Override
	public List<MenuTenantDto> listAllMenusForGrant() {
		List<MenuTenantDto> tlist = Lists.newArrayList();
		List<MenuTenantDto> vlist = Lists.newArrayList();
		List<SysMenuTenant> list = this.baseMapper.listAllMenusForGrant();
		list.forEach(m -> {
			vlist.add(new MenuTenantDto().setId(m.getId()).setName(m.getMenuName()).setParent(m.getParentId()));
		});
		// 创建map结构用来临时存储对象
		Map<String, MenuTenantDto> map = vlist.stream().collect(Collectors.toMap(MenuTenantDto::getId, Function.identity(), (key1, key2) -> key2));
		// 将列表转化成树结构
		vlist.forEach(menu -> {
			String pid = menu.getParent();
			if (Checker.beNotEmpty(pid)) {
				MenuTenantDto pmenu = map.get(pid);
				if (Checker.beNotNull(pmenu)) {
					if (Checker.beEmpty(pmenu.getChildren())) {
						pmenu.setChildren(Lists.newArrayList());
					}
					pmenu.getChildren().add(menu);
				}
			} else {
				tlist.add(menu);
			}
		});
		return tlist;
	}

	private void saveMenu(MenuTenantDto menu) {
		Assert.CheckArgument(menu);
		ResourceApiTenantDto resource = apiService.getByResouce(menu.getId(), ResourceTypeEnum.MENU);
		boolean beNotExist = resource == null;
		if (beNotExist) {
			resource = new ResourceApiTenantDto();
		}
		resource.setResourceId(menu.getId()).setResourceType(ResourceTypeEnum.MENU.name()).setState(CommonState.ACTIVE)
				.setReqUrl(menu.getPath()).setReqMethod(menu.getReqMethod());
		if (beNotExist) {
			apiService.saveIt(resource);
		} else {
			apiService.updateIt(resource);
		}
		super.saveIt(menu);
	}

	private void updateMenu(MenuTenantDto menu) {
		Assert.CheckArgument(menu);
		ResourceApiTenantDto resource = apiService.getByResouce(menu.getId(), ResourceTypeEnum.MENU);
		boolean beNotExist = resource == null;
		if (beNotExist) {
			resource = new ResourceApiTenantDto();
		}
		resource.setResourceId(menu.getId()).setResourceType(ResourceTypeEnum.MENU.name()).setState(CommonState.ACTIVE)
				.setReqUrl(menu.getPath()).setReqMethod(menu.getReqMethod());
		if (beNotExist) {
			apiService.saveIt(resource);
		} else {
			apiService.updateIt(resource);
		}
		super.updateIt(menu);
	}

	@Override
	public void saveOrUpdateMenu(MenuTenantDto menu) {
		Assert.CheckArgument(menu);
		MenuTenantDto dbMenu = getIt(menu.getId());
		if (Checker.beNull(dbMenu)) {
			saveMenu(menu);
		} else {
			updateMenu(menu);
		}
	}

	@Override
	public void deleteMenu(Set<String> ids) {
		Assert.CheckArgument(ids);
		List<SysMenuTenant> childs = this.baseMapper.selectList(lambdaQuery().in(SysMenuTenant::getParentId, ids));
		if (Checker.beNotEmpty(childs)) {
			throw new ServerRuntimeException(AuthorizationExceptionEnum.MENU_HAS_CHILDREN_MENU_CAN_NOT_REMOVE);
		}
		List<ButtonTenantDto> list = buttonService.listByMenus(ids);
		if (Checker.beNotEmpty(list)) {
			throw new ServerRuntimeException(AuthorizationExceptionEnum.MENU_HAS_CHILDREN_BUTTON_CAN_NOT_REMOVE);
		}
		List<ResourceApiTenantDto> resources = apiService.listByResource(ids, ResourceTypeEnum.MENU);
		if (Checker.beNotEmpty(resources)) {
			apiService.deleteItBatch(resources);
		}
		tenantMenuService.deleteByMenus(ids);
		roleResourceService.deleteByResources(ids, ResourceTypeEnum.MENU);
		super.deletes(ids);
	}
}
