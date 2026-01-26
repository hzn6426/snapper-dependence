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

package com.baomibing.authority.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomibing.authority.constant.enums.ResourceTypeEnum;
import com.baomibing.authority.dto.*;
import com.baomibing.authority.service.*;
import com.baomibing.authority.vo.AdvanceSearchVo;
import com.baomibing.cache.CacheService;
import com.baomibing.core.common.Assert;
import com.baomibing.core.wrap.CommonTreeWrap;
import com.baomibing.orm.perm.*;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.constant.TenantRedisKeyConstant;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ObjectUtil;
import com.baomibing.web.base.MBaseController;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.baomibing.tool.user.UserContext.currentTenantId;

@RestController
@RequestMapping(path = {"/eapi/resource",}, consumes = {"application/json","application/x-www-form-urlencoded"},
		produces = MediaType.APPLICATION_JSON_VALUE)
public class TenantResourceController extends MBaseController<SysTenantRoleResourceDto> {

	@Autowired private SysTenantRoleResourceService roleResourceService;
	@Autowired private SysTenantUserBusinessPermService userBusinessPermService;
	@Autowired private SysTenantUsetBusinessPermService usetBusinessPermService;
	@Autowired private SchemaService schemaService;
	@Autowired private SysBusinessPermTenantService businessPermService;
	@Autowired private CacheService cacheService;
	@Autowired private SysTenantUserDataPermService userDataPermService;
	@Autowired private SysTenantUsetDataPermService usetDataPermService;
	@Autowired private SysTenantUserColumnPermService userColumnPermService;
	@Autowired private SysTenantUsetColumnPermService usetColumnPermService;
	@Autowired private SysTenantMenuService tenantMenuService;
	@Autowired private SysTenantButtonService tenantButtonService;

	/**
	 * 保存菜单资源
	 *
	 * @param resourcePerm 菜单
	 */
	@PostMapping("/saveMenuPerm")
	public void saveMenuPerm(@RequestBody TenantResourcePermDto resourcePerm) {
		Assert.CheckArgument(resourcePerm);
		Assert.CheckArgument(resourcePerm.getRoleId());
		String tenantId = ObjectUtil.defaultIfNull(currentTenantId(), resourcePerm.getTenantId());
		this.roleResourceService.saveMenusPermsByRole(tenantId, resourcePerm.getRoleId(), Sets.newHashSet(resourcePerm.getMenus()));

	}

	/**
	 * 保存按钮资源
	 *
	 * @param resourcePerm 菜单
	 */
	@PostMapping("/saveButtonPerm")
	public void saveButtonPerm(@RequestBody TenantResourcePermDto resourcePerm) {
		Assert.CheckArgument(resourcePerm);
		Assert.CheckArgument(resourcePerm.getRoleId());
		this.roleResourceService.saveButtonPermsByMenuAndRole(resourcePerm.getTenantId(), resourcePerm.getRoleId(), resourcePerm.getMenuId(),
				Sets.newHashSet(resourcePerm.getButtons()));
	}

	/**
	 * 获取用户对应的资源(菜单)ID列表
	 *
	 * @return
	 */
	@GetMapping("listPermMenus")
	public List<String> listPermMenus(@RequestParam String rid, @RequestParam String tid) {
//		return roleResourceService.listPermResourceIdsByRoles(Sets.newHashSet(rid));
		return roleResourceService.listPermResourceIdsByRoles(Sets.newHashSet(rid), ResourceTypeEnum.MENU, tid);
	}

	/**
	 * 获取用户对应的资源(按钮)ID列表
	 *
	 * @return
	 */
	@GetMapping("listPermButtons")
	public List<String> listPermButtons(@RequestParam String rid, @RequestParam String menuId, @RequestParam String tid) {
//		return roleResourceService.listPermResourceIdsByRoles(Sets.newHashSet(rid));
		List<ButtonTenantDto> buttons = roleResourceService.listPermButtonsByRolesAndMenu(Sets.newHashSet(rid), menuId, tid);
		return buttons.stream().map(ButtonTenantDto::getId).distinct().collect(Collectors.toList());
	}

	/**
	 * 以树的方式封装系统所有的菜单和按钮
	 *
	 * @return
	 */
//	@GetMapping("treeAllMenusAndButtons")
//	public List<CommonTreeWrap> treeAllMenusAndButtons() {
//		List<MenuDto> menus = roleResourceService.listAllMenusAndButtonsForGrant();
//		return loopMenus(menus);
//	}

	/**
	 * 以树的方式封装系统所有的菜单
	 *
	 * @return
	 */
	@GetMapping("treeAllMenus")
	public List<CommonTreeWrap> treeAllMenus(@RequestParam String tid) {
		Set<String> roleIds = currentUserRoles();
		List<MenuTenantDto> menus = roleResourceService.listAllMenusForGrant(roleIds, tid);
		return loopMenus(menus);
	}

	/**
	 * 根据菜单ID获取所有按钮列表
	 *
	 * @param menuId 菜单ID
	 * @return
	 */
	@GetMapping("listAllButtonsByMenu")
	public List<ButtonTenantDto> listAllButtonsByMenuForGrant(@RequestParam String tid, @RequestParam String menuId) {
		Set<String> roleIds = currentUserRoles();
		List<ButtonTenantDto> buttons =  roleResourceService.listPermButtonsByRolesAndMenu(roleIds, menuId, tid);
		return buttons;
	}

	/**
	 * 递归获取子菜单和按钮
	 *
	 * @param menus
	 * @return
	 */
	private List<CommonTreeWrap> loopMenus(List<MenuTenantDto> menus) {
		List<CommonTreeWrap> list = Lists.newArrayList();
		if (Checker.beEmpty(menus)) {
			return list;
		}
		menus.forEach(m -> {
			CommonTreeWrap wrap = new CommonTreeWrap().setTitle(m.getName()).setKey(m.getId())
					.setTag(ResourceTypeEnum.MENU.name()).setIsLeaf(Checker.beEmpty(m.getChildren()));
			List<CommonTreeWrap> cmenus = loopMenus(m.getChildren());
			wrap.setChildren(cmenus);
			list.add(wrap);
		});
		return list;
	}


	/**
	 * 根据用户获取角色权限对应的权限资源列表及其对应的数据权限范围
	 *
	 * @param uid 用户ID
	 * @return
	 */
	@GetMapping("/listResourcesForBPermByUser")
	public List<MenuTenantDto> listPermMenusAndButtonsForBusinessPermByUser(@RequestParam String tid, @RequestParam String orgId, @RequestParam String uid) {
		return roleResourceService.listPermMenusAndButtonsForBusinessPermByUser(tid, orgId, uid);
	}

	/**
	 * 根据用户组获取权限对应的权限资源列表及其对应的数据权限范围
	 * @param usetId 用户组ID
	 * @return
	 */
	@GetMapping("/listResourcesForBPermByUset")
	public List<MenuTenantDto> listPermMenusAndButtonsForBusinessPermByUset(@RequestParam String tid, @RequestParam String usetId) {
		return roleResourceService.listPermMenusAndButtonsForBusinessPermByUset(tid, usetId);
	}

	/**
	 * 根据权限ID及用户ID获取用户对应权限的委托列表
	 *
	 * @param permId 权限ID
	 * @param uid    用户ID
	 * @return
	 */
	@GetMapping("/listPermEntrustsByUser")
	public List<String> listPermEntrustsByUser(@RequestParam String tid, @RequestParam String permId, @RequestParam String uid, @RequestParam String orgId) {
		if (Checker.beEmpty(permId) || Checker.beEmpty(uid) || Checker.beEmpty(orgId) || Checker.beEmpty(tid)) {
			return Lists.newArrayList();
		}
		List<String> userIdEntrusts = userBusinessPermService.listUserEntrustIdsByPerm(tid, orgId, uid, permId);
		List<String> groupIdEntrusts = userBusinessPermService.listGroupEntrustIdsByPerm(tid, orgId, uid, permId);
		List<String> targets = Lists.newArrayList(userIdEntrusts);
		targets.addAll(groupIdEntrusts);
		return targets;
	}

	/**
	 * 根据权限ID及用户ID获取用户对应权限的排除的排除的委托列表
	 *
	 * @param permId 权限ID
	 * @param uid    用户ID
	 * @return
	 */
	@GetMapping("/listPermExceptEntrustsByUser")
	public List<String> listPermExceptEntrustsByUser(@RequestParam String tid, @RequestParam String permId, @RequestParam String uid, @RequestParam String orgId) {
		if (Checker.beEmpty(permId) || Checker.beEmpty(uid) || Checker.beEmpty(orgId) || Checker.beEmpty(tid)) {
			return Lists.newArrayList();
		}
		List<String> userIdEntrusts = userBusinessPermService.listUserExceptEntrustIdsByPerm(tid, orgId, uid, permId);
		List<String> groupIdEntrusts = userBusinessPermService.listGroupExceptEntrustIdsByPerm(tid, orgId, uid, permId);
		List<String> targets = Lists.newArrayList(userIdEntrusts);
		targets.addAll(groupIdEntrusts);
		return targets;
	}

	/**
	 * 根据ID及用户组ID获取对应权限的委托列表
	 * @param permId 权限ID
	 * @param usetId 用户组ID
	 * @return
	 */
	@GetMapping("/listPermEntrustsByUset")
	public List<String> listPermEntrustByUset(@RequestParam String tid, @RequestParam String permId, @RequestParam String usetId) {
		if (Checker.beEmpty(permId) || Checker.beEmpty(usetId) || Checker.beEmpty(tid)) {
			return Lists.newArrayList();
		}
		List<String> userIdEntrusts = usetBusinessPermService.listUserEntrustIdsByPerm(tid, usetId, permId);
		List<String> groupIdEntrusts = usetBusinessPermService.listGroupEntrustIdsByPerm(tid, usetId, permId);
		List<String> targets = Lists.newArrayList(userIdEntrusts);
		targets.addAll(groupIdEntrusts);
		return targets;
	}

	/**
	 * 根据ID及用户组ID获取对应权限的排除的委托列表
	 * @param permId 权限ID
	 * @param usetId 用户组ID
	 * @return
	 */
	@GetMapping("/listPermExceptEntrustsByUset")
	public List<String> listPermExceptEntrustByUset(@RequestParam String tid, @RequestParam String permId, @RequestParam String usetId) {
		if (Checker.beEmpty(permId) || Checker.beEmpty(usetId)) {
			return Lists.newArrayList();
		}
		List<String> userIdEntrusts = usetBusinessPermService.listUserExceptEntrustIdsByPerm(tid, usetId, permId);
		List<String> groupIdEntrusts = usetBusinessPermService.listGroupExceptEntrustIdsByPerm(tid, usetId, permId);
		List<String> targets = Lists.newArrayList(userIdEntrusts);
		targets.addAll(groupIdEntrusts);
		return targets;
	}

	/**
	 * 保存用户业务权限
	 *
	 * @param perm 业务权限
	 */
	@PostMapping("/saveBusinessPerm")
	public void saveBusinessPerm(@RequestBody SysTenantUserBusinessPermDto perm) {
		userBusinessPermService.saveUserBusinessPerm(perm);
	}

	/**
	 * 存储用户数据权限
	 * @param perm 数据权限
	 */
	@PostMapping("/saveDataPerm")
	public void saveDataPerm(@RequestBody SysTenantUserDataPermDto perm) {
		userDataPermService.saveUserDataPerm(perm);
	}

	@PostMapping("/saveUsetDataPerm")
	public void saveUsetDataPerm(@RequestBody SysTenantUsetDataPermDto perm) {
		usetDataPermService.saveUserDataPerm(perm);
	}

	@GetMapping("/getUserDataPerm")
	public SysTenantUserDataPermDto getUserDataPerm(@RequestParam String tid, @RequestParam String uid, @RequestParam String orgId, @RequestParam String permId) {
		SysTenantUserDataPermDto perm =  userDataPermService.getUserDataPerm(tid, uid, orgId, permId);
		if (Checker.beNotNull(perm)) {
			perm.setSearchExpresses(JSONArray.parseArray(perm.getPermExpress(), AdvanceSearchVo.class));
		}
		return perm;
	}

	@GetMapping("/getUsetDataPerm")
	public SysTenantUsetDataPermDto listUsetDataPerm(@RequestParam String tid, @RequestParam String usetId, @RequestParam String permId) {
		SysTenantUsetDataPermDto perm =  usetDataPermService.getUsetDataPerm(tid, usetId, permId);
		if (Checker.beNotNull(perm)) {
			perm.setSearchExpresses(JSONArray.parseArray(perm.getPermExpress(), AdvanceSearchVo.class));
		}
		return perm;
	}

	@PostMapping("saveUserColumnPerm")
	public void saveUserColumnPerm(@RequestBody SysTenantUserColumnPermDto perm) {
		userColumnPermService.saveColumnPerm(perm);
	}

	@PostMapping("saveUsetColumnPerm")
	public void saveUsetColumnPerm(@RequestBody SysTenantUsetColumnPermDto perm) {
		usetColumnPermService.saveColumnPerm(perm);
	}

	@GetMapping("listUserColumnPerm")
	public List<ActionSelectTable> listUserColumnPerm(@RequestParam String tid, @RequestParam String uid, @RequestParam String orgId, @RequestParam String permId) {
		SysTenantUserColumnPermDto perm = userColumnPermService.getUserColumnPerm(tid, uid, orgId, permId);
		if (Checker.beNull(perm)) {
			return  null;
		}
		return JSONArray.parseArray(perm.getColumnExpress(), ActionSelectTable.class);
	}

	@GetMapping("listUsetColumnPerm")
	public List<ActionSelectTable> listUsetColumnPerm(@RequestParam String tid, @RequestParam String usetId, @RequestParam String permId) {
		SysTenantUsetColumnPermDto perm = usetColumnPermService.getUsetColumnPerm(tid, usetId, permId);
		if (Checker.beNull(perm)) {
			return null;
		}
		return JSONArray.parseArray(perm.getColumnExpress(), ActionSelectTable.class);
	}

	/**
	 * 保存用户组业务权限
	 * @param perm
	 */
	@PostMapping("/saveUsetBusinessPerm")
	public void saveUsetBusinessPerm(@RequestBody SysTenantUsetBusinessPermDto perm) {
		usetBusinessPermService.saveUserBusinessPerm(perm);
	}

	@GetMapping("fetchActionMapperByPerm")
	public ActionMapper fetchByAction(@RequestParam String permId) {
		if (Checker.beEmpty(permId)) {
			return null;
		}
		BusinessPermTenantDto perm =  businessPermService.getIt(permId);
		if (Checker.beNull(perm)) {
			return null;
		}
		String action = perm.getPermAction();
		String cacheKey = TenantRedisKeyConstant.CACHE_ACTION_CONNECT_PREFIX + action;
		if (cacheService.hasKey(cacheKey)) {
			String value = cacheService.get(cacheKey);
			if (Checker.beNotEmpty(value)) {
				ActionMapper mapper =  JSONObject.parseObject(value, ActionMapper.class);
				//获取表格真实表名
				if (Checker.beNotNull(mapper) && Checker.beNotEmpty(mapper.getWhereTables())) {
					mapper.setWhereTables(mapper.getWhereTables());
					List<ActionWhereTable> actionTables = mapper.getWhereTables();
					Set<String> tableNames = actionTables.stream().map(ActionWhereTable::getTable).collect(Collectors.toSet());
					List<Table> tables = schemaService.fetTableByNames(tableNames);
					Map<String, String> tmap = tables.stream().filter(t -> Checker.beNotEmpty(t.getTableComment())).collect(Collectors.toMap(Table::getTableName, Table::getTableComment));
					actionTables.forEach(t -> t.setTableComment(ObjectUtil.defaultIfNull(tmap.get(t.getTable()), t.getTable())));
				}
				return mapper;
			}
		}
		return null;
	}

	@GetMapping("fetchColumnActionMapperByPerm")
	public ActionMapper fetchColumnActionMapperByPerm(@RequestParam String permId) {
		if (Checker.beEmpty(permId)) {
			return null;
		}
		BusinessPermTenantDto perm =  businessPermService.getIt(permId);
		if (Checker.beNull(perm)) {
			return null;
		}
		String action = perm.getPermAction();
		String cacheKey = TenantRedisKeyConstant.CACHE_ACTION_CONNECT_PREFIX + action;
		if (cacheService.hasKey(cacheKey)) {
			String value = cacheService.get(cacheKey);
			if (Checker.beNotEmpty(value)) {
				ActionMapper mapper =  JSONObject.parseObject(value, ActionMapper.class);
				//获取表格真实表名
				if (Checker.beNotNull(mapper) && Checker.beNotEmpty(mapper.getSelectTables())) {
					List<ActionSelectTable> actionTables = mapper.getSelectTables();
					actionTables = actionTables.stream().filter(at -> Checker.beNotEmpty(at.getColumns())).collect(Collectors.toList());
					Set<String> tableNames = actionTables.stream().map(ActionSelectTable::getTable).collect(Collectors.toSet());
					List<Table> tables = schemaService.fetTableByNames(tableNames);
					Map<String, String> tmap = tables.stream().filter(t -> Checker.beNotEmpty(t.getTableComment())).collect(Collectors.toMap(Table::getTableName, Table::getTableComment));
					actionTables.forEach(t -> t.setTableComment(ObjectUtil.defaultIfNull(tmap.get(t.getTable()), t.getTable())));
					//查找列注解
					List<ActionTableColumn> columns = schemaService.fetchColumnByTable(tableNames, false);
					Map<String, ActionTableColumn> columnMap = columns.stream().collect(Collectors.toMap(t->t.getTableName() + Strings.DOT + t.getColumnName(), Function.identity()));
					for (ActionSelectTable at : actionTables) {
						List<ActionTableColumn> tableColumns = at.getColumns();
						if (Checker.beEmpty(tableColumns)) {
							continue;
						}
						List<ActionTableColumn> targetColumns = Lists.newArrayList();
						for (ActionTableColumn tc : tableColumns) {
							if (Checker.beNotNull(columnMap.get(at.getTable() + Strings.DOT + tc.getColumnName()))) {
								targetColumns.add(columnMap.get(at.getTable() + Strings.DOT + tc.getColumnName()));
							} else {
								targetColumns.add(tc);
							}
							if (Checker.beEmpty(tc.getColumnComment())) {
								tc.setColumnComment(tc.getColumnName());
							}
						}
						at.setColumns(targetColumns);
					}
					mapper.setSelectTables(actionTables);
				}
				return mapper;
			}
		}
		return null;
	}


	@GetMapping("fetchColumnByTable")
	public List<ActionTableColumn> fetchColumnByTable(@RequestParam String tables, @RequestParam Boolean refresh)  {
		if (Checker.beEmpty(tables)) {
			return Lists.newArrayList();
		}
		boolean beFresh = !Checker.beNull(refresh) && refresh;
		return schemaService.fetchColumnByTable(Sets.newHashSet(Splitter.on(Strings.COMMA).splitToList(tables)), beFresh);
	}
}
