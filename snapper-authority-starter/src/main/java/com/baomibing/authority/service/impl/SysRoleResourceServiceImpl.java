
package com.baomibing.authority.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomibing.authority.constant.PermActionConst;
import com.baomibing.authority.constant.enums.BusinessPermScopeEnum;
import com.baomibing.authority.constant.enums.DataPermViewEnum;
import com.baomibing.authority.constant.enums.MenuTypeEnum;
import com.baomibing.authority.constant.enums.ResourceTypeEnum;
import com.baomibing.authority.dto.*;
import com.baomibing.authority.entity.SysButton;
import com.baomibing.authority.entity.SysMenu;
import com.baomibing.authority.entity.SysRoleResource;
import com.baomibing.authority.mapper.SysRoleResourceMapper;
import com.baomibing.authority.service.*;
import com.baomibing.authority.vo.AdvanceSearchVo;
import com.baomibing.authority.vo.DataPermBusinessVo;
import com.baomibing.authority.vo.DataPermColumnVo;
import com.baomibing.authority.vo.DataPermFunctionVo;
import com.baomibing.core.annotation.Action;
import com.baomibing.core.annotation.ActionConnect;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.orm.perm.ActionSelectTable;
import com.baomibing.tool.constant.RedisKeyConstant;
import com.baomibing.tool.user.UserKey;
import com.baomibing.tool.util.Checker;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Splitter;
import com.google.common.collect.*;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.baomibing.tool.constant.NumberConstant.ONE_DAY_SECONDS;
import static com.baomibing.tool.constant.PermConstant.RESOURCE_NO_NEED_ROLE;

/**
 * 角色资源服务实现类
 * 
 * @author zening
 * @since 1.0.0
 */
@Service
public class SysRoleResourceServiceImpl extends MBaseServiceImpl<SysRoleResourceMapper, SysRoleResource, RoleResourceDto> implements SysRoleResourceService {

	@Autowired private SysRoleResourceService self;// 引用自身,防止spring 代理bug
	@Autowired private SysUserRoleService userRoleService;
	@Autowired private SysResourceApiService resourceApiService;
	@Autowired private SysMenuService menuService;
	@Autowired private SysUsetRoleService usetRoleService;
	@Autowired private SysRoleService roleService;
	@Autowired private SysUserBusinessPermService userBusinessPermService;
	@Autowired private SysUsetUserEntrustService usetUserEntrustService;
	@Autowired private SysUsetGroupEntrustService usetGroupEntrustService;
	@Autowired private SysUsetUserExceptEntrustService usetUserExceptEntrustService;
	@Autowired private SysUsetGroupExceptEntrustService usetGroupExceptEntrustService;
	@Autowired private SysUserUsetService userUsetService;
	@Autowired private SysUserDataPermService userDataPermService;
	@Autowired private SysUsetDataPermService usetDataPermService;
	@Autowired private SysUserColumnPermService userColumnPermService;
	@Autowired private SysUsetColumnPermService usetColumnPermService;

	@Override
	public void refreshPrivileges(String... buttons) {
		// 已授权(按钮be_unauth = 0)
		List<ResourceApiDto> apis = this.listAllPermButtonsForGroupRoleIds(Sets.newHashSet(buttons));
		// 无权限(按钮be_unauth = 1)
		List<ResourceApiDto> unPermApisApis = this.listAllUnPermButtonsForGroupRoleIds(Sets.newHashSet(buttons));
		// 无权限角色特定标记
		unPermApisApis.forEach(p -> p.setRoleIds(RESOURCE_NO_NEED_ROLE));
		apis.addAll(unPermApisApis);
		// 删除前缀的KEY
		if (Checker.beEmpty(buttons)) {
			cacheService.deleteByKeyPrefix(RedisKeyConstant.CACHE_API_PREFIX);
		}
		// 加入缓存
		apis.stream().parallel().forEach(a -> {
			cacheService.set(UserKey.buttonPermKey(a.getReqMethod(), a.getReqUrl()), a.getRoleIds());
			cacheService.set(UserKey.buttonPermKey(a.getReqMethod(), a.getReqUrl()).replace("/api/","/wapi/"), a.getRoleIds());

		});
	}

	@Override
	public List<String> listPermResourceIdsBySuper(ResourceTypeEnum resourceType) {
		List<ResourceApiDto> resourceList = resourceApiService.listByResourceType(resourceType);
		if (Checker.beEmpty(resourceList)) {
			return Lists.newArrayList();
		}
		return resourceList.stream().map(ResourceApiDto::getResourceId).collect(Collectors.toList());
	}


	@Override
	public List<String> listPermResourceIdsByRoles(Set<String> roleIds, ResourceTypeEnum resourceType) {
		if (Checker.beEmpty(roleIds) || Checker.beNull(resourceType))
			return Lists.newArrayList();
		QueryWrapper<SysRoleResource> wrapper = new QueryWrapper<>();
		wrapper.eq("resource_type", resourceType.toString()).in("role_id", roleIds);
		List<SysRoleResource> roleResourceList = this.baseMapper.selectList(wrapper);
		if (Checker.beEmpty(roleResourceList))
			return Lists.newArrayList();
		return roleResourceList.stream().map(SysRoleResource::getResourceId).collect(Collectors.toList());
	}


	@Override
	public List<ButtonDto> listPermButtonsByRolesAndMenu(Set<String> roleIds, String menuId) {
		List<SysButton> buttonList = this.baseMapper.listPermButtonsByMenuAndRoles(Lists.newArrayList(roleIds), menuId);
		return Checker.beEmpty(buttonList) ? Lists.newArrayList()
				: new ArrayList<>(this.collectionMapper.mapCollection(buttonList, ButtonDto.class));
	}

	/**
	 * 清除角色对应资源的权限
	 *
	 * @param roleId       角色ID
	 * @param resourceType 资源类型
	 */
	private void deleteResourcePermsByRole(String roleId, ResourceTypeEnum resourceType) {
		QueryWrapper<SysRoleResource> wrapper = new QueryWrapper<>();
		wrapper.eq("role_id", roleId).eq("resource_type", resourceType);
		this.baseMapper.delete(wrapper);
	}

	/**
	 * 删除角色对应的某些资源权限
	 *
	 * @param roleId         角色ID
	 * @param resourceType   资源类型
	 * @param resourceIdList 资源ID列表
	 */
	private void deleteSomeResourcsePermsByRole(String roleId, ResourceTypeEnum resourceType,
												List<String> resourceIdList) {
		if (Checker.beEmpty(resourceIdList))
			return;
		QueryWrapper<SysRoleResource> wrapper = new QueryWrapper<>();
		wrapper.eq("role_id", roleId).eq("resource_type", resourceType).in("resource_id", resourceIdList);
		this.baseMapper.delete(wrapper);
	}

	/**
	 * 保存角色对应的某些资源权限
	 *
	 * @param roleId         角色ID
	 * @param resourceType   资源类型
	 * @param resourceIdList 资源ID列表
	 */
	private void saveSomeResourcesPermsByRole(String roleId, ResourceTypeEnum resourceType,
											  List<String> resourceIdList) {
		if (Checker.beEmpty(resourceIdList))
			return;
		List<RoleResourceDto> list = new ArrayList<>();
		resourceIdList.forEach(id -> list.add(
				new RoleResourceDto().setResourceId(id).setRoleId(roleId).setResourceType(resourceType.toString())));
		for (RoleResourceDto rr : list) {
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
	private void saveResourcePermsByRole(String roleId, Set<String> resourceIds, ResourceTypeEnum resourceType) {
		// 列表为空,清空授权
		if (Checker.beEmpty(resourceIds)) {
			deleteResourcePermsByRole(roleId, resourceType);
			return;
		}
		if (resourceType.equals(ResourceTypeEnum.MENU)) {//菜单类型，去掉非叶子节点
			List<MenuDto> menus = menuService.gets(resourceIds);
			Set<String> pids = menus.stream().map(MenuDto::getParent).filter(Checker::beNotEmpty).collect(Collectors.toSet());
			List<String> notLeafs = ListUtils.subtract(Lists.newArrayList(resourceIds), Lists.newArrayList(pids));
			resourceIds = Sets.newHashSet(notLeafs);
		}
//			List<String> itAndParents = menuService.listAllParentByIds(Sets.newHashSet(resourceIds));
//			resourceIds = itAndParents.stream().filter(t -> Checker.beNotEmpty(t)).flatMap(t -> Splitter.on(",").splitToStream(t))
//					.collect(Collectors.toSet());
////			toAddList = Lists.newArrayList(toAddSets);
//		}
		// 获取原始授权的资源ID列表
		List<String> dbResourceIdList = self.listPermResourceIdsByRoles(ImmutableSet.of(roleId), resourceType);
		/*
		 * 处理资源： 1.以当前授权资源id集合为全集，以原始资源id集合为子集，进行补集运算，结果为 需要添加的资源权限，
		 * 2.以原始资源权限id集合为全集，以当前授权资源id集合为子集，进行补集运算，结果为需要删除的资源权限
		 */
		List<String> toAddList = ListUtils.subtract(Lists.newArrayList(resourceIds), dbResourceIdList);
//		log.info("need add resouceType {}, ids {}", resourceType.toString(), toAddList);
		List<String> toDelList = ListUtils.subtract(dbResourceIdList, Lists.newArrayList(resourceIds));
//		log.info("need delete resouceType {}, ids {}", resourceType.toString(), toDelList);
		// 删除需要删除的
		this.deleteSomeResourcsePermsByRole(roleId, resourceType, toDelList);
//		if (resourceType.equals(ResourceTypeEnum.MENU)) {
//			toAddList = toAddList.stream().filter(t -> Checker.beNotEmpty(t)).collect(Collectors.toList());
//		}
		// 添加需要添加的
		this.saveSomeResourcesPermsByRole(roleId, resourceType, toAddList);
//		// 刷新权限
//		this.refreshPrivileges();
	}

	@Transactional
	@Override
	public void saveButtonPermsByMenuAndRole(String roleId, String menuId, Set<String> buttonIds) {
		Assert.CheckArgument(roleId);
		RoleDto role = roleService.getIt(roleId);
		assertBeLock(role);
		List<ButtonDto> buttons = listPermButtonsByRolesAndMenu(Sets.newHashSet(roleId), menuId);
		List<String> dbIds = buttons.stream().map(ButtonDto::getId).collect(Collectors.toList());
		List<String> toAddList = ListUtils.subtract(Lists.newArrayList(buttonIds), dbIds);
		List<String> toDelList = ListUtils.subtract(dbIds, Lists.newArrayList(buttonIds));
		// 删除需要删除的
		this.deleteSomeResourcsePermsByRole(roleId, ResourceTypeEnum.BUTTON, toDelList);
		// 添加需要添加的
		this.saveSomeResourcesPermsByRole(roleId, ResourceTypeEnum.BUTTON, toAddList);
		// 刷新权限
//		self.refreshPrivileges();
	}

	@Transactional
	@Override
	public void saveMenusPermsByRole(String roleId, Set<String> menuIds) {
		Assert.CheckArgument(roleId);
		RoleDto role = roleService.getIt(roleId);
		assertBeLock(role);
		this.saveResourcePermsByRole(roleId, menuIds, ResourceTypeEnum.MENU);

	}

//	@Override
//	public List<MenuDto> listAllPermMenusByRoles(Set<String> roleIds) {
//		List<MenuDto> list = Lists.newArrayList();
//		if (Checker.beEmpty(roleIds))
//			return list;
////		String userName = currentUserName();
////		String cachedMenu = cacheService.get(MessageFormat.format(RedisKeyConstant.KEY_USER_MENU, userName));
////		if (Checker.beNotEmpty(cachedMenu)) {
////			return JSONArray.parseArray(cachedMenu, MenuDto.class);
////		}
//
//		List<SysMenu> dbMenuList = this.baseMapper.listAllPermMenusByRoles(Lists.newArrayList(roleIds));
//		List<MenuDto> menuList = new ArrayList<>(this.collectionMapper.mapCollection(dbMenuList, MenuDto.class));
//		// 创建map结构用来临时存储对象
////		Map<String, MenuDto> map = menuList.stream()
////				.collect(Collectors.toMap(MenuDto::getId, Function.identity(), (key1,key2) -> key2));
//		// 找未分配权限的子菜单的上级菜单
////		Set<String> parentIds = menuList.stream().map(t -> t.getParent()).collect(Collectors.toSet());
////		List<String> needParentMenuIds = ListUtils.subtract(Lists.newArrayList(parentIds), Lists.newArrayList(map.keySet()));
////		List<MenuDto> needParentMenu = menuService.listAllResourceMenusByIds(Sets.newHashSet(needParentMenuIds));
////		menuList.addAll(needParentMenu);
//		Map<String, MenuDto> allMap = menuList.stream()
//				.collect(Collectors.toMap(MenuDto::getId, Function.identity(), (key1, key2) -> key2));
//		// 将列表转化成树结构
//		menuList.forEach(menu -> {
//			String pid = menu.getParent();
//			if (Checker.beNotEmpty(pid) && !"ROOT".equalsIgnoreCase(pid)) {
//				MenuDto pmenu = allMap.get(pid);
//				if (Checker.beNotNull(pmenu)) {
//					if (Checker.beEmpty(pmenu.getChildren())) {
//						pmenu.setChildren(Lists.newArrayList());
//					}
//					pmenu.getChildren().add(menu);
//					if (Checker.beEmpty(pmenu.getRedirect())) {
//						pmenu.setRedirect(menu.getPath());
//					}
//				}
//
//			} else {
//				list.add(menu);
//			}
//		});
////		cacheService.set(MessageFormat.format(RedisKeyConstant.KEY_USER_MENU, userName), JSONArray.toJSONString(list),
////				TimeConsts.HLAF_AN_HOUR_SECONDS);
//		return list;
//	}


	@Override
	public List<MenuDto> listAllPermPointMenusByRoles(Set<String> roleIds, MenuTypeEnum type) {
		List<MenuDto> list = Lists.newArrayList();
		if (Checker.beEmpty(roleIds))
			return list;
		String userName = currentUserName();
		String tag = currentUserSystemTag();
		String cacheHashKey =UserKey.userMenuKey(userName, tag);
//		if (MenuTypeEnum.BUSINESS.equals(type)) {
//			String cachedMenu = cacheService.get(cacheHashKey);
//			if (Checker.beNotEmpty(cachedMenu)) {
//				return JSONArray.parseArray(cachedMenu, MenuDto.class);
//			}
//		}
		List<SysMenu> dbMenuList = this.baseMapper.listAllPermPointMenusByRoles(Lists.newArrayList(roleIds), type.name());
		List<MenuDto> menuList = new ArrayList<>(this.collectionMapper.mapCollection(dbMenuList, MenuDto.class));
		// 创建map结构用来临时存储对象
		Map<String, MenuDto> map = menuList.stream().collect(Collectors.toMap(MenuDto::getId, Function.identity(), (key1, key2) -> key2));
		// 找未分配权限的子菜单的上级菜单
		Set<String> parentIds = menuList.stream().map(MenuDto::getParent).collect(Collectors.toSet());
		List<String> needParentMenuIds = ListUtils.subtract(Lists.newArrayList(parentIds), Lists.newArrayList(map.keySet()));
		List<String> allParentMenuIds = menuService.listAllParentByIds(Sets.newHashSet(needParentMenuIds));
		Set<String> allParents = allParentMenuIds.stream().flatMap(t -> Splitter.on(",").splitToList(t).stream())
				.filter(Checker::beNotEmpty).collect(Collectors.toSet());
		List<MenuDto> needParentMenu = menuService.listAllResourceMenusByIds(Sets.newHashSet(allParents));
		menuList.addAll(needParentMenu);
		Map<String, MenuDto> allMap = menuList.stream()
				.collect(Collectors.toMap(MenuDto::getId, Function.identity(), (key1, key2) -> key2));
		// 将列表转化成树结构
		menuList.forEach(menu -> {
			String pid = menu.getParent();
			if (Checker.beNotEmpty(pid) && !"ROOT".equalsIgnoreCase(pid)) {
				MenuDto pmenu = allMap.get(pid);
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
		Ordering<MenuDto> ordering = Ordering.from(Comparator.comparingInt(MenuDto::getPriority));
		list.sort(ordering);
		for (MenuDto menuDto : list) {
			List<MenuDto> children = menuDto.getChildren();
			if (Checker.beNotEmpty(children)) {
				children.sort(ordering);
			}
		}
		if (MenuTypeEnum.BUSINESS.equals(type) && Checker.beNotEmpty(list)) {
			cacheService.set(cacheHashKey, JSONArray.toJSONString(list), ONE_DAY_SECONDS);
		}
		return list;
	}

	@Override
	public List<MenuDto> listAllPermMenusForSuper(MenuTypeEnum type) {
		List<MenuDto> list = Lists.newArrayList();

		List<SysMenu> dbMenuList = this.baseMapper.listAllPermMenusForSuper(type.name());
		List<MenuDto> menuList = new ArrayList<>(this.collectionMapper.mapCollection(dbMenuList, MenuDto.class));
		Map<String, MenuDto> allMap = menuList.stream()
				.collect(Collectors.toMap(MenuDto::getId, Function.identity(), (key1, key2) -> key2));
		// 将列表转化成树结构
		menuList.forEach(menu -> {
			String pid = menu.getParent();
			if (Checker.beNotEmpty(pid) && !"ROOT".equalsIgnoreCase(pid)) {
				MenuDto pmenu = allMap.get(pid);
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
//		cacheService.set(MessageFormat.format(RedisKeyConstant.KEY_USER_MENU, userName), JSONArray.toJSONString(list),
//				TimeConsts.HLAF_AN_HOUR_SECONDS);
		return list;
	}

//	@Override
//	public List<MenuDto> listAllMenusAndButtonsForGrant() {
//		List<MenuDto> tlist = Lists.newArrayList();
//		List<MenuDto> vlist = Lists.newArrayList();
//		List<SysMenu> list = this.baseMapper.listAllMenusAndButtonsForGrant();
//		list.forEach(m -> {
//			List<CheckGroupWrap> wrapList = Lists.newArrayList();
//			if (Checker.beNotEmpty(m.getButtons())) {
//				List<String> buttonList = Splitter.on(",").splitToList(m.getButtons());
//				buttonList.forEach(b -> {
//					List<String> l = Splitter.on("-").splitToList(b);
//					if (Checker.beNotEmpty(l) && l.size() > 1) {
//						wrapList.add(new CheckGroupWrap(l.get(1), l.get(0)));
//					}
//				});
//			}
//			vlist.add(new MenuDto().setId(m.getId()).setName(m.getMenuName()).setParent(m.getParentId())
//					.setButtonWraps(wrapList));
//		});
//		// 创建map结构用来临时存储对象
//		Map<String, MenuDto> map = vlist.stream()
//				.collect(Collectors.toMap(MenuDto::getId, Function.identity(), (key1, key2) -> key2));
//		// 将列表转化成树结构
//		vlist.forEach(menu -> {
//			String pid = menu.getParent();
//			if (Checker.beNotEmpty(pid)) {
//				MenuDto pmenu = map.get(pid);
//				if (Checker.beNotNull(pmenu)) {
//					if (Checker.beEmpty(pmenu.getChildren())) {
//						pmenu.setChildren(Lists.newArrayList());
//					}
//					pmenu.getChildren().add(menu);
//				}
//			} else {
//				tlist.add(menu);
//			}
//		});
//		return tlist;
//	}


	@Action(value = PermActionConst.ROLE_TREE_ALL_MENUS)
	@ActionConnect(value = "listAllMenusForGrant")
	@Override
	public List<MenuDto> listAllMenusForGrant() {
		List<MenuDto> tlist = Lists.newArrayList();
		List<MenuDto> vlist = Lists.newArrayList();
		List<SysMenu> list = this.baseMapper.listAllMenusForGrant();
		Set<String> roleIds = currentUserRoles();
		if (Checker.beNotEmpty(roleIds)) {
			List<SysMenu> ownMenus = baseMapper.listAllRoleMenusForGrant(roleIds);
			Set<String> menuIds = list.stream().map(SysMenu::getId).collect(Collectors.toSet());
			List<SysMenu> needAdd = ownMenus.stream().filter(m -> !menuIds.contains(m.getId())).collect(Collectors.toList());
			list.addAll(needAdd);
		}
		Set<String> ids = list.stream().map(SysMenu::getId).collect(Collectors.toSet());
		Set<String> parentIds = list.stream().map(SysMenu::getParentId).filter(t -> !ids.contains(t)).collect(Collectors.toSet());
		List<MenuDto> parentMenus = menuService.gets(parentIds);
		parentMenus.forEach(m -> {
			vlist.add(new MenuDto().setId(m.getId()).setName(m.getName()).setParent(m.getParent()).setMenuType(m.getMenuType()));
		});
		list.forEach(m -> {
//			List<CheckGroupWrap> wrapList = Lists.newArrayList();
//			if (Checker.beNotEmpty(m.getButtons())) {
//				List<String> buttonList = Splitter.on(",").splitToList(m.getButtons());
//				buttonList.forEach(b -> {
//					List<String> l = Splitter.on("-").splitToList(b);
//					if (Checker.beNotEmpty(l) && l.size() > 1) {
//						wrapList.add(new CheckGroupWrap(l.get(1), l.get(0)));
//					}
//				});
//			}
			vlist.add(new MenuDto().setId(m.getId()).setName(m.getMenuName()).setParent(m.getParentId()).setMenuType(m.getMenuType()));
		});
		// 创建map结构用来临时存储对象
		Map<String, MenuDto> map = vlist.stream().collect(Collectors.toMap(MenuDto::getId, Function.identity(), (key1, key2) -> key2));
		// 将列表转化成树结构
		vlist.forEach(menu -> {
			String pid = menu.getParent();
			if (Checker.beNotEmpty(pid)) {
				MenuDto pmenu = map.get(pid);
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


	@Action(PermActionConst.ROLE_LIST_ALL_BUTTONS_BY_MENU)
	@ActionConnect(value = "listAllButtonsForGrant")
	@Override
	public List<ButtonDto> listAllButtonsByMenuForGrant(String menuId) {
		if (Checker.beEmpty(menuId)) {
			return emptyList();
		}
		List<ButtonDto> buttons =  Lists.newArrayList(collectionMapper.mapCollection(baseMapper.listAllButtonsForGrant(menuId), ButtonDto.class));
		Set<String> roleIds = currentUserRoles();
		if (Checker.beNotEmpty(roleIds)) {
			List<ButtonDto> ownButtons = Lists.newArrayList(collectionMapper.mapCollection(baseMapper.listAllRoleButtonsForGrant(menuId, roleIds), ButtonDto.class));
			Set<String> buttonIds = buttons.stream().map(b -> b.getId()).collect(Collectors.toSet());
			List<ButtonDto> needAddButtons = ownButtons.stream().filter(b -> !buttonIds.contains(b.getId())).collect(Collectors.toList());
			buttons.addAll(needAddButtons);
		}
		return buttons;

	}

	@Override
	public List<ResourceApiDto> listAllPermButtonsForGroupRoleIds(Set<String> buttonIds) {
		return Lists.newArrayList(this.collectionMapper.mapCollection(this.baseMapper.listAllPermButtonsForGroupRoleIds(buttonIds), ResourceApiDto.class));
	}

	@Override
	public List<ResourceApiDto> listAllUnPermButtonsForGroupRoleIds(Set<String> buttonIds) {
		return Lists.newArrayList(this.collectionMapper.mapCollection(this.baseMapper.listAllUnPermButtonsForGroupRoleIds(buttonIds), ResourceApiDto.class));
	}

	@Override
	public List<ButtonDto> listPermButtonsForBusinessPermByGroupAndUser(String orgId, String userId) {
		if (Checker.beEmpty(userId)) {
			return Lists.newArrayList();
		}
		List<SysButton> buttons = this.baseMapper.listPermButtonsForBusinessPermByGroupAndUser(orgId, userId);
		return Checker.beEmpty(buttons) ? Lists.newArrayList()
				: Lists.newArrayList(this.collectionMapper.mapCollection(buttons, ButtonDto.class));
	}

	@Override
	public List<MenuDto> listPermMenusAndButtonsForBusinessPermByUser(String orgId, String userId) {
		if (Checker.beEmpty(userId) || Checker.beEmpty(orgId)) {
			return Lists.newArrayList();
		}
		List<RoleDto> roles = userRoleService.listRolesByGroupAndUser(orgId, userId);
		Set<String> roleIds = roles.stream().map(RoleDto::getId).collect(Collectors.toSet());
		// 取角色对应的菜单列表
		List<SysMenu> dbMenuList = this.baseMapper.listAllPermMenusByRoles(Lists.newArrayList(roleIds));
		List<MenuDto> menuList = new ArrayList<>(this.collectionMapper.mapCollection(dbMenuList, MenuDto.class));
		// 创建map结构用来临时存储对象
		Map<String, MenuDto> map = menuList.stream().collect(Collectors.toMap(MenuDto::getId, Function.identity(), (key1, key2) -> key2));
		// 找未分配权限的子菜单的上级菜单
		Set<String> parentIds = menuList.stream().map(MenuDto::getParent).collect(Collectors.toSet());
		List<String> needParentMenuIds = ListUtils.subtract(Lists.newArrayList(parentIds), Lists.newArrayList(map.keySet()));
		List<String> allParentMenuIds = menuService.listAllParentByIds(Sets.newHashSet(needParentMenuIds));
		Set<String> allParents = allParentMenuIds.stream().flatMap(t -> Splitter.on(",").splitToStream(t))
				.filter(Checker::beNotEmpty).collect(Collectors.toSet());
		List<MenuDto> needParentMenu = menuService.listAllResourceMenusByIds(Sets.newHashSet(allParents));
		menuList.addAll(needParentMenu);
		//临时调整测试
		String cacheKey = RedisKeyConstant.CACHE_ACTION_CONNECT_PREFIX;
		Set<String> keys = cacheService.keys(cacheKey);
		// 取用户对应权限的按钮列表
		List<ButtonDto> buttonList = listPermButtonsForBusinessPermByGroupAndUser(orgId, userId);
		List<ButtonDto> targetButtons = buttonList.stream()
				.filter(b -> Checker.beNotEmpty(b.getPermAction()) && keys.contains(cacheKey + b.getPermAction()))
				.collect(Collectors.toList());

		// 创建map结构用来临时存储对象
		Map<String, MenuDto> allMenuMap = menuList.stream()
				.collect(Collectors.toMap(MenuDto::getId, Function.identity(), (key1, key2) -> key2));
		List<MenuDto> tlist = Lists.newArrayList();

		Set<String> buttonMenuIds = Sets.newHashSet();
		// 遍历按钮，将按钮加入到菜单中
		targetButtons.forEach(b -> {
			String mid = b.getMenuId();
			MenuDto menu = map.get(mid);
			if (Checker.beNotNull(menu)) {
				buttonMenuIds.add(mid);
				if (Checker.beEmpty(menu.getChildren())) {
					menu.setChildren(Lists.newArrayList());
				}
				menu.getChildren().add(new MenuDto().setId(b.getId()).setName("[" + b.getSubMenu() + "]-" + b.getButtonName())
						.setPermScope(b.getPermScope()).setTag(ResourceTypeEnum.BUTTON.name())
						.setDataPerm(b.getDataPerm()).setColumnPerm(b.getColumnPerm())
						.setPermId(b.getPermId()).setPermStartTime(b.getPermStartTime())
						.setPermEndTime(b.getPermEndTime()));
			}
		});

		// 将列表转化成树结构
		menuList.forEach(menu -> {
			String pid = menu.getParent();
			if (Checker.beNotEmpty(pid)) {
				MenuDto pmenu = allMenuMap.get(pid);
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

		List<MenuDto> targetMenus =  deleteNoButtonMenus(tlist, buttonMenuIds);
		return deleteNoButtonMenus(targetMenus, buttonMenuIds);
	}

	private List<MenuDto> deleteNoButtonMenus(List<MenuDto> menuList, Set<String> buttonMenuIds) {
		if (Checker.beEmpty(menuList)) return menuList;
		Iterator<MenuDto> it = menuList.iterator();
		while (it.hasNext()) {
			MenuDto m = it.next();
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
	public List<MenuDto> listPermMenusAndButtonsForBusinessPermByUset(String usetId) {
		if (Checker.beEmpty(usetId)) {
			return Lists.newArrayList();
		}
		Set<String> roleIds = usetRoleService.listRolesByUset(usetId);
		// 取角色对应的菜单列表
		List<SysMenu> dbMenuList = this.baseMapper.listAllPermMenusByRoles(Lists.newArrayList(roleIds));
		List<MenuDto> menuList = new ArrayList<>(this.collectionMapper.mapCollection(dbMenuList, MenuDto.class));
		// 创建map结构用来临时存储对象
		Map<String, MenuDto> map = menuList.stream().collect(Collectors.toMap(MenuDto::getId, Function.identity(), (key1, key2) -> key2));
		// 找未分配权限的子菜单的上级菜单
		Set<String> parentIds = menuList.stream().map(MenuDto::getParent).collect(Collectors.toSet());
		List<String> needParentMenuIds = ListUtils.subtract(Lists.newArrayList(parentIds), Lists.newArrayList(map.keySet()));
		List<String> allParentMenuIds = menuService.listAllParentByIds(Sets.newHashSet(needParentMenuIds));
		Set<String> allParents = allParentMenuIds.stream().flatMap(t -> Splitter.on(",").splitToStream(t))
				.filter(Checker::beNotEmpty).collect(Collectors.toSet());
		List<MenuDto> needParentMenu = menuService.listAllResourceMenusByIds(Sets.newHashSet(allParents));
		menuList.addAll(needParentMenu);

		// 取用户对应权限的按钮列表
		List<SysButton> buttons = baseMapper.listPermButtonsForBusinessPermByUset(usetId);

		List<ButtonDto> buttonList = Checker.beEmpty(buttons) ? Lists.newArrayList()
				: Lists.newArrayList(this.collectionMapper.mapCollection(buttons, ButtonDto.class));

		//临时调整测试
		String cacheKey = RedisKeyConstant.CACHE_ACTION_CONNECT_PREFIX;
		Set<String> keys = cacheService.keys(cacheKey);
		List<ButtonDto> targetButtons = buttonList.stream()
				.filter(b -> Checker.beNotEmpty(b.getPermAction()) && keys.contains(cacheKey + b.getPermAction()))
				.collect(Collectors.toList());


//        List<ButtonDto> targetButtons = buttonList.stream()
//                .filter(b -> cacheService.hasKey(com.baomibing.tool.user.RedisKeyConstant.CACHE_ACTION_CONNECT_PREFIX + b.getPermAction()))
//                .collect(Collectors.toList());
//		Set<String> menuIds = buttonList.stream().map(b -> b.getMenuId()).collect(Collectors.toSet());

		// 创建map结构用来临时存储对象
		Map<String, MenuDto> allMenuMap = menuList.stream()
				.collect(Collectors.toMap(MenuDto::getId, Function.identity(), (key1, key2) -> key2));
		List<MenuDto> tlist = Lists.newArrayList();

		Set<String> buttonMenuIds = Sets.newHashSet();
		// 遍历按钮，将按钮加入到菜单中
		targetButtons.forEach(b -> {
			String mid = b.getMenuId();
			MenuDto menu = map.get(mid);
			if (Checker.beNotNull(menu)) {
				buttonMenuIds.add(mid);
				if (Checker.beEmpty(menu.getChildren())) {
					menu.setChildren(Lists.newArrayList());
				}
				menu.getChildren().add(new MenuDto().setId(b.getId()).setName("[" + b.getSubMenu() + "]-" + b.getButtonName())
						.setPermScope(b.getPermScope()).setTag(ResourceTypeEnum.BUTTON.name())
						.setDataPerm(b.getDataPerm()).setColumnPerm(b.getColumnPerm())
						.setPermId(b.getPermId()).setPermStartTime(b.getPermStartTime())
						.setPermEndTime(b.getPermEndTime()));
			}
		});

		// 将列表转化成树结构
		menuList.forEach(menu -> {
			String pid = menu.getParent();
			if (Checker.beNotEmpty(pid)) {
				MenuDto pmenu = allMenuMap.get(pid);
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

		List<MenuDto> targetMenus =  deleteNoButtonMenus(tlist, buttonMenuIds);
		return deleteNoButtonMenus(targetMenus, buttonMenuIds);

//		return tlist.stream().filter(m -> {
//			if (buttonMenuIds.contains(m.getId())) {
//				return true;
//			}
//			if (Checker.beEmpty(m.getChildren())) {
//				return false;
//			}
//			return m.getChildren().stream().anyMatch(mc -> buttonMenuIds.contains(mc.getId()) ||
//					(Checker.beNotEmpty(mc.getChildren()) && mc.getChildren().stream().anyMatch(mcc -> buttonMenuIds.contains(mcc.getId()))));
//		}).collect(Collectors.toList());
		//================数据最终结构==========================
		//  name             permScope        tag
		// |-系统管理            -             MENU
		//    |-用户管理         -             MENU
		//       |-查询     CURRENT_USER      PERM
		//       |-删除     CURRENT_USER      PERM
		//       |-修改     CURRENT_USER      PERM
		//       |-分配角色  CURRENT_USER      PERM
//        return tlist;
	}

	@Override
	public void deleteByRoles(Set<String> roles) {
		Assert.CheckArgument(roles);
		this.baseMapper.delete(lambdaQuery().in(SysRoleResource::getRoleId, roles));
	}

	@Override
	public void deleteByResources(Set<String> resourceIds, ResourceTypeEnum resourceType) {
		Assert.CheckArgument(resourceIds);
		Assert.CheckArgument(resourceType);
		this.baseMapper.delete(lambdaQuery().in(SysRoleResource::getResourceId, resourceIds).eq(SysRoleResource::getResourceType, resourceType.name()));
	}

	@Override
	public List<MenuDto> listPermMenusAndButtonsByRoleIds(Set<String> roleIds, boolean beFilterPerm, boolean beFilterNoPerm) {
		if (Checker.beEmpty(roleIds)) {
			return emptyList();
		}
		// 取角色对应的菜单列表
		List<SysMenu> dbMenuList = this.baseMapper.listAllPermMenusByRoles(Lists.newArrayList(roleIds));
		List<MenuDto> menuList = new ArrayList<>(this.collectionMapper.mapCollection(dbMenuList, MenuDto.class));
		// 创建map结构用来临时存储对象
		Map<String, MenuDto> map = menuList.stream().collect(Collectors.toMap(MenuDto::getId, Function.identity(), (key1, key2) -> key2));
		// 找未分配权限的子菜单的上级菜单
		Set<String> parentIds = menuList.stream().map(MenuDto::getParent).collect(Collectors.toSet());
		List<String> needParentMenuIds = ListUtils.subtract(Lists.newArrayList(parentIds), Lists.newArrayList(map.keySet()));
		List<String> allParentMenuIds = menuService.listAllParentByIds(Sets.newHashSet(needParentMenuIds));
		Set<String> allParents = allParentMenuIds.stream().flatMap(t -> Splitter.on(",").splitToStream(t))
				.filter(Checker::beNotEmpty).collect(Collectors.toSet());
		List<MenuDto> needParentMenu = menuService.listAllResourceMenusByIds(Sets.newHashSet(allParents));
		menuList.addAll(needParentMenu);
		//临时调整测试
		String cacheKey = RedisKeyConstant.CACHE_ACTION_CONNECT_PREFIX;
		Set<String> keys = cacheService.keys(cacheKey);
		// 取用户对应权限的按钮列表
		List<SysButton> buttons = baseMapper.listPermButtonsByRoles(Lists.newArrayList(roleIds));
		List<ButtonDto> buttonList = Checker.beEmpty(buttons) ? Lists.newArrayList()
				: Lists.newArrayList(this.collectionMapper.mapCollection(buttons, ButtonDto.class));
		if (beFilterPerm) {
			buttonList = buttonList.stream()
					.filter(b -> Checker.beNotEmpty(b.getPermAction()) && keys.contains(cacheKey + b.getPermAction()))
					.collect(Collectors.toList());
		}
		if (!beFilterNoPerm) {
			buttonList = buttonList.stream()
					.filter(b -> Boolean.FALSE.equals(b.getBeUnauth())).collect(Collectors.toList());
		}
		// 创建map结构用来临时存储对象
		Map<String, MenuDto> allMenuMap = menuList.stream()
				.collect(Collectors.toMap(MenuDto::getId, Function.identity(), (key1, key2) -> key2));
		List<MenuDto> tlist = Lists.newArrayList();

		Set<String> buttonMenuIds = Sets.newHashSet();
		// 遍历按钮，将按钮加入到菜单中
		buttonList.forEach(b -> {
			String mid = b.getMenuId();
			MenuDto menu = map.get(mid);
			if (Checker.beNotNull(menu)) {
				buttonMenuIds.add(mid);
				if (Checker.beEmpty(menu.getChildren())) {
					menu.setChildren(Lists.newArrayList());
				}
				menu.getChildren().add(new MenuDto().setId(b.getId()).setName(b.getButtonName())
						.setTag(ResourceTypeEnum.BUTTON.name()).setBeUnauth(b.getBeUnauth()).setPermId(b.getPermId()));
			}
		});

		// 将列表转化成树结构
		menuList.forEach(menu -> {
			menu.setTag(ResourceTypeEnum.MENU.name());
			String pid = menu.getParent();
			if (Checker.beNotEmpty(pid)) {
				MenuDto pmenu = allMenuMap.get(pid);
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

		List<MenuDto> targetMenus =  deleteNoButtonMenus(tlist, buttonMenuIds);
		return deleteNoButtonMenus(targetMenus, buttonMenuIds);
	}

	@Override
	public List<MenuDto> listActionMenusAndButtonsByRoleIds(Set<String> roleIds) {
		if (Checker.beEmpty(roleIds)) {
			return emptyList();
		}
		// 取角色对应的菜单列表
		List<SysMenu> dbMenuList = this.baseMapper.listAllPermMenusByRoles(Lists.newArrayList(roleIds));
		List<MenuDto> menuList = new ArrayList<>(this.collectionMapper.mapCollection(dbMenuList, MenuDto.class));
		// 创建map结构用来临时存储对象
		Map<String, MenuDto> map = menuList.stream().collect(Collectors.toMap(MenuDto::getId, Function.identity(), (key1, key2) -> key2));
		// 找未分配权限的子菜单的上级菜单
		Set<String> parentIds = menuList.stream().map(MenuDto::getParent).collect(Collectors.toSet());
		List<String> needParentMenuIds = ListUtils.subtract(Lists.newArrayList(parentIds), Lists.newArrayList(map.keySet()));
		List<String> allParentMenuIds = menuService.listAllParentByIds(Sets.newHashSet(needParentMenuIds));
		Set<String> allParents = allParentMenuIds.stream().flatMap(t -> Splitter.on(",").splitToStream(t))
				.filter(Checker::beNotEmpty).collect(Collectors.toSet());
		List<MenuDto> needParentMenu = menuService.listAllResourceMenusByIds(Sets.newHashSet(allParents));
		menuList.addAll(needParentMenu);
		//临时调整测试
		String cacheKey = RedisKeyConstant.CACHE_ACTION_CONNECT_PREFIX;
		Set<String> keys = cacheService.keys(cacheKey);

		// 取用户对应权限的按钮列表
		List<SysButton> buttons = baseMapper.listPermButtonsByRoles(Lists.newArrayList(roleIds));
		List<ButtonDto> buttonList = Checker.beEmpty(buttons) ? Lists.newArrayList()
				: Lists.newArrayList(this.collectionMapper.mapCollection(buttons, ButtonDto.class));

		//临时调整测试
		List<ButtonDto> targetButtons = buttonList.stream()
				.filter(b -> keys.contains(cacheKey + b.getPermAction()))
				.collect(Collectors.toList());

		// 创建map结构用来临时存储对象
		Map<String, MenuDto> allMenuMap = menuList.stream()
				.collect(Collectors.toMap(MenuDto::getId, Function.identity(), (key1, key2) -> key2));
		List<MenuDto> tlist = Lists.newArrayList();

		Set<String> buttonMenuIds = Sets.newHashSet();
		// 遍历按钮，将按钮加入到菜单中
		targetButtons.forEach(b -> {
			String mid = b.getMenuId();
			MenuDto menu = map.get(mid);
			if (Checker.beNotNull(menu)) {
				buttonMenuIds.add(mid);
				if (Checker.beEmpty(menu.getChildren())) {
					menu.setChildren(Lists.newArrayList());
				}
				menu.getChildren().add(new MenuDto().setId(b.getId()).setName(b.getButtonName())
						.setPermScope(b.getPermScope()).setTag(ResourceTypeEnum.BUTTON.name())
						.setDataPerm(b.getDataPerm()).setColumnPerm(b.getColumnPerm())
						.setPermId(b.getPermId()).setPermStartTime(b.getPermStartTime())
						.setPermEndTime(b.getPermEndTime()));
			}
		});

		// 将列表转化成树结构
		menuList.forEach(menu -> {
			menu.setTag(ResourceTypeEnum.MENU.name());
			String pid = menu.getParent();
			if (Checker.beNotEmpty(pid)) {
				MenuDto pmenu = allMenuMap.get(pid);
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

		List<MenuDto> targetMenus =  deleteNoButtonMenus(tlist, buttonMenuIds);
		return deleteNoButtonMenus(targetMenus, buttonMenuIds);
	}

	private DataPermFunctionVo mergePermScope(List<SysButton> buttons) {
		DataPermFunctionVo dp = new DataPermFunctionVo();
		String scope = BusinessPermScopeEnum.CURRENT_USER.name();
		Map<String ,Integer> scopeMap = Maps.newHashMap();
		scopeMap.put(BusinessPermScopeEnum.CURRENT_USER.name(), 1);
		scopeMap.put(BusinessPermScopeEnum.CUSTOMER_SPECIFIED.name(), 2);
		scopeMap.put(BusinessPermScopeEnum.SCOPE_ALL.name(), 3);
		Date start = null, end = null;
		if (Checker.beNotEmpty(buttons)) {
			for (SysButton button : buttons) {
				if (Checker.beNotEmpty(button.getPermScope())) {
					if (scopeMap.get(button.getPermScope()) > scopeMap.get(scope)) {
						scope = button.getPermScope();
					}
				}
				if (Checker.beNotNull(button.getPermStartTime())) {
					if (Checker.beNull(start)) {
						start = button.getPermStartTime();
					} else if (button.getPermStartTime().before(start)) {
						start = button.getPermStartTime();
					}
				}
				if (Checker.beNotNull(button.getPermEndTime())) {
					if (Checker.beNull(end)) {
						end = button.getPermEndTime();
					} else if (button.getPermEndTime().after(end)) {
						end = button.getPermEndTime();
					}
				}
			}
		}
		dp.setPermScope(scope);
		dp.setPermStartTime(start);
		dp.setPermEndTime(end);
		return dp;
	}

	private void mergeEntrusts(DataPermFunctionVo dp, List<String> userIdEntrusts, List<String> groupIdEntrusts,
							   List<String> exceptUserIdEntrusts, List<String> exceptGroupIdEntrusts) {
		Set<String> entrusts = Sets.newHashSet();
		Set<String> exceptEntrusts = Sets.newHashSet();
		if (Checker.beNotEmpty(userIdEntrusts)) {
			userIdEntrusts.removeAll(exceptUserIdEntrusts);
			Set<String> okUserIdEntrusts = userIdEntrusts.stream().filter(t -> exceptGroupIdEntrusts.stream().anyMatch(e -> e.startsWith(t))).collect(Collectors.toSet());
			Set<String> filterGroupUserEntrusts = okUserIdEntrusts.stream().filter(t -> groupIdEntrusts.stream().anyMatch(e -> e.startsWith(t))).collect(Collectors.toSet());
			entrusts.addAll(filterGroupUserEntrusts);
		}
		if (Checker.beNotEmpty(groupIdEntrusts)) {
			groupIdEntrusts.removeAll(exceptGroupIdEntrusts);
			entrusts.addAll(groupIdEntrusts);
		}

		if (Checker.beNotEmpty(exceptUserIdEntrusts)) {
			Set<String> okExceptUserIdEntrusts = exceptUserIdEntrusts.stream().filter(t -> exceptGroupIdEntrusts.stream().anyMatch(e -> e.startsWith(t))).collect(Collectors.toSet());
			exceptEntrusts.addAll(okExceptUserIdEntrusts);
		}
		if (Checker.beNotEmpty(exceptGroupIdEntrusts)) {
			exceptEntrusts.addAll(exceptGroupIdEntrusts);
		}
		dp.setEntrustIds(entrusts);
		dp.setExceptEntrustIds(exceptEntrusts);
	}

	@Override
	public DataPermFunctionVo getFunctionDataPerm(String viewType, String orgId, String permId, String usetId) {
		if (Checker.beEmpty(viewType) || Checker.beEmpty(orgId) || Checker.beEmpty(permId)) {
			return null;
		}
		DataPermViewEnum dpv = EnumUtils.getEnum(DataPermViewEnum.class, viewType);
		if (Checker.beNull(dpv)) {
			return null;
		}

		DataPermFunctionVo dp = new DataPermFunctionVo();
		if (dpv == DataPermViewEnum.USER) {
			List<SysButton> buttons = baseMapper.listUserPermScopeByPerm(orgId, currentUserId(), permId);
			dp = mergePermScope(buttons);
			List<String> userIdEntrusts = userBusinessPermService.listUserEntrustIdsByPerm(orgId, currentUserId(), permId);
			List<String> groupIdEntrusts = userBusinessPermService.listGroupEntrustIdsByPerm(orgId, currentUserId(), permId);

			List<String> exceptUserIdEntrusts = userBusinessPermService.listUserExceptEntrustIdsByPerm(orgId, currentUserId(), permId);
			List<String> exceptGroupIdEntrusts = userBusinessPermService.listGroupExceptEntrustIdsByPerm(orgId, currentUserId(), permId);

			mergeEntrusts(dp, userIdEntrusts, groupIdEntrusts, exceptUserIdEntrusts, exceptGroupIdEntrusts);

		} else if (dpv == DataPermViewEnum.USET) {
			Set<String> usetIds = Sets.newHashSet();
			if (DataPermViewEnum.ALL.name().equals(usetId)) {
				List<UsetDto> usets = userUsetService.listUsetsByGroupAndUser(orgId, currentUserId());
				if (Checker.beNotEmpty(usets)) {
					usetIds.addAll(usets.stream().map(UsetDto::getId).collect(Collectors.toSet()));
				}
			} else {
				usetIds.add(usetId);
			}
			if (Checker.beEmpty(usetIds)) {
				return dp;
			}
			List<SysButton> buttons = baseMapper.listUsetPermScopeByPerm(usetIds, permId);

			dp = mergePermScope(buttons);

			List<String> userIdEntrusts = usetUserEntrustService.listEntrustUserCodesByUsetAndPerm(usetIds, permId);
			List<GroupDto> groups = usetGroupEntrustService.listEntrustGroupsByUsetAndPerm(usetIds, permId);
			List<String> groupIds = groups.stream().map(GroupDto::getId).collect(Collectors.toList());

			List<String> exceptUserIdEntrusts = usetUserExceptEntrustService.listEntrustUserCodesByUsetAndPerm(usetIds, permId);
			List<GroupDto> exceptGroups = usetGroupExceptEntrustService.listEntrustGroupsByUsetAndPerm(usetIds, permId);
			List<String> exceptGroupIds = exceptGroups.stream().map(GroupDto::getId).collect(Collectors.toList());

			mergeEntrusts(dp, userIdEntrusts, groupIds, exceptUserIdEntrusts, exceptGroupIds);

		} else if (dpv == DataPermViewEnum.ALL) {
			List<SysButton> buttons = baseMapper.listUserPermScopeByPerm(orgId, currentUserId(), permId);
			List<String> userIdEntrusts = userBusinessPermService.listUserEntrustIdsByPerm(orgId, currentUserId(), permId);
			List<String> groupIdEntrusts = userBusinessPermService.listGroupEntrustIdsByPerm(orgId, currentUserId(), permId);

			List<String> exceptUserIdEntrusts = userBusinessPermService.listUserExceptEntrustIdsByPerm(orgId, currentUserId(), permId);
			List<String> exceptGroupIdEntrusts = userBusinessPermService.listGroupExceptEntrustIdsByPerm(orgId, currentUserId(), permId);


			List<UsetDto> usets = userUsetService.listUsetsByGroupAndUser(orgId, currentUserId());
			if (Checker.beEmpty(usets)) {
				dp = mergePermScope(buttons);
				mergeEntrusts(dp, userIdEntrusts, groupIdEntrusts, exceptUserIdEntrusts, exceptGroupIdEntrusts);
				return dp;
			}
			Set<String> usetIds = usets.stream().map(UsetDto::getId).collect(Collectors.toSet());
			List<SysButton> usetButtons = baseMapper.listUsetPermScopeByPerm(usetIds, permId);

			buttons.addAll(usetButtons);
			dp = mergePermScope(buttons);

			List<String> usetUserIdEntrusts = usetUserEntrustService.listEntrustUserCodesByUsetAndPerm(usetIds, permId);
			List<GroupDto> groups = usetGroupEntrustService.listEntrustGroupsByUsetAndPerm(usetIds, permId);
			List<String> groupIds = groups.stream().map(GroupDto::getId).collect(Collectors.toList());

			List<String> usetExceptUserIdEntrusts = usetUserExceptEntrustService.listEntrustUserCodesByUsetAndPerm(usetIds, permId);
			List<GroupDto> exceptGroups = usetGroupExceptEntrustService.listEntrustGroupsByUsetAndPerm(usetIds, permId);
			List<String> exceptGroupIds = exceptGroups.stream().map(GroupDto::getId).collect(Collectors.toList());

			userIdEntrusts.addAll(usetUserIdEntrusts);
			groupIdEntrusts.addAll(groupIds);
			exceptUserIdEntrusts.addAll(usetExceptUserIdEntrusts);
			exceptGroupIdEntrusts.addAll(exceptGroupIds);
			mergeEntrusts(dp, userIdEntrusts, groupIdEntrusts, exceptUserIdEntrusts, exceptGroupIdEntrusts);

		}
		return dp;
	}


	private List<DataPermBusinessVo> mergeBusinessPerm(List<DataPermBusinessVo> perms) {

		if (Checker.beEmpty(perms)) {
			return perms;
		}
		Map<Boolean, List<DataPermBusinessVo>> permMap = perms.stream().collect(Collectors.groupingBy(DataPermBusinessVo::getBeOrCondition));
		List<DataPermBusinessVo> list = Lists.newArrayList();
		//TODO 分类合并
		permMap.forEach((k, v) -> {
			DataPermBusinessVo dbv = new DataPermBusinessVo();

			Date start = null, end = null;
			for (DataPermBusinessVo db : v) {
				if (Checker.beEmpty(dbv.getSearchExpresses())) {
					dbv.setSearchExpresses(Lists.newArrayList());
				}
				if (Checker.beNotEmpty(db.getSearchExpresses())) {
					dbv.getSearchExpresses().addAll(db.getSearchExpresses());
				}
				if (Checker.beNull(dbv.getBeOrCondition())) {
					dbv.setBeOrCondition(db.getBeOrCondition());
				}
				if (Checker.beNotNull(db.getPermStartTime())) {
					if (Checker.beNull(start)) {
						start = db.getPermStartTime();
					} else if (db.getPermStartTime().before(start)) {
						start = db.getPermStartTime();
					}
				}
				if (Checker.beNotNull(db.getPermEndTime())) {
					if (Checker.beNull(end)) {
						end = db.getPermEndTime();
					} else if (db.getPermEndTime().after(end)) {
						end = db.getPermEndTime();
					}
				}
			}
			dbv.setPermStartTime(start).setPermEndTime(end);
			list.add(dbv);

		});
		return list;
	}

	@Override
	public List<DataPermBusinessVo> getBusinessDataPerm(String viewType, String orgId, String permId, String usetId) {
		if (Checker.beEmpty(viewType) || Checker.beEmpty(orgId) || Checker.beEmpty(permId)) {
			return null;
		}

		DataPermViewEnum dpv = EnumUtils.getEnum(DataPermViewEnum.class, viewType);
		if (Checker.beNull(dpv)) {
			return null;
		}
		List<DataPermBusinessVo> list = Lists.newArrayList();
		if (dpv == DataPermViewEnum.USER) {
			UserDataPermDto perm = userDataPermService.getUserDataPerm(currentUserId(), orgId, permId);
			if (Checker.beNotNull(perm)) {
				perm.setSearchExpresses(JSONArray.parseArray(perm.getPermExpress(), AdvanceSearchVo.class));
				DataPermBusinessVo db = new DataPermBusinessVo();
				db.setPermExpress(perm.getPermExpress());
				db.setPermEndTime(perm.getPermEndTime());
				db.setPermStartTime(perm.getPermStartTime());
				db.setBeOrCondition(perm.getBeOrCondition());
				db.setSearchExpresses(perm.getSearchExpresses());
				list.add(db);
			}

		} else if (dpv == DataPermViewEnum.USET) {
			Set<String> usetIds = Sets.newHashSet();
			if (DataPermViewEnum.ALL.name().equals(usetId)) {
				List<UsetDto> usets = userUsetService.listUsetsByGroupAndUser(orgId, currentUserId());
				if (Checker.beNotEmpty(usets)) {
					usetIds.addAll(usets.stream().map(UsetDto::getId).collect(Collectors.toSet()));
				}
			} else {
				usetIds.add(usetId);
			}
			if (Checker.beNotEmpty(usetIds)) {
				List<UsetDataPermDto> usetDataPerms = usetDataPermService.listUsetDataPerm(usetIds, permId);
				for (UsetDataPermDto perm : usetDataPerms) {
					perm.setSearchExpresses(JSONArray.parseArray(perm.getPermExpress(), AdvanceSearchVo.class));
					DataPermBusinessVo db = new DataPermBusinessVo();
					db.setPermExpress(perm.getPermExpress());
					db.setPermEndTime(perm.getPermEndTime());
					db.setPermStartTime(perm.getPermStartTime());
					db.setBeOrCondition(perm.getBeOrCondition());
					db.setSearchExpresses(perm.getSearchExpresses());
					list.add(db);
				}
			}

		} else if (dpv == DataPermViewEnum.ALL) {
			UserDataPermDto perm = userDataPermService.getUserDataPerm(currentUserId(), orgId, permId);
			if (Checker.beNotNull(perm)) {
				perm.setSearchExpresses(JSONArray.parseArray(perm.getPermExpress(), AdvanceSearchVo.class));
				DataPermBusinessVo db = new DataPermBusinessVo();
				db.setPermExpress(perm.getPermExpress());
				db.setPermEndTime(perm.getPermEndTime());
				db.setPermStartTime(perm.getPermStartTime());
				db.setBeOrCondition(perm.getBeOrCondition());
				db.setSearchExpresses(perm.getSearchExpresses());
				list.add(db);
			}

			Set<String> usetIds = Sets.newHashSet();
			List<UsetDto> usets = userUsetService.listUsetsByGroupAndUser(orgId, currentUserId());
			if (Checker.beNotEmpty(usets)) {
				usetIds.addAll(usets.stream().map(UsetDto::getId).collect(Collectors.toSet()));
			}

			if (Checker.beNotEmpty(usetIds)) {
				List<UsetDataPermDto> usetDataPerms = usetDataPermService.listUsetDataPerm(usetIds, permId);
				for (UsetDataPermDto p : usetDataPerms) {
					p.setSearchExpresses(JSONArray.parseArray(p.getPermExpress(), AdvanceSearchVo.class));
					DataPermBusinessVo db = new DataPermBusinessVo();
					db.setPermExpress(p.getPermExpress());
					db.setPermEndTime(p.getPermEndTime());
					db.setPermStartTime(p.getPermStartTime());
					db.setBeOrCondition(p.getBeOrCondition());
					db.setSearchExpresses(p.getSearchExpresses());
					list.add(db);
				}
			}

		}
		return mergeBusinessPerm(list);
	}

	private List<DataPermColumnVo> mergeColumnPerm(List<DataPermColumnVo> perms) {
		if (Checker.beEmpty(perms)) {
			return perms;
		}
		Map<String, List<DataPermColumnVo>> permMap = perms.stream().collect(Collectors.groupingBy(DataPermColumnVo::getTable));
		List<DataPermColumnVo> list = Lists.newArrayList();
		//TODO 分类合并
		permMap.forEach((k, v) -> {
			DataPermColumnVo dbv = new DataPermColumnVo();
			dbv.setTable(v.get(0).getTable());
			dbv.setTableComment(v.get(0).getTableComment());
			dbv.setAlias(v.get(0).getAlias());
			if (Checker.beEmpty(dbv.getColumns())) {
				dbv.setColumns(Lists.newArrayList());
			}
			for (DataPermColumnVo db : v) {
				dbv.getColumns().addAll(db.getColumns());
			}
			list.add(dbv);
		});
		return list;
	}


	@Override
	public List<DataPermColumnVo> getColumnDataPerm(String viewType, String orgId, String permId, String usetId) {
		if (Checker.beEmpty(viewType) || Checker.beEmpty(orgId) || Checker.beEmpty(permId)) {
			return null;
		}

		DataPermViewEnum dpv = EnumUtils.getEnum(DataPermViewEnum.class, viewType);
		if (Checker.beNull(dpv)) {
			return null;
		}
		List<DataPermColumnVo> list = Lists.newArrayList();
		if (dpv == DataPermViewEnum.USER) {
			UserColumnPermDto perm = userColumnPermService.getUserColumnPerm(currentUserId(), orgId, permId);
			if (Checker.beNull(perm)) {
				return  null;
			}
			List<ActionSelectTable>  tables = JSONArray.parseArray(perm.getColumnExpress(), ActionSelectTable.class);
			for (ActionSelectTable table : tables) {
				list.add(new DataPermColumnVo()
						.setTable(table.getTable())
						.setTableComment(table.getTableComment())
						.setAlias(table.getAlias())
						.setColumns(table.getColumns()));
			}
		} else if (dpv == DataPermViewEnum.USET) {
			Set<String> usetIds = Sets.newHashSet();
			if (DataPermViewEnum.ALL.name().equals(usetId)) {
				List<UsetDto> usets = userUsetService.listUsetsByGroupAndUser(orgId, currentUserId());
				if (Checker.beNotEmpty(usets)) {
					usetIds.addAll(usets.stream().map(UsetDto::getId).collect(Collectors.toSet()));
				}
			} else {
				usetIds.add(usetId);
			}
			List<UsetColumnPermDto> perms = usetColumnPermService.listUsetColumnPerm(usetIds, permId);
			for (UsetColumnPermDto perm : perms) {
				if (Checker.beNotEmpty(perm.getColumnExpress())) {
					List<ActionSelectTable> tables = JSONArray.parseArray(perm.getColumnExpress(), ActionSelectTable.class);
					for (ActionSelectTable table : tables) {
						list.add(new DataPermColumnVo()
								.setTable(table.getTable())
								.setTableComment(table.getTableComment())
								.setAlias(table.getAlias())
								.setColumns(table.getColumns()));
					}
				}
			}
		} else if (dpv == DataPermViewEnum.ALL) {
			UserColumnPermDto perm = userColumnPermService.getUserColumnPerm(currentUserId(), orgId, permId);
			if (Checker.beNull(perm)) {
				return  null;
			}
			List<ActionSelectTable>  tables = JSONArray.parseArray(perm.getColumnExpress(), ActionSelectTable.class);
			for (ActionSelectTable table : tables) {
				list.add(new DataPermColumnVo()
						.setTable(table.getTable())
						.setTableComment(table.getTableComment())
						.setAlias(table.getAlias())
						.setColumns(table.getColumns()));
			}

			Set<String> usetIds = Sets.newHashSet();
			List<UsetDto> usets = userUsetService.listUsetsByGroupAndUser(orgId, currentUserId());
			if (Checker.beNotEmpty(usets)) {
				usetIds.addAll(usets.stream().map(UsetDto::getId).collect(Collectors.toSet()));
			}
			List<UsetColumnPermDto> perms = usetColumnPermService.listUsetColumnPerm(usetIds, permId);
			for (UsetColumnPermDto uperm : perms) {
				if (Checker.beNotEmpty(uperm.getColumnExpress())) {
					List<ActionSelectTable> utables = JSONArray.parseArray(uperm.getColumnExpress(), ActionSelectTable.class);
					for (ActionSelectTable table : utables) {
						list.add(new DataPermColumnVo()
								.setTable(table.getTable())
								.setTableComment(table.getTableComment())
								.setAlias(table.getAlias())
								.setColumns(table.getColumns()));
					}
				}
			}

		}

		return mergeColumnPerm(list);
	}
}
