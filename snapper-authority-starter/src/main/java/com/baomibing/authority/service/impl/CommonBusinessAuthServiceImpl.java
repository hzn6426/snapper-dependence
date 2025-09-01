/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.service.impl;


import com.baomibing.authority.constant.enums.BusinessPermScopeEnum;
import com.baomibing.authority.constant.enums.GroupPermRangeEnum;
import com.baomibing.authority.constant.enums.PositionPermScopeEnum;
import com.baomibing.authority.dto.*;
import com.baomibing.authority.service.*;
import com.baomibing.core.annotation.NotAuthAop;
import com.baomibing.core.wrap.*;
import com.baomibing.tool.user.User;
import com.baomibing.tool.user.UserContext;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.baomibing.authority.constant.enums.BusinessPermScopeEnum.CURRENT_USER;
import static com.baomibing.tool.util.Checker.beNotEmpty;

/**
 * 通用数据权限服务实现类
 * @author zening
 * @version 1.0.0
 */
@Service
public class CommonBusinessAuthServiceImpl implements CommonBusinessAuthService {

	@Autowired private SysGroupService groupService;
	@Autowired private SysBusinessPermService permService;

	@Autowired private SysPositionService positionService;
	@Autowired private SysPositionUserEntrustService positionUserEntrustService;
	@Autowired private SysPositionGroupEntrustService positionGroupEntrustService;

	@Autowired private SysUserUsetService userUsetService;

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
		BusinessPermScopeEnum permTypeEnum = CURRENT_USER;
		List<UsetDto> usets = userUsetService.listUsetsByGroupAndUser(user.getCurrentGroupId(), user.getId());
		Set<String> usetIds = usets.stream().map(UsetDto::getId).collect(Collectors.toSet());
		BusinessPermScopeEnum usetPermTypeEnum = CURRENT_USER;

		GroupDto companyGroup = groupService.getIt(user.getCompanyId());
		// 如果是全部
		if (permTypeEnum == BusinessPermScopeEnum.SCOPE_ALL || usetPermTypeEnum == BusinessPermScopeEnum.SCOPE_ALL) {
			wrap.setGroupWraps(Lists.newArrayList()).setUserNos(Lists.newArrayList()).setScope(BusinessPermScopeEnum.SCOPE_ALL.name())
					.setCompanyWrap(new GroupIntervalWrap(companyGroup.getId(), companyGroup.getGlft(), companyGroup.getGrht()))
					.setCompanyGroupWraps(groupService.unionGroupInterval(Lists.newArrayList(companyGroup)));
			return wrap;
		}

		//用户权限
		if (permTypeEnum == CURRENT_USER) {// 说明未配置特殊权限，走默认的组织范围
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
//		if (beNotEmpty(exceptGroupEntrusts)) {
//			exceptGroupIntervalList = groupService.unionGroupInterval(exceptGroupEntrusts);
//		}

		//获取数据权限
		List<DataPermWrap> dataPerms = Lists.newArrayList();
		//获取列权限
		List<ColumnPermWrap> exceptColumns = Lists.newArrayList();

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



}
