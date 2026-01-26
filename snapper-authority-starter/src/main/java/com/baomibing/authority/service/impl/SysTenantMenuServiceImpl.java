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

import com.baomibing.authority.dto.MenuTenantDto;
import com.baomibing.authority.dto.SysTenantMenuDto;
import com.baomibing.authority.entity.SysMenuTenant;
import com.baomibing.authority.entity.SysTenantMenu;
import com.baomibing.authority.mapper.SysTenantMenuMapper;
import com.baomibing.authority.service.SysTenantMenuService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * SysTenantMenuServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysTenantMenuServiceImpl extends MBaseServiceImpl<SysTenantMenuMapper, SysTenantMenu, SysTenantMenuDto> implements SysTenantMenuService {

    @Override
    public void doSave(Set<String> menuIds, String tenantId) {
        Assert.CheckArgument(menuIds);
        Assert.CheckArgument(tenantId);
//        List<String> dbIds = Lists.newArrayList(getIdsByTenant(tenantId));
//        List<String> toAddList = ListUtils.subtract(Lists.newArrayList(menuIds), dbIds);
//        List<String> toDelList = ListUtils.subtract(dbIds, Lists.newArrayList(menuIds));
//        if (Checker.BeNotEmpty(toDelList)) {
//            deleteByTenantId(Sets.newHashSet(toDelList), tenantId);
//        }
        List<SysTenantMenuDto> menus = Lists.newArrayList();
        menuIds.forEach(m -> menus.add(new SysTenantMenuDto().setMenuId(m).setTenantId(tenantId)));
        super.saveItBatch(menus);
    }

    private Set<String> getIdsByTenant(String tenantId) {
        List<SysTenantMenu> list = baseMapper.selectList(lambdaQuery().eq(SysTenantMenu::getTenantId, tenantId));
        return list.stream().map(SysTenantMenu::getMenuId).collect(Collectors.toSet());
    }

    private void deleteByTenantId(Set<String> menuIds, String tenantId) {
        Assert.CheckArgument(tenantId);
        Assert.CheckArgument(menuIds);
        baseMapper.delete(lambdaQuery().eq(SysTenantMenu::getTenantId, tenantId).in(SysTenantMenu::getMenuId, menuIds));
    }

    @Override
    public List<MenuTenantDto> listAllMenusForGrant(String tenantId) {
        List<MenuTenantDto> tlist = Lists.newArrayList();
        List<MenuTenantDto> vlist = Lists.newArrayList();
        List<SysMenuTenant> list = this.baseMapper.listAllMenusForGrant(tenantId);
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

    @Override
    public List<String> listAllMenus(String tenantId) {
        if (Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }
        List<SysMenuTenant> list = this.baseMapper.listAllMenusForGrant(tenantId);
        return list.stream().map(SysMenuTenant::getId).collect(Collectors.toList());
    }

    @Override
    public void deleteByMenus(Set<String> menuIds) {
        Assert.CheckArgument(menuIds);
        baseMapper.delete(lambdaQuery().in(SysTenantMenu::getMenuId, menuIds));
    }

    @Override
    public void deleteByTenant(String tenantId) {
        Assert.CheckArgument(tenantId);
        baseMapper.delete(lambdaQuery().eq(SysTenantMenu::getTenantId, tenantId));
    }
}
