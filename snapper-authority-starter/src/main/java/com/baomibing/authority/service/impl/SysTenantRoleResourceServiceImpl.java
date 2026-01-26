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

import com.baomibing.authority.constant.enums.MenuTypeEnum;
import com.baomibing.authority.constant.enums.ResourceTypeEnum;
import com.baomibing.authority.dto.*;
import com.baomibing.authority.entity.SysButtonTenant;
import com.baomibing.authority.entity.SysMenuTenant;
import com.baomibing.authority.entity.SysTenantRoleResource;
import com.baomibing.authority.mapper.SysTenantRoleResourceMapper;
import com.baomibing.authority.service.*;
import com.baomibing.cache.CacheService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.baomibing.tool.constant.TenantRedisKeyConstant.CACHE_ACTION_CONNECT_PREFIX;


/**
 * SysTenantRoleResourceServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysTenantRoleResourceServiceImpl extends MBaseServiceImpl<SysTenantRoleResourceMapper, SysTenantRoleResource, SysTenantRoleResourceDto> implements SysTenantRoleResourceService {
    @Autowired private SysTenantRoleResourceService self;// 引用自身,防止spring 代理bug
    @Autowired private CacheService cacheService;
    @Autowired private SysTenantUserRoleService userRoleService;
    @Autowired private SysResourceApiService resourceApiService;
    @Autowired private SysMenuTenantService menuService;
    @Autowired private SysButtonTenantService buttonService;
    @Autowired private SysTenantUsetRoleService usetRoleService;
    @Autowired private SysTenantRoleResourceService roleResourceService;



    @Override
    public List<String> listPermResourceIdsByRoles(Set<String> roleIds, ResourceTypeEnum resourceType, String tenantId) {
        if (Checker.beEmpty(roleIds) || Checker.beNull(resourceType) || Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }
        LambdaQueryWrapper<SysTenantRoleResource> wrapper = lambdaQuery();
        wrapper.eq(SysTenantRoleResource::getResourceType, resourceType.toString()).in(SysTenantRoleResource::getRoleId, roleIds).eq(SysTenantRoleResource::getTenantId, tenantId);
        List<SysTenantRoleResource> roleResourceList = this.baseMapper.selectList(wrapper);
        if (Checker.beEmpty(roleResourceList)) {
            return Lists.newArrayList();
        }
        return roleResourceList.stream().map(SysTenantRoleResource::getResourceId).collect(Collectors.toList());
    }

    @Override
    public List<ButtonTenantDto> listPermButtonsByRolesAndMenu(Set<String> roleIds, String menuId, String tenantId) {
        if (Checker.beEmpty(roleIds) || Checker.beEmpty(menuId) || Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }
        List<SysButtonTenant> buttonList = this.baseMapper.listPermButtonsByMenuAndRoles(Lists.newArrayList(roleIds), menuId, tenantId);
        return Checker.beEmpty(buttonList) ? Lists.newArrayList()
                : new ArrayList<>(this.collectionMapper.mapCollection(buttonList, ButtonTenantDto.class));
    }


    @Override
    public List<MenuTenantDto> listAllMenusForGrant(Set<String> roleIds, String tenantId) {
        List<MenuTenantDto> list = Lists.newArrayList();
        if (Checker.beEmpty(roleIds) || Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }
        List<MenuTenantDto> menuList =  Lists.newArrayList(this.collectionMapper.mapCollection(baseMapper.listAllMenusForGrant(Lists.newArrayList(roleIds), tenantId), MenuTenantDto.class));
        // 创建map结构用来临时存储对象
        Map<String, MenuTenantDto> map = menuList.stream().collect(Collectors.toMap(MenuTenantDto::getId, Function.identity(), (key1, key2) -> key2));
        // 找未分配权限的子菜单的上级菜单
        Set<String> parentIds = menuList.stream().map(MenuTenantDto::getParent).collect(Collectors.toSet());
        List<String> needParentMenuIds = ListUtils.subtract(Lists.newArrayList(parentIds), Lists.newArrayList(map.keySet()));
        List<String> allParentMenuIds = menuService.listAllParentByIds(Sets.newHashSet(needParentMenuIds));
        Set<String> allParents = allParentMenuIds.stream().flatMap(t -> Splitter.on(",").splitToStream(t))
                .filter(Checker::beNotEmpty).collect(Collectors.toSet());
        List<MenuTenantDto> needParentMenu = menuService.listAllResourceMenusByIds(Sets.newHashSet(allParents));
        menuList.addAll(needParentMenu);
        Map<String, MenuTenantDto> allMap = menuList.stream()
                .collect(Collectors.toMap(MenuTenantDto::getId, Function.identity(), (key1, key2) -> key2));
        // 将列表转化成树结构
        menuList.forEach(menu -> {
            String pid = menu.getParent();
            if (Checker.beNotEmpty(pid) && !"ROOT".equalsIgnoreCase(pid)) {
                MenuTenantDto pmenu = allMap.get(pid);
                if (Checker.beNotNull(pmenu)) {
                    if (Checker.beEmpty(pmenu.getChildren())) {
                        pmenu.setChildren(Lists.newArrayList());
                    }
                    pmenu.getChildren().add(menu);
                    if (Checker.beEmpty(pmenu.getRedirect())) {
                        pmenu.setRedirect(menu.getPath());
                    }
                }

            } else {
                list.add(menu);
            }
        });
        Ordering<MenuTenantDto> ordering = Ordering.from(Comparator.comparingInt(MenuTenantDto::getPriority));
        list.sort(ordering);
        for (MenuTenantDto menuDto : list) {
            List<MenuTenantDto> children = menuDto.getChildren();
            if (Checker.beNotEmpty(children)) {
                children.sort(ordering);
            }
        }
        return list;
    }

//    @Override
//    public List<ButtonTenantDto> listAllButtonsByMenuForGrant(Set<String> roleIds, String tenantId, String menuId) {
//        return Collections.emptyList();
//    }

    /**
     * 清除角色对应资源的权限
     *
     * @param roleId       角色ID
     * @param resourceType 资源类型
     */
    private void deleteResourcePermsByRole(String tenantId, String roleId, ResourceTypeEnum resourceType) {
        LambdaQueryWrapper<SysTenantRoleResource> wrapper = lambdaQuery();
        wrapper.eq(SysTenantRoleResource::getRoleId, roleId).eq(SysTenantRoleResource::getResourceType, resourceType).eq(SysTenantRoleResource::getTenantId, tenantId);
        this.baseMapper.delete(wrapper);
    }

    /**
     * 删除角色对应的某些资源权限
     *
     * @param roleId         角色ID
     * @param resourceType   资源类型
     * @param resourceIdList 资源ID列表
     */
    private void deleteSomeResourcesPermsByRole(String tenantId, String roleId, ResourceTypeEnum resourceType, List<String> resourceIdList) {
        if (Checker.beEmpty(resourceIdList)) {
            return;
        }
        LambdaQueryWrapper<SysTenantRoleResource> wrapper = lambdaQuery();
        wrapper.eq(SysTenantRoleResource::getRoleId, roleId).eq(SysTenantRoleResource::getResourceType, resourceType)
                .in(SysTenantRoleResource::getResourceId, resourceIdList).eq(SysTenantRoleResource::getTenantId, tenantId);
        this.baseMapper.delete(wrapper);
    }

    /**
     * 保存角色对应的某些资源权限
     *
     * @param roleId         角色ID
     * @param resourceType   资源类型
     * @param resourceIdList 资源ID列表
     */
    private void saveSomeResourcesPermsByRole(String tenantId, String roleId, ResourceTypeEnum resourceType, List<String> resourceIdList) {
        if (Checker.beEmpty(resourceIdList)) {
            return;
        }
        List<SysTenantRoleResourceDto> list = Lists.newArrayList();
        resourceIdList.forEach(id -> list.add( new SysTenantRoleResourceDto().setResourceId(id).setRoleId(roleId).setResourceType(resourceType.toString()).setTenantId(tenantId)));
        for (SysTenantRoleResourceDto rr : list) {
            super.saveIt(rr);
        }
    }

    /**
     * 根据角色保存资源的权限
     *
     * @param roleId       角色ID
     * @param resourceIds  资源ID列表
     * @param resourceType 资源类型
     */
    private void saveResourcePermsByRole(String tenantId, String roleId, Set<String> resourceIds, ResourceTypeEnum resourceType) {
        // 列表为空,清空授权
        if (Checker.beEmpty(resourceIds)) {
            deleteResourcePermsByRole(tenantId, roleId, resourceType);
            return;
        }
        if (resourceType.equals(ResourceTypeEnum.MENU)) {//菜单类型，去掉非叶子节点
            List<MenuTenantDto> menus = menuService.gets(resourceIds);
            Set<String> pids = menus.stream().map(MenuTenantDto::getParent).filter(Checker::beNotEmpty).collect(Collectors.toSet());
            List<String> notLeafs = ListUtils.subtract(Lists.newArrayList(resourceIds), Lists.newArrayList(pids));
            resourceIds = Sets.newHashSet(notLeafs);
        }
        // 获取原始授权的资源ID¡列表
        List<String> dbResourceIdList = self.listPermResourceIdsByRoles(ImmutableSet.of(roleId), resourceType, tenantId);
        /*
         * 处理资源： 1.以当前授权资源id集合为全集，以原始资源id集合为子集，进行补集运算，结果为 需要添加的资源权限，
         * 2.以原始资源权限id集合为全集，以当前授权资源id集合为子集，进行补集运算，结果为需要删除的资源权限
         */
        List<String> toAddList = ListUtils.subtract(Lists.newArrayList(resourceIds), dbResourceIdList);
        List<String> toDelList = ListUtils.subtract(dbResourceIdList, Lists.newArrayList(resourceIds));
        // 删除需要删除的
        this.deleteSomeResourcesPermsByRole(tenantId, roleId, resourceType, toDelList);
//		if (resourceType.equals(ResourceTypeEnum.MENU)) {
//			toAddList = toAddList.stream().filter(t -> Checker.beNotEmpty(t)).collect(Collectors.toList());
//		}66
        // 添加需要添加的123
        this.saveSomeResourcesPermsByRole(tenantId, roleId, resourceType, toAddList);
        // 刷新权限
//        this.refreshPrivileges();
    }

    @Override
    public void saveButtonPermsByMenuAndRole(String tenantId, String roleId, String menuId, Set<String> buttonIds) {
        Assert.CheckArgument(roleId);
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        Assert.CheckArgument(tenantId);
        Assert.CheckArgument(menuId);
        List<ButtonTenantDto> buttons = listPermButtonsByRolesAndMenu(Sets.newHashSet(roleId), menuId, tenantId);
        List<String> dbIds = buttons.stream().map(ButtonTenantDto::getId).collect(Collectors.toList());
        List<String> toAddList = ListUtils.subtract(Lists.newArrayList(buttonIds), dbIds);
        List<String> toDelList = ListUtils.subtract(dbIds, Lists.newArrayList(buttonIds));
        // 删除需要删除的
        this.deleteSomeResourcesPermsByRole(tenantId, roleId, ResourceTypeEnum.BUTTON, toDelList);
        // 添加需要添加的
        this.saveSomeResourcesPermsByRole(tenantId, roleId, ResourceTypeEnum.BUTTON, toAddList);
    }

    @Override
    public void saveButtonPermsByRole(String tenantId, String roleId, Set<String> buttonIds) {
        Assert.CheckArgument(roleId);
        Assert.CheckArgument(tenantId);
//        List<ButtonTenantDto> buttons = listPermButtonsByRoles(Sets.newHashSet(roleId), tenantId);
//        List<String> dbIds = buttons.stream().map(ButtonTenantDto::getId).collect(Collectors.toList());
//        List<String> toAddList = ListUtils.subtract(Lists.newArrayList(buttonIds), dbIds);
//        List<String> toDelList = ListUtils.subtract(dbIds, Lists.newArrayList(buttonIds));
//        // 删除需要删除的
//        this.deleteSomeResourcesPermsByRole(tenantId, roleId, ResourceTypeEnum.BUTTON, toDelList);
        // 添加需要添加的
        this.saveResourcePermsByRole(tenantId, roleId, buttonIds, ResourceTypeEnum.BUTTON);
    }

    @Override
    public void saveMenusPermsByRole(String tenantId, String roleId, Set<String> menuIds) {
        Assert.CheckArgument(roleId);
//        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        Assert.CheckArgument(tenantId);
        this.saveResourcePermsByRole(tenantId, roleId, menuIds, ResourceTypeEnum.MENU);
    }

    @Override
    public List<MenuTenantDto> listAllPermPointMenusByRoles(Set<String> roleIds, MenuTypeEnum type, String tenantId) {
        List<MenuTenantDto> list = Lists.newArrayList();
//        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        if (Checker.beEmpty(roleIds) || Checker.beEmpty(tenantId)) {
            return list;
        }
//        String userName = currentUserName();
//        String tag = currentUserSystemTag();
        List<SysMenuTenant> dbMenuList = this.baseMapper.listAllPermPointMenusByRoles(Lists.newArrayList(roleIds), type.name(), tenantId);
        List<MenuTenantDto> menuList = new ArrayList<>(this.collectionMapper.mapCollection(dbMenuList, MenuTenantDto.class));
        // 创建map结构用来临时存储对象
        Map<String, MenuTenantDto> map = menuList.stream().collect(Collectors.toMap(MenuTenantDto::getId, Function.identity(), (key1, key2) -> key2));
        // 找未分配权限的子菜单的上级菜单
        Set<String> parentIds = menuList.stream().map(MenuTenantDto::getParent).collect(Collectors.toSet());
        List<String> needParentMenuIds = ListUtils.subtract(Lists.newArrayList(parentIds), Lists.newArrayList(map.keySet()));
        List<String> allParentMenuIds = menuService.listAllParentByIds(Sets.newHashSet(needParentMenuIds));
        Set<String> allParents = allParentMenuIds.stream().flatMap(t -> Splitter.on(",").splitToStream(t))
                .filter(Checker::beNotEmpty).collect(Collectors.toSet());
        List<MenuTenantDto> needParentMenu = menuService.listAllResourceMenusByIds(Sets.newHashSet(allParents));
        menuList.addAll(needParentMenu);
        Map<String, MenuTenantDto> allMap = menuList.stream()
                .collect(Collectors.toMap(MenuTenantDto::getId, Function.identity(), (key1, key2) -> key2));
        // 将列表转化成树结构
        menuList.forEach(menu -> {
            String pid = menu.getParent();
            if (Checker.beNotEmpty(pid) && !"ROOT".equalsIgnoreCase(pid)) {
                MenuTenantDto pmenu = allMap.get(pid);
                if (Checker.beNotNull(pmenu)) {
                    if (Checker.beEmpty(pmenu.getChildren())) {
                        pmenu.setChildren(Lists.newArrayList());
                    }
                    pmenu.getChildren().add(menu);
                    if (Checker.beEmpty(pmenu.getRedirect())) {
                        pmenu.setRedirect(menu.getPath());
                    }
                }

            } else {
                list.add(menu);
            }
        });
        Ordering<MenuTenantDto> ordering = Ordering.from(Comparator.comparingInt(MenuTenantDto::getPriority));
        list.sort(ordering);
        for (MenuTenantDto menuDto : list) {
            List<MenuTenantDto> children = menuDto.getChildren();
            if (Checker.beNotEmpty(children)) {
                children.sort(ordering);
            }
        }
        return list;
    }




//    @Override
//    public List<ResourceApiDto> listAllPermButtonsForGroupRoleIds(String tenantId) {
//        if (Checker.beEmpty(tenantId)) {
//            return Lists.newArrayList();
//        }
//        return Lists.newArrayList(this.collectionMapper.mapCollection(this.baseMapper.listAllPermButtonsForGroupRoleIds(tenantId), ResourceApiDto.class));
//    }
//
//    @Override
//    public List<ResourceApiDto> listAllUnPermButtonsForGroupRoleIds(String tenantId) {
//        return Lists.newArrayList(this.collectionMapper.mapCollection(this.baseMapper.listAllUnPermButtonsForGroupRoleIds(tenantId), ResourceApiDto.class));
//    }

    @Override
    public List<ButtonTenantDto> listPermButtonsForBusinessPermByGroupAndUser(String tenantId, String orgId, String userId) {
//        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        if (Checker.beEmpty(userId) || Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }
        List<SysButtonTenant> buttons = this.baseMapper.listPermButtonsForBusinessPermByGroupAndUser(tenantId, orgId, userId);
        return Checker.beEmpty(buttons) ? Lists.newArrayList()
                : Lists.newArrayList(this.collectionMapper.mapCollection(buttons, ButtonTenantDto.class));
    }

    @Override
    public List<MenuTenantDto> listPermMenusAndButtonsForBusinessPermByUser(String tenantId, String orgId, String userId) {
//        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        if (Checker.beEmpty(userId) || Checker.beEmpty(orgId) || Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }

        List<SysTenantRoleDto> roles = userRoleService.listRolesByGroupAndUser(tenantId, orgId, userId);
        Set<String> roleIds = roles.stream().map(SysTenantRoleDto::getId).collect(Collectors.toSet());
        // 取角色对应的菜单列表
        List<SysMenuTenant> dbMenuList = this.baseMapper.listAllPermMenusByRoles(Lists.newArrayList(roleIds), tenantId);
        List<MenuTenantDto> menuList = new ArrayList<>(this.collectionMapper.mapCollection(dbMenuList, MenuTenantDto.class));
        // 创建map结构用来临时存储对象
        Map<String, MenuTenantDto> map = menuList.stream().collect(Collectors.toMap(MenuTenantDto::getId, Function.identity(), (key1, key2) -> key2));
        // 找未分配权限的子菜单的上级菜单
        Set<String> parentIds = menuList.stream().map(MenuTenantDto::getParent).collect(Collectors.toSet());
        List<String> needParentMenuIds = ListUtils.subtract(Lists.newArrayList(parentIds), Lists.newArrayList(map.keySet()));
        List<String> allParentMenuIds = menuService.listAllParentByIds(Sets.newHashSet(needParentMenuIds));
        Set<String> allParents = allParentMenuIds.stream().flatMap(t -> Splitter.on(",").splitToStream(t))
                .filter(Checker::beNotEmpty).collect(Collectors.toSet());
        List<MenuTenantDto> needParentMenu = menuService.listAllResourceMenusByIds(Sets.newHashSet(allParents));
        menuList.addAll(needParentMenu);

        Set<String> keys = cacheService.keys(CACHE_ACTION_CONNECT_PREFIX);
        // 取用户对应权限的按钮列表
        List<ButtonTenantDto> buttonList = listPermButtonsForBusinessPermByGroupAndUser(tenantId, orgId, userId);
        List<ButtonTenantDto> targetButtons = buttonList.stream()
                .filter(b -> keys.contains(CACHE_ACTION_CONNECT_PREFIX + b.getPermAction()))
                .collect(Collectors.toList());

//		Set<String> menuIds = buttonList.stream().map(b -> b.getMenuId()).collect(Collectors.toSet());

        // 创建map结构用来临时存储对象
        Map<String, MenuTenantDto> allMenuMap = menuList.stream()
                .collect(Collectors.toMap(MenuTenantDto::getId, Function.identity(), (key1, key2) -> key2));
        List<MenuTenantDto> tlist = Lists.newArrayList();

        Set<String> buttonMenuIds = Sets.newHashSet();
        // 遍历按钮，将按钮加入到菜单中
        targetButtons.forEach(b -> {
            String mid = b.getMenuId();
            MenuTenantDto menu = map.get(mid);
            if (Checker.beNotNull(menu)) {
                buttonMenuIds.add(mid);
                if (Checker.beEmpty(menu.getChildren())) {
                    menu.setChildren(Lists.newArrayList());
                }
                menu.getChildren().add(new MenuTenantDto().setId(b.getId()).setName("[" + b.getSubMenu() + "]-" + b.getButtonName())
                        .setPermScope(b.getPermScope()).setTag(ResourceTypeEnum.BUTTON.name())
                        .setPermId(b.getPermId()).setPermStartTime(b.getPermStartTime())
                        .setPermEndTime(b.getPermEndTime()));
            }
        });

        // 将列表转化成树结构
        menuList.forEach(menu -> {
            String pid = menu.getParent();
            if (Checker.beNotEmpty(pid)) {
                MenuTenantDto pmenu = allMenuMap.get(pid);
                if (Checker.beNotNull(pmenu)) {
                    if (Checker.beEmpty(pmenu.getChildren())) {
                        pmenu.setChildren(Lists.newArrayList());
                    }
                    pmenu.getChildren().add(menu);
                } else {
                    tlist.add(menu);
                }
            } else {
                tlist.add(menu);
            }
        });

        List<MenuTenantDto> targetMenus =  deleteNoButtonMenus(tlist, buttonMenuIds);
        return deleteNoButtonMenus(targetMenus, buttonMenuIds);
    }

    private List<MenuTenantDto> deleteNoButtonMenus(List<MenuTenantDto> menuList, Set<String> buttonMenuIds) {
        if (Checker.beEmpty(menuList)) {
            return menuList;
        }
        Iterator<MenuTenantDto> it = menuList.iterator();
        while (it.hasNext()) {
            MenuTenantDto m = it.next();
            boolean beDelete = false;
            if (buttonMenuIds.contains(m.getId())) {
                continue;
            }
            if (Checker.beEmpty(m.getChildren())) {
                it.remove();
                continue;
            }
            m.setChildren(deleteNoButtonMenus(m.getChildren(), buttonMenuIds));

        }
        return menuList;
    }

    @Override
    public void deleteByRoles(Set<String> roles, String tenantId) {
        Assert.CheckArgument(roles);
        Assert.CheckArgument(tenantId);
        this.baseMapper.delete(lambdaQuery().in(SysTenantRoleResource::getRoleId, roles).eq(SysTenantRoleResource::getTenantId, tenantId));
    }

    @Override
    public void deleteByResources(Set<String> resourceIds, ResourceTypeEnum resourceType, String tenantId) {
        Assert.CheckArgument(resourceIds);
        Assert.CheckArgument(resourceType);
//        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        Assert.CheckArgument(tenantId);
        this.baseMapper.delete(lambdaQuery().in(SysTenantRoleResource::getResourceId, resourceIds).eq(SysTenantRoleResource::getResourceType, resourceType.name()).eq(SysTenantRoleResource::getTenantId, tenantId));
    }

    @Override
    public void deleteByResources(Set<String> resourceIds, ResourceTypeEnum resourceType) {
        Assert.CheckArgument(resourceIds);
        Assert.CheckArgument(resourceType);
        this.baseMapper.delete(lambdaQuery().in(SysTenantRoleResource::getResourceId, resourceIds).eq(SysTenantRoleResource::getResourceType, resourceType.name()));
    }

    @Override
    public List<MenuTenantDto> listPermMenusAndButtonsForBusinessPermByUset(String tenantId, String usetId) {
//        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        if (Checker.beEmpty(usetId) || Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }
        Set<String> roleIds = usetRoleService.listRolesByUset(tenantId, usetId);
        // 取角色对应的菜单列表
        List<SysMenuTenant> dbMenuList = this.baseMapper.listAllPermMenusByRoles(Lists.newArrayList(roleIds), tenantId);
        List<MenuTenantDto> menuList = new ArrayList<>(this.collectionMapper.mapCollection(dbMenuList, MenuTenantDto.class));
        // 创建map结构用来临时存储对象
        Map<String, MenuTenantDto> map = menuList.stream().collect(Collectors.toMap(MenuTenantDto::getId, Function.identity(), (key1, key2) -> key2));
        // 找未分配权限的子菜单的上级菜单
        Set<String> parentIds = menuList.stream().map(MenuTenantDto::getParent).collect(Collectors.toSet());
        List<String> needParentMenuIds = ListUtils.subtract(Lists.newArrayList(parentIds), Lists.newArrayList(map.keySet()));
        List<String> allParentMenuIds = menuService.listAllParentByIds(Sets.newHashSet(needParentMenuIds));
        Set<String> allParents = allParentMenuIds.stream().flatMap(t -> Splitter.on(",").splitToStream(t))
                .filter(Checker::beNotEmpty).collect(Collectors.toSet());
        List<MenuTenantDto> needParentMenu = menuService.listAllResourceMenusByIds(Sets.newHashSet(allParents));
        menuList.addAll(needParentMenu);

        // 取用户对应权限的按钮列表
        List<SysButtonTenant> buttons = baseMapper.listPermButtonsForBusinessPermByUset(tenantId, usetId);

        List<ButtonDto> buttonList = Checker.beEmpty(buttons) ? Lists.newArrayList()
                : Lists.newArrayList(this.collectionMapper.mapCollection(buttons, ButtonDto.class));

        Set<String> keys = cacheService.keys(CACHE_ACTION_CONNECT_PREFIX);
        List<ButtonDto> targetButtons = buttonList.stream()
                .filter(b -> keys.contains(CACHE_ACTION_CONNECT_PREFIX + b.getPermAction()))
                .collect(Collectors.toList());

        // 创建map结构用来临时存储对象
        Map<String, MenuTenantDto> allMenuMap = menuList.stream()
                .collect(Collectors.toMap(MenuTenantDto::getId, Function.identity(), (key1, key2) -> key2));
        List<MenuTenantDto> tlist = Lists.newArrayList();

        Set<String> buttonMenuIds = Sets.newHashSet();
        // 遍历按钮，将按钮加入到菜单中
        targetButtons.forEach(b -> {
            String mid = b.getMenuId();
            MenuTenantDto menu = map.get(mid);
            if (Checker.beNotNull(menu)) {
                buttonMenuIds.add(mid);
                if (Checker.beEmpty(menu.getChildren())) {
                    menu.setChildren(Lists.newArrayList());
                }
                menu.getChildren().add(new MenuTenantDto().setId(b.getId()).setName("[" + b.getSubMenu() + "]-" + b.getButtonName())
                        .setPermScope(b.getPermScope()).setTag(ResourceTypeEnum.BUTTON.name())
                        .setPermId(b.getPermId()).setPermStartTime(b.getPermStartTime())
                        .setPermEndTime(b.getPermEndTime()));
            }
        });

        // 将列表转化成树结构
        menuList.forEach(menu -> {
            String pid = menu.getParent();
            if (Checker.beNotEmpty(pid)) {
                MenuTenantDto pmenu = allMenuMap.get(pid);
                if (Checker.beNotNull(pmenu)) {
                    if (Checker.beEmpty(pmenu.getChildren())) {
                        pmenu.setChildren(Lists.newArrayList());
                    }
                    pmenu.getChildren().add(menu);
                } else {
                    tlist.add(menu);
                }
            } else {
                tlist.add(menu);
            }
        });

        List<MenuTenantDto> targetMenus =  deleteNoButtonMenus(tlist, buttonMenuIds);
        return deleteNoButtonMenus(targetMenus, buttonMenuIds);
    }

    @Override
    public String getRolesByUrl(String tenantId, String reqUrl, String reqMethod) {
        if (Checker.beEmpty(tenantId) || Checker.beEmpty(reqUrl) || Checker.beEmpty(reqMethod)) {
            return Strings.EMPTY;
        }
        List<String> roleIds = this.baseMapper.listRolesByUrl(tenantId, reqUrl, reqMethod);
        //出现多条标识配置错误
        if (Checker.beNotEmpty(roleIds) && roleIds.size() > 1) {
            return Strings.EMPTY;
        }

        return Checker.beEmpty(roleIds) ? Strings.EMPTY : roleIds.get(0);
    }


    @Override
    public void addButtonPermIfNotExistByRole(String tenantId, String roleId, Set<String> buttonIds) {
        Assert.CheckArgument(tenantId);
        Assert.CheckArgument(roleId);
        if (Checker.beEmpty(buttonIds)) {
            return;
        }
        List<String> dbResourceIdList = self.listPermResourceIdsByRoles(ImmutableSet.of(roleId), ResourceTypeEnum.BUTTON, tenantId);
        if (Checker.beNotEmpty(dbResourceIdList)) {
            List<String> toAdds = buttonIds.stream().filter(b -> !dbResourceIdList.contains(b)).collect(Collectors.toList());
            saveSomeResourcesPermsByRole(tenantId, roleId, ResourceTypeEnum.BUTTON, toAdds);
        } else {
            saveSomeResourcesPermsByRole(tenantId, roleId, ResourceTypeEnum.BUTTON, Lists.newArrayList(buttonIds));
        }

    }

    @Override
    public void deleteButtonPermByRole(String tenantId, String roleId, Set<String> buttonIds) {
        Assert.CheckArgument(tenantId);
        Assert.CheckArgument(roleId);
        this.deleteSomeResourcesPermsByRole(tenantId, roleId, ResourceTypeEnum.BUTTON, Lists.newArrayList(buttonIds));
    }
}
