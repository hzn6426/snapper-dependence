
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


import com.baomibing.authority.constant.enums.BusinessPermScopeEnum;
import com.baomibing.authority.constant.enums.GroupPermRangeEnum;
import com.baomibing.authority.constant.enums.PositionPermScopeEnum;
import com.baomibing.authority.dto.*;
import com.baomibing.authority.service.*;
import com.baomibing.authority.vo.AdvanceSearchVo;
import com.baomibing.core.annotation.NotAuthAop;
import com.baomibing.core.wrap.*;
import com.baomibing.orm.perm.ActionSelectTable;
import com.baomibing.tool.user.User;
import com.baomibing.tool.user.UserContext;
import com.baomibing.tool.util.Checker;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.baomibing.tool.util.Checker.beNotEmpty;

/**
 * 通用数据权限服务实现类
 * @author zening
 * @version 1.0.0
 */
@Service
public class CommonBusinessAuthServiceImpl implements CommonBusinessAuthService {

	@Autowired private SysGroupService groupService;
	@Autowired private SysUserBusinessPermService userBusinessPermService;
	@Autowired private SysUsetBusinessPermService usetBusinessPermService;
	@Autowired private SysBusinessPermService permService;
	@Autowired private SysUserEntrustService userEntrustService;
	@Autowired private SysUserExceptEntrustService userExceptEntrustService;
	@Autowired private SysGroupEntrustService groupEntrustService;
	@Autowired private SysGroupExceptEntrustService groupExceptEntrustService;
	@Autowired private SysPositionService positionService;
	@Autowired private SysPositionUserEntrustService positionUserEntrustService;
	@Autowired private SysPositionGroupEntrustService positionGroupEntrustService;
	@Autowired private SysUsetUserEntrustService usetUserEntrustService;
	@Autowired private SysUsetUserExceptEntrustService usetUserExceptEntrustService;
	@Autowired private SysUsetGroupEntrustService usetGroupEntrustService;
	@Autowired private SysUsetGroupExceptEntrustService usetGroupExceptEntrustService;
	@Autowired private SysUserUsetService userUsetService;
	@Autowired private SysUserDataPermService userDataPermService;
	@Autowired private SysUsetDataPermService usetDataPermService;
	@Autowired private SysUserColumnPermService userColumnPermService;
	@Autowired private SysUsetColumnPermService usetColumnPermService;
	@NotAuthAop
	@Override
	public EntrustWarpper getEntrustBusinessPerm(User user, String permId, String groupScope, boolean beIgnoreUserScope, boolean beIgnoreGroupScope) {
		EntrustWarpper wrap = new EntrustWarpper();
		if (Checker.beNull(user) || Checker.beEmpty(permId)) {
			return wrap;
		}
		// 组织范围对应的类型
		GroupPermRangeEnum groupScopeTypeEnum = GroupPermRangeEnum.CURRENT_COMPANY;
		//根据用户和权限获取用户权限对应的操作范围
		BusinessPermScopeEnum permTypeEnum = userBusinessPermService.getUserBusiness(user.getCurrentGroupId(), user.getId(), permId);
		List<UsetDto> usets = userUsetService.listUsetsByGroupAndUser(user.getCurrentGroupId(), user.getId());
		Set<String> usetIds = usets.stream().map(UsetDto::getId).collect(Collectors.toSet());
		BusinessPermScopeEnum usetPermTypeEnum = usetBusinessPermService.getUsetBusiness(usetIds, permId);

		GroupDto companyGroup = groupService.getIt(user.getCompanyId());
		// 如果是全部
		if (permTypeEnum == BusinessPermScopeEnum.SCOPE_ALL || usetPermTypeEnum == BusinessPermScopeEnum.SCOPE_ALL) {
			wrap.setGroupWraps(Lists.newArrayList()).setUserNos(Lists.newArrayList()).setScope(BusinessPermScopeEnum.SCOPE_ALL.name())
					.setCompanyWrap(new GroupIntervalWrap(companyGroup.getId(), companyGroup.getGlft(), companyGroup.getGrht()))
					.setCompanyGroupWraps(groupService.unionGroupInterval(Lists.newArrayList(companyGroup)));
			return wrap;
		}

		//用户权限
		if (permTypeEnum == BusinessPermScopeEnum.CURRENT_USER) {// 说明未配置特殊权限，走默认的组织范围
			if (beNotEmpty(groupScope)) {
				groupScopeTypeEnum = EnumUtils.getEnum(GroupPermRangeEnum.class, groupScope);
				if (Checker.beNull(groupScopeTypeEnum)) {
					groupScopeTypeEnum = GroupPermRangeEnum.CURRENT_COMPANY;
				}
			} else {
				groupScopeTypeEnum = null;
			}
		}

		List<String> userNoEntrusts = Lists.newArrayList();
		List<String> exceptUserNoEntrusts = Lists.newArrayList();
		if (!beIgnoreUserScope) {
			userNoEntrusts.add(user.getUserName());
		}
		List<GroupDto> groupEntrusts = Lists.newArrayList();
		List<GroupDto> companyEntrusts = Lists.newArrayList();
		List<GroupDto> exceptGroupEntrusts = Lists.newArrayList();
		//根据范围，查询人对应的用户和组织委托
		switch (permTypeEnum) {
			case CUSTOMER_SPECIFIED:
				if (!beIgnoreUserScope) {
					userNoEntrusts.addAll(userEntrustService.listEntrustUserCodesByGroupAndUserAndPerm(user.getCurrentGroupId(), user.getId(), permId));
					exceptUserNoEntrusts.addAll(userExceptEntrustService.listEntrustUserCodesByGroupAndUserAndPerm(user.getCurrentGroupId(), user.getId(), permId));
				}
				List<GroupDto> userGroupEntrusts = groupEntrustService.listEntrustGroupsByGroupAndUserAndPerm(user.getCurrentGroupId(), user.getId(), permId);
				if (!beIgnoreGroupScope) {
					groupEntrusts.addAll(userGroupEntrusts);
					exceptGroupEntrusts.addAll(groupExceptEntrustService.listEntrustGroupsByGroupAndUserAndPerm(user.getCurrentGroupId(), user.getId(), permId));
				}
				companyEntrusts.addAll(userGroupEntrusts);
				break;
			case CURRENT_GROUP:
				if (beNotEmpty(user.getCurrentGroupId())) {
					GroupDto currentGroupEntrust = groupService.getIt(user.getCurrentGroupId());
					if (!beIgnoreGroupScope) {
						groupEntrusts.add(currentGroupEntrust);
					}
					companyEntrusts.add(currentGroupEntrust);
				}
				break;
			default:
				break;
		}


		//用户组权限
		GroupPermRangeEnum usetGroupScopeTypeEnum = GroupPermRangeEnum.CURRENT_COMPANY;
		if (usetPermTypeEnum == BusinessPermScopeEnum.CURRENT_USER) {// 说明未配置特殊权限，走默认的组织范围
			if (beNotEmpty(groupScope)) {
				usetGroupScopeTypeEnum = EnumUtils.getEnum(GroupPermRangeEnum.class, groupScope);
				if (Checker.beNull(usetGroupScopeTypeEnum)) {
					usetGroupScopeTypeEnum = GroupPermRangeEnum.CURRENT_COMPANY;
				}
			} else {
				usetGroupScopeTypeEnum = null;
			}
		}

		if (Checker.beNotNull(usetGroupScopeTypeEnum) && usetGroupScopeTypeEnum != GroupPermRangeEnum.CURRENT_COMPANY) {
			groupScopeTypeEnum = usetGroupScopeTypeEnum;
		}


		//根据范围，查询人对应的用户和组织委托
		switch (usetPermTypeEnum) {
			case CUSTOMER_SPECIFIED:
				permTypeEnum = BusinessPermScopeEnum.CUSTOMER_SPECIFIED;
				if (!beIgnoreUserScope) {
					userNoEntrusts.addAll(usetUserEntrustService.listEntrustUserCodesByUsetAndPerm(usetIds, permId));
					exceptUserNoEntrusts.addAll(usetUserExceptEntrustService.listEntrustUserCodesByUsetAndPerm(usetIds, permId));
				}
				List<GroupDto> usetGroupEntrusts = usetGroupEntrustService.listEntrustGroupsByUsetAndPerm(usetIds, permId);
				if (!beIgnoreGroupScope) {
					groupEntrusts.addAll(usetGroupEntrusts);
					exceptGroupEntrusts.addAll(usetGroupExceptEntrustService.listEntrustGroupsByUsetAndPerm(usetIds, permId));
				}
				companyEntrusts.addAll(usetGroupEntrusts);
				break;
			case CURRENT_GROUP:
				if (beNotEmpty(user.getCurrentGroupId())) {
					GroupDto usetGroup = groupService.getIt(user.getCurrentGroupId());
					if (!beIgnoreGroupScope) {
						groupEntrusts.add(usetGroup);
					}
					companyEntrusts.add(usetGroup);
				}
				break;
			default:
				break;
		}


		if (Checker.beNotNull(groupScopeTypeEnum)) {
			if (groupScopeTypeEnum == GroupPermRangeEnum.CURRENT_COMPANY) {//				if (Checker.beNotEmpty(user.getCompanyId())) {
				wrap.setCompanyWrap(new GroupIntervalWrap(companyGroup.getId(), companyGroup.getGlft(), companyGroup.getGrht()));
			}
		}


		PositionDto position = positionService.getIt(UserContext.currentUserPositionId());
		if (Checker.beNotNull(position)) {
			PositionPermScopeEnum ppse = Checker.beEmpty(position.getPermScope()) ? null : PositionPermScopeEnum.valueOf(position.getPermScope());
			if (Checker.beNotNull(ppse)) {
				switch (ppse) {
					case CURRENT_GROUP:
						if (beNotEmpty(position.getOrgId())) {
							GroupDto positionGroupEntrust = groupService.getIt(position.getOrgId());
							if (!beIgnoreGroupScope) {
								groupEntrusts.add(positionGroupEntrust);
							}
							companyEntrusts.add(positionGroupEntrust);
						}
						break;
					case UNDERLING_GROUP:
						List<GroupDto> positionUnderGroupEntrusts = groupService.listChildrenByParent(position.getOrgId());
						if (!beIgnoreGroupScope) {
							groupEntrusts.addAll(positionUnderGroupEntrusts);
						}
						companyEntrusts.addAll(positionUnderGroupEntrusts);
						break;
					case CUSTOMER_SPECIFIED:
						permTypeEnum = BusinessPermScopeEnum.CUSTOMER_SPECIFIED;
						if (!beIgnoreUserScope) {
							userNoEntrusts.addAll(positionUserEntrustService.listEntrustUserCodesByPosition(position.getId()));
						}
						List<GroupDto> positionSpecGroupEntrusts = positionGroupEntrustService.listEntrustGroupsByPosition(position.getId());
						if (!beIgnoreGroupScope) {
							groupEntrusts.addAll(positionSpecGroupEntrusts);
						}
						companyEntrusts.addAll(positionSpecGroupEntrusts);
						break;
					default:
						break;
				}
			}
		}
		//groups取并集
		List<GroupIntervalWrap> groupIntervalList = groupService.unionGroupInterval(groupEntrusts);
		//取公司和组织的并集
		if (Checker.beNotNull(companyGroup)){
			groupEntrusts.add(companyGroup);
			companyEntrusts.add(companyGroup);
		}
		List<GroupIntervalWrap> companyGroupInterValList = groupService.unionGroupInterval(companyEntrusts);
		//排除组织取并集
		List<GroupIntervalWrap> exceptGroupIntervalList = Lists.newArrayList();
		if (beNotEmpty(exceptGroupEntrusts)) {
			exceptGroupIntervalList = groupService.unionGroupInterval(exceptGroupEntrusts);
		}

		//获取数据权限
		List<DataPermWrap> dataPerms = getEntrustDataPerm(user, permId);
		//获取列权限
		List<ColumnPermWrap> exceptColumns = getEntrustColumnPerm(user, permId);

		return wrap.setScope(permTypeEnum.name())
				.setUserNos(userNoEntrusts)
				.setGroupWraps(groupIntervalList)
				.setCompanyGroupWraps(companyGroupInterValList)
				.setExceptUserNos(exceptUserNoEntrusts)
				.setExceptGroupWraps(exceptGroupIntervalList)
				.setDataPerms(dataPerms)
				.setExceptColumns(exceptColumns);
	}

	@Override
	public String getPermActionByUrlAndMethod(String url, String method) {
		BusinessPermDto perm = permService.getByUrlAndMethod(url, method);
		if (Checker.beNotNull(perm)) {
			return perm.getPermAction();
		}
		return null;
	}

	@NotAuthAop
	@Override
	public String getPermIdByUrlAndMethod(String url, String method) {
		BusinessPermDto perm = permService.getByUrlAndMethod(url, method);
		if (Checker.beNotNull(perm)) {
			return perm.getId();
		}
		return null;
	}

	@NotAuthAop
	@Override
	public String getPermIdByAction(String action) {
		BusinessPermDto perm = permService.getByAction(action);
		if (Checker.beNotNull(perm)) {
			return perm.getId();
		}
		return null;
	}

	@NotAuthAop
	@Override
	public List<DataPermWrap> getEntrustDataPerm(User user, String permId) {
		List<DataPermWrap> list = Lists.newArrayList();
		if (Checker.beNull(user) || Checker.beEmpty(permId)) {
			return list;
		}
		UserDataPermDto userDataPerm = userDataPermService.getUserDataPerm(user.getId(), user.getCurrentGroupId(), permId);
		List<UsetDto> usets = userUsetService.listUsetsByGroupAndUser(user.getCurrentGroupId(), user.getId());
		Set<String> usetIds = usets.stream().map(UsetDto::getId).collect(Collectors.toSet());
		List<UsetDataPermDto> usetDataPerms = usetDataPermService.listUsetDataPerm(usetIds, permId);
		if (Checker.beNotNull(userDataPerm)) {
			if (beNotEmpty(userDataPerm.getPermExpress())) {
				List<AdvanceSearchVo> searchList = JSONArray.parseArray(userDataPerm.getPermExpress(), AdvanceSearchVo.class);
				List<DataPermSearchWrap> wraps = Lists.newArrayList();
				if (beNotEmpty(searchList)) {
					searchList.forEach(s -> wraps.add(new DataPermSearchWrap().setPermExpresses(s.getExpress())
							.setAliasName(s.getAliasName()).setTableName(s.getTableName())
							.setConditionTableAlias(s.getConditionTableAlias())));
					list.add(new DataPermWrap().setSearchWraps(wraps).setBeOrCondition(userDataPerm.getBeOrCondition()));
				}
			}
		}
		if (beNotEmpty(usetDataPerms)) {
			for (UsetDataPermDto perm : usetDataPerms) {
				if (beNotEmpty(perm.getPermExpress())) {
					List<AdvanceSearchVo> searchList = JSONArray.parseArray(perm.getPermExpress(), AdvanceSearchVo.class);
					List<DataPermSearchWrap> wraps = Lists.newArrayList();
					if (beNotEmpty(searchList)) {
						searchList.forEach(s -> wraps.add(new DataPermSearchWrap().setPermExpresses(s.getExpress())
								.setAliasName(s.getAliasName()).setTableName(s.getTableName())
								.setConditionTableAlias(s.getConditionTableAlias())));
						list.add(new DataPermWrap().setSearchWraps(wraps).setBeOrCondition(perm.getBeOrCondition()));
					}
				}
			}
		}

		return list;
	}

	@Override
	public List<ColumnPermWrap> getEntrustColumnPerm(User user, String permId) {
		List<ColumnPermWrap> list = Lists.newArrayList();
		if (Checker.beNull(user) || Checker.beEmpty(permId)) {
			return list;
		}
		UserColumnPermDto userColumnPerm = userColumnPermService.getUserColumnPerm(user.getId(), user.getCurrentGroupId(), permId);
		if (Checker.beNotNull(userColumnPerm)) {
			if (Checker.beNotEmpty(userColumnPerm.getColumnExpress())) {
				List<ActionSelectTable> actionTables = JSONArray.parseArray(userColumnPerm.getColumnExpress(), ActionSelectTable.class);
				if (Checker.beNotEmpty(actionTables)) {
					actionTables.forEach(at -> {
						at.getColumns().forEach(c -> list.add(new ColumnPermWrap()
							.setColumnName(c.getColumnName())
							.setTableName(c.getTableName())
							.setBeSpecial(c.getBeSpecial())));
					});
				}
			}
		}
		List<UsetDto> usets = userUsetService.listUsetsByGroupAndUser(user.getCurrentGroupId(), user.getId());
		Set<String> usetIds = usets.stream().map(UsetDto::getId).collect(Collectors.toSet());
		List<UsetColumnPermDto> usetColumnPerms = usetColumnPermService.listUsetColumnPerm(usetIds, permId);
		if (Checker.beNotEmpty(usetColumnPerms)) {
			for (UsetColumnPermDto perm : usetColumnPerms) {
				if (Checker.beNotEmpty(perm.getColumnExpress())) {
					List<ActionSelectTable> actionTables = JSONArray.parseArray(perm.getColumnExpress(), ActionSelectTable.class);
					if (Checker.beNotEmpty(actionTables)) {
						actionTables.forEach(at -> {
							at.getColumns().forEach(c -> list.add(new ColumnPermWrap()
								.setColumnName(c.getColumnName())
								.setTableName(c.getTableName())
								.setBeSpecial(c.getBeSpecial())));
						});
					}
				}
			}
		}
		return list;
	}


	public static void main(String[] args) {
		BusinessPermScopeEnum businessPermScopeEnum = BusinessPermScopeEnum.valueOf("CURRENT_COMPANY");
		System.out.println(businessPermScopeEnum);
	}
}
