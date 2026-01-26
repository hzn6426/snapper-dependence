
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
import com.baomibing.authority.dto.ButtonDto;
import com.baomibing.authority.dto.MenuDto;
import com.baomibing.authority.dto.ResourceApiDto;
import com.baomibing.authority.entity.SysMenu;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.mapper.SysMenuMapper;
import com.baomibing.authority.service.SysButtonService;
import com.baomibing.authority.service.SysMenuService;
import com.baomibing.authority.service.SysResourceApiService;
import com.baomibing.authority.service.SysRoleResourceService;
import com.baomibing.authority.state.CommonState;
import com.baomibing.authority.wrap.MenuWrap;
import com.baomibing.core.common.Assert;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 菜单管理
 * 
 * @author zening
 * @since 1.0.0
 */
@Service
public class SysMenuServiceImpl extends MBaseServiceImpl<SysMenuMapper, SysMenu, MenuDto> implements SysMenuService {

	@Autowired private SysButtonService buttonService;
	@Autowired private SysResourceApiService apiService;
	@Autowired private SysRoleResourceService roleResourceService;


	@Override
	public List<MenuDto> listAllResourceMenusByIds(Set<String> menuIds) {
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
//		List<MenuDto> menus = mapper(baseMapper.selectList(lambdaQuery().orderByAsc(Lists.newArrayList(SysMenu::getMenuType,SysMenu::getPriority))));
		List<MenuDto> menus = mapper(baseMapper.selectList(lambdaQuery().orderByAsc(SysMenu::getMenuType,SysMenu::getPriority)));
		Set<String> menuIds = menus.stream().map(MenuDto::getId).collect(Collectors.toSet());
		List<ResourceApiDto> apis = apiService.listByResource(menuIds, ResourceTypeEnum.MENU);
		Map<String, ResourceApiDto> apiMap = apis.stream().collect(Collectors.toMap(ResourceApiDto::getResourceId, Function.identity(), (v1, v2)->v2));
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

	private void saveMenu(MenuDto menu) {
		Assert.CheckArgument(menu);
		ResourceApiDto resource = apiService.getByResouce(menu.getId(), ResourceTypeEnum.MENU);
		boolean beNotExist = resource == null;
		if (beNotExist) {
			resource = new ResourceApiDto();
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

	private void updateMenu(MenuDto menu) {
		Assert.CheckArgument(menu);
		ResourceApiDto resource = apiService.getByResouce(menu.getId(), ResourceTypeEnum.MENU);
		boolean beNotExist = resource == null;
		if (beNotExist) {
			resource = new ResourceApiDto();
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
	public void saveOrUpdateMenu(MenuDto menu) {
		Assert.CheckArgument(menu);
		MenuDto dbMenu = getIt(menu.getId());
		if (Checker.beNull(dbMenu)) {
			saveMenu(menu);
		} else {
			assertBeLock(dbMenu);
			updateMenu(menu);
		}
	}

	@Override
	public void deleteMenu(Set<String> ids) {
		Assert.CheckArgument(ids);
		List<MenuDto> menus = super.gets(ids);
		for (MenuDto dbMenu : menus) {
			assertBeLock(dbMenu);
		}
		List<SysMenu> childs = this.baseMapper.selectList(lambdaQuery().in(SysMenu::getParentId, ids));
		if (Checker.beNotEmpty(childs)) {
			throw new ServerRuntimeException(AuthorizationExceptionEnum.MENU_HAS_CHILDREN_MENU_CAN_NOT_REMOVE);
		}
		List<ButtonDto> list = buttonService.listByMenus(ids);
		if (Checker.beNotEmpty(list)) {
			throw new ServerRuntimeException(AuthorizationExceptionEnum.MENU_HAS_CHILDREN_BUTTON_CAN_NOT_REMOVE);
		}
		List<ResourceApiDto> resources = apiService.listByResource(ids, ResourceTypeEnum.MENU);
		if (Checker.beNotEmpty(resources)) {
			apiService.deleteItBatch(resources);
		}
		roleResourceService.deleteByResources(ids, ResourceTypeEnum.MENU);
		super.deletes(ids);
	}
	

	
	
}
