/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomibing.authority.constant.enums.ResourceTypeEnum;
import com.baomibing.authority.dto.*;
import com.baomibing.authority.service.*;
import com.baomibing.authority.vo.AdvanceSearchVo;
import com.baomibing.authority.vo.DataPermBusinessVo;
import com.baomibing.authority.vo.DataPermColumnVo;
import com.baomibing.authority.vo.DataPermFunctionVo;
import com.baomibing.cache.CacheService;
import com.baomibing.core.common.Assert;
import com.baomibing.core.wrap.CommonTreeWrap;
import com.baomibing.orm.perm.*;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ObjectUtil;
import com.baomibing.web.annotation.ULog;
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

import static com.baomibing.tool.constant.RedisKeyConstant.CACHE_ACTION_CONNECT_PREFIX;

@RestController
@RequestMapping(path = "/api/resource", consumes = {"application/json","application/x-www-form-urlencoded"}, 
produces = MediaType.APPLICATION_JSON_VALUE)
public class ResourceController extends MBaseController<RoleResourceDto> {

	@Autowired private SysRoleResourceService roleResourceService;
	@Autowired private SysUserBusinessPermService userBusinessPermService;
	@Autowired private SysUsetBusinessPermService usetBusinessPermService;
	@Autowired private SchemaService schemaService;
	@Autowired private SysBusinessPermService businessPermService;
	@Autowired private CacheService cacheService;
	@Autowired private SysUserDataPermService userDataPermService;
	@Autowired private SysUsetDataPermService usetDataPermService;
	@Autowired private SysUserColumnPermService userColumnPermService;
	@Autowired private SysUsetColumnPermService usetColumnPermService;
	

	/**
	 * 保存菜单资源
	 * 
	 * @param resourcePerm 菜单
	 */
	@ULog("资源-保存菜单权限")
	@PostMapping("/saveMenuPerm")
	public void saveMenuPerm(@RequestBody ResourcePermDto resourcePerm) {
		Assert.CheckArgument(resourcePerm);
		Assert.CheckArgument(resourcePerm.getRoleId());
		this.roleResourceService.saveMenusPermsByRole(resourcePerm.getRoleId(), Sets.newHashSet(resourcePerm.getMenus()));

	}

	/**
	 * 保存按钮资源
	 * 
	 * @param resourcePerm 菜单
	 */
	@ULog("资源-保存按钮权限")
	@PostMapping("/saveButtonPerm")
	public void saveButtonPerm(@RequestBody ResourcePermDto resourcePerm) {
		Assert.CheckArgument(resourcePerm);
		Assert.CheckArgument(resourcePerm.getRoleId());
		this.roleResourceService.saveButtonPermsByMenuAndRole(resourcePerm.getRoleId(), resourcePerm.getMenuId(),
				Sets.newHashSet(resourcePerm.getButtons()));
	}

	/**
	 * 获取用户对应的资源(菜单)ID列表
	 * 
	 * @return
	 */
	@GetMapping("listPermMenus")
	public List<String> listPermMenus(@RequestParam String rid) {
//		return roleResourceService.listPermResourceIdsByRoles(Sets.newHashSet(rid));
		return roleResourceService.listPermResourceIdsByRoles(Sets.newHashSet(rid), ResourceTypeEnum.MENU);
	}
    
	/**
	 * 获取用户对应的资源(按钮)ID列表
	 * 
	 * @return
	 */
	@GetMapping("listPermButtons")
	public List<String> listPermButtons(@RequestParam String rid, @RequestParam String menuId) {
//		return roleResourceService.listPermResourceIdsByRoles(Sets.newHashSet(rid));
		List<ButtonDto> buttons = roleResourceService.listPermButtonsByRolesAndMenu(Sets.newHashSet(rid), menuId);
		return buttons.stream().map(ButtonDto::getId).distinct().collect(Collectors.toList());
	}

	/**
	 * 以树的方式封装系统所有的菜单
	 * 
	 * @return
	 */
	@GetMapping("treeAllMenus")
	public List<CommonTreeWrap> treeAllMenus() {
		List<MenuDto> menus = roleResourceService.listAllMenusForGrant();
		return loopMenus(menus);
	}
	
	/**
	 * 根据菜单ID获取所有按钮列表
	 * 
	 * @param menuId 菜单ID
	 * @return
	 */
	@GetMapping("listAllButtonsByMenu")
	public List<ButtonDto> listAllButtonsByMenuForGrant(@RequestParam String menuId) {
		return roleResourceService.listAllButtonsByMenuForGrant(menuId);
	}

	/**
	 * 递归获取子菜单和按钮
	 * 
	 * @param menus
	 * @return
	 */
	private static List<CommonTreeWrap> loopMenus(List<MenuDto> menus) {
		List<CommonTreeWrap> list = Lists.newArrayList();
		if (Checker.beEmpty(menus)) {
			return list;
		}
		menus.forEach(m -> {
			CommonTreeWrap wrap = new CommonTreeWrap().setTitle(m.getName()).setKey(m.getId()).setMenuType(m.getMenuType())
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
	public List<MenuDto> listPermMenusAndButtonsForBusinessPermByUser(@RequestParam String orgId, @RequestParam String uid) {
		return roleResourceService.listPermMenusAndButtonsForBusinessPermByUser(orgId, uid);
	}

	/**
	 * 根据用户组获取权限对应的权限资源列表及其对应的数据权限范围
	 * @param usetId 用户组ID
	 * @return
	 */
	@GetMapping("/listResourcesForBPermByUset")
	public List<MenuDto> listPermMenusAndButtonsForBusinessPermByUset(@RequestParam String usetId) {
		return roleResourceService.listPermMenusAndButtonsForBusinessPermByUset(usetId);
	}

	/**
	 * 根据权限ID及用户ID获取用户对应权限的委托列表
	 * 
	 * @param permId 权限ID
	 * @param uid    用户ID
	 * @return
	 */
	@GetMapping("/listPermEntrustsByUser")
	public List<String> listPermEntrustsByUser(@RequestParam String permId, @RequestParam String uid, @RequestParam String orgId) {
		if (Checker.beEmpty(permId) || Checker.beEmpty(uid) || Checker.beEmpty(orgId)) {
			return Lists.newArrayList();
		}
		List<String> userIdEntrusts = userBusinessPermService.listUserEntrustIdsByPerm(orgId, uid, permId);
		List<String> groupIdEntrusts = userBusinessPermService.listGroupEntrustIdsByPerm(orgId, uid, permId);
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
	public List<String> listPermExceptEntrustsByUser(@RequestParam String permId, @RequestParam String uid, @RequestParam String orgId) {
		if (Checker.beEmpty(permId) || Checker.beEmpty(uid) || Checker.beEmpty(orgId)) {
			return Lists.newArrayList();
		}
		List<String> userIdEntrusts = userBusinessPermService.listUserExceptEntrustIdsByPerm(orgId, uid, permId);
		List<String> groupIdEntrusts = userBusinessPermService.listGroupExceptEntrustIdsByPerm(orgId, uid, permId);
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
	public List<String> listPermEntrustByUset(@RequestParam String permId, @RequestParam String usetId) {
		if (Checker.beEmpty(permId) || Checker.beEmpty(usetId)) {
			return Lists.newArrayList();
		}
		List<String> userIdEntrusts = usetBusinessPermService.listUserEntrustIdsByPerm(usetId, permId);
		List<String> groupIdEntrusts = usetBusinessPermService.listGroupEntrustIdsByPerm(usetId, permId);
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
	public List<String> listPermExceptEntrustByUset(@RequestParam String permId, @RequestParam String usetId) {
		if (Checker.beEmpty(permId) || Checker.beEmpty(usetId)) {
			return Lists.newArrayList();
		}
		List<String> userIdEntrusts = usetBusinessPermService.listUserExceptEntrustIdsByPerm(usetId, permId);
		List<String> groupIdEntrusts = usetBusinessPermService.listGroupExceptEntrustIdsByPerm(usetId, permId);
		List<String> targets = Lists.newArrayList(userIdEntrusts);
		targets.addAll(groupIdEntrusts);
		return targets;
	}

	/**
	 * 存储用户数据权限
	 * @param perm 数据权限
	 */
	@ULog("资源-保存用户数据权限")
	@PostMapping("/saveDataPerm")
	public void saveDataPerm(@RequestBody UserDataPermDto perm) {
		userDataPermService.saveUserDataPerm(perm);
	}

	@ULog("资源-保存用户组数据权限")
	@PostMapping("/saveUsetDataPerm")
	public void saveUsetDataPerm(@RequestBody UsetDataPermDto perm) {
		usetDataPermService.saveUserDataPerm(perm);
	}

	@GetMapping("/getUserDataPerm")
	public UserDataPermDto listUserDataPerm(@RequestParam String uid, @RequestParam String orgId, @RequestParam String permId) {
		UserDataPermDto perm =  userDataPermService.getUserDataPerm(uid, orgId, permId);
		if (Checker.beNotNull(perm)) {
			perm.setSearchExpresses(JSONArray.parseArray(perm.getPermExpress(), AdvanceSearchVo.class));
		}
		return perm;
	}

	@GetMapping("/getUsetDataPerm")
	public UsetDataPermDto listUsetDataPerm(@RequestParam String usetId, @RequestParam String permId) {
		UsetDataPermDto perm =  usetDataPermService.getUsetDataPerm(usetId, permId);
		if (Checker.beNotNull(perm)) {
			perm.setSearchExpresses(JSONArray.parseArray(perm.getPermExpress(), AdvanceSearchVo.class));
		}
		return perm;
	}

	/**
	 * 保存用户业务权限
	 * 
	 * @param perm 业务权限
	 */
	@ULog("资源-保存用户业务权限")
	@PostMapping("/saveBusinessPerm")
	public void saveBusinessPerm(@RequestBody UserBusinessPermDto perm) {
		userBusinessPermService.saveUserBusinessPerm(perm);
	}


	/**
	 * 保存用户组业务权限
	 * @param perm
	 */
	@ULog("资源-保存用户组业务权限")
	@PostMapping("/saveUsetBusinessPerm")
	public void saveUsetBusinessPerm(@RequestBody UsetBusinessPermDto perm) {
		usetBusinessPermService.saveUserBusinessPerm(perm);
	}

	@GetMapping("fetchActionMapperByPerm")
	public ActionMapper fetchByAction(@RequestParam String permId) {
		if (Checker.beEmpty(permId)) {
			return null;
		}
		BusinessPermDto perm =  businessPermService.getIt(permId);
		if (Checker.beNull(perm)) {
			return null;
		}
        String action = perm.getPermAction();
		String cacheKey = CACHE_ACTION_CONNECT_PREFIX + action;
		if (cacheService.hasKey(cacheKey)) {
			String value = cacheService.get(cacheKey);
			if (Checker.beNotEmpty(value)) {
				ActionMapper mapper =  JSONObject.parseObject(value, ActionMapper.class);
				//获取表格真实表名
				if (Checker.beNotNull(mapper) && Checker.beNotEmpty(mapper.getWhereTables())) {
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


	@GetMapping("fetchColumnByTable")
	public List<ActionTableColumn> fetchColumnByTable(@RequestParam String tables, @RequestParam Boolean refresh)  {
		if (Checker.beEmpty(tables)) {
			return Lists.newArrayList();
		}
		boolean beFresh = !Checker.beNull(refresh) && refresh;
		return schemaService.fetchColumnByTable(Sets.newHashSet(Splitter.on(Strings.COMMA).splitToList(tables)), beFresh);
	}

	@ULog("资源-保存用户列权限")
	@PostMapping("saveUserColumnPerm")
	public void saveUserColumnPerm(@RequestBody UserColumnPermDto perm) {
		userColumnPermService.saveColumnPerm(perm);
	}

	@ULog("资源-保存用户组列权限")
	@PostMapping("saveUsetColumnPerm")
	public void saveUsetColumnPerm(@RequestBody UsetColumnPermDto perm) {
		usetColumnPermService.saveColumnPerm(perm);
	}

	@GetMapping("listUserColumnPerm")
	public List<ActionSelectTable> listUserColumnPerm(@RequestParam String uid, @RequestParam String orgId, @RequestParam String permId) {
		UserColumnPermDto perm = userColumnPermService.getUserColumnPerm(uid, orgId, permId);
		if (Checker.beNull(perm)) {
			return  null;
		}
		return JSONArray.parseArray(perm.getColumnExpress(), ActionSelectTable.class);
	}

	@GetMapping("listUsetColumnPerm")
	public List<ActionSelectTable> listUsetColumnPerm(@RequestParam String usetId, @RequestParam String permId) {
		UsetColumnPermDto perm = usetColumnPermService.getUsetColumnPerm(usetId, permId);
		if (Checker.beNull(perm)) {
			return null;
		}
		return JSONArray.parseArray(perm.getColumnExpress(), ActionSelectTable.class);
	}

	@GetMapping("fetchColumnActionMapperByPerm")
	public ActionMapper fetchColumnActionMapperByPerm(@RequestParam String permId) {
		if (Checker.beEmpty(permId)) {
			return null;
		}
		BusinessPermDto perm =  businessPermService.getIt(permId);
		if (Checker.beNull(perm)) {
			return null;
		}
		String action = perm.getPermAction();
        String cacheKey = CACHE_ACTION_CONNECT_PREFIX + action;
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
							if (Checker.beEmpty(tc.getColumnComment())) {
								tc.setColumnComment(tc.getColumnName());
							}
							if (Checker.beNotNull(columnMap.get(at.getTable() + Strings.DOT + tc.getColumnName()))) {
								targetColumns.add(columnMap.get(at.getTable() + Strings.DOT + tc.getColumnName()));
							} else {
								targetColumns.add(tc);
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

	@GetMapping("getUserFunctionDataPerm")
	public DataPermFunctionVo getUserFunctionDataPerm(@RequestParam String viewType, @RequestParam String orgId, @RequestParam String permId, @RequestParam String usetId) {
		return roleResourceService.getFunctionDataPerm(viewType,  orgId, permId, usetId);
	}

	@GetMapping("getUserBusinessDataPerm")
	public List<DataPermBusinessVo> getUserBusinessDataPerm(@RequestParam String viewType, @RequestParam String orgId, @RequestParam String permId, @RequestParam String usetId) {
		return roleResourceService.getBusinessDataPerm(viewType, orgId, permId, usetId);
	}

	@GetMapping("getUserColumnDataPerm")
	public List<DataPermColumnVo> getUserColumnDataPerm(@RequestParam String viewType, @RequestParam String orgId, @RequestParam String permId, @RequestParam String usetId) {
		return roleResourceService.getColumnDataPerm(viewType, orgId, permId, usetId);
	}
}
