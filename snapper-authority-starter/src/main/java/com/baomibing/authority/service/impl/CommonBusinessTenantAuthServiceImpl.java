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

import com.alibaba.fastjson.JSONArray;
import com.baomibing.authority.constant.enums.BusinessPermScopeEnum;
import com.baomibing.authority.constant.enums.GroupPermRangeEnum;
import com.baomibing.authority.constant.enums.PositionPermScopeEnum;
import com.baomibing.authority.dto.*;
import com.baomibing.authority.service.*;
import com.baomibing.authority.vo.AdvanceSearchVo;
import com.baomibing.core.annotation.NotAuthAop;
import com.baomibing.core.wrap.*;
import com.baomibing.orm.perm.ActionSelectTable;
import com.baomibing.tool.user.TenantUser;
import com.baomibing.tool.user.UserContext;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.SnowflakeIdWorker;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.baomibing.tool.util.Checker.beNotEmpty;

/**
 * CommonTenantBusinessAuthServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class CommonBusinessTenantAuthServiceImpl implements CommonBusinessTenantAuthService {

    @Autowired private SysTenantGroupService groupService;
    @Autowired private SysTenantUserBusinessPermService userBusinessPermService;
    @Autowired private SysTenantUsetBusinessPermService usetBusinessPermService;
    @Autowired private SysBusinessPermTenantService permService;
    @Autowired private SysTenantUserEntrustService userEntrustService;
    @Autowired private SysTenantUserExceptEntrustService userExceptEntrustService;
    @Autowired private SysTenantGroupEntrustService groupEntrustService;
    @Autowired private SysTenantGroupExceptEntrustService groupExceptEntrustService;
    @Autowired private SysTenantPositionService positionService;
    @Autowired private SysTenantPositionUserEntrustService positionUserEntrustService;
    @Autowired private SysTenantPositionGroupEntrustService positionGroupEntrustService;
    @Autowired private SysTenantUsetUserEntrustService usetUserEntrustService;
    @Autowired private SysTenantUsetUserExceptEntrustService usetUserExceptEntrustService;
    @Autowired private SysTenantUsetGroupEntrustService usetGroupEntrustService;
    @Autowired private SysTenantUsetGroupExceptEntrustService usetGroupExceptEntrustService;
    @Autowired private SysTenantUserUsetService userUsetService;
    @Autowired private SysTenantUserDataPermService userDataPermService;
    @Autowired private SysTenantUsetDataPermService usetDataPermService;
    @Autowired private SysTenantUserColumnPermService userColumnPermService;
    @Autowired private SysTenantUsetColumnPermService usetColumnPermService;

    @Override
    public TenantEntrustWarpper getEntrustBusinessPerm(TenantUser user, String permId, String groupScope, boolean beIgnoreUserScope, boolean beIgnoreGroupScope) {
        TenantEntrustWarpper wrap = new TenantEntrustWarpper();
        if (Checker.beNull(user) || Checker.beEmpty(permId)) {
            return wrap;
        }
        // 组织范围对应的类型
        GroupPermRangeEnum groupScopeTypeEnum = GroupPermRangeEnum.CURRENT_COMPANY;
        //根据用户和权限获取用户权限对应的操作范围
        BusinessPermScopeEnum permTypeEnum = userBusinessPermService.getUserBusiness(user.getTenantId(), user.getCurrentGroupId(), user.getId(), permId);
        List<SysTenantUsetDto> usets = userUsetService.listUsetsByGroupAndUser(user.getTenantId(), user.getCurrentGroupId(), user.getId());
        Set<String> usetIds = usets.stream().map(SysTenantUsetDto::getId).collect(Collectors.toSet());
        BusinessPermScopeEnum usetPermTypeEnum = usetBusinessPermService.getUsetBusiness(usetIds, permId, user.getTenantId());

        SysTenantGroupDto companyGroup = groupService.getIt(user.getTenantId(), user.getCompanyId());
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
        List<SysTenantGroupDto> groupEntrusts = Lists.newArrayList();
        List<SysTenantGroupDto> companyEntrusts = Lists.newArrayList();
        List<SysTenantGroupDto> exceptGroupEntrusts = Lists.newArrayList();
        //根据范围，查询人对应的用户和组织委托
        switch (permTypeEnum) {
            case CUSTOMER_SPECIFIED:
                if (!beIgnoreUserScope) {
                    userNoEntrusts.addAll(userEntrustService.listEntrustUserCodesByGroupAndUserAndPerm(user.getTenantId(), user.getCurrentGroupId(), user.getId(), permId));
                    exceptUserNoEntrusts.addAll(userExceptEntrustService.listEntrustUserCodesByGroupAndUserAndPerm(user.getTenantId(), user.getCurrentGroupId(), user.getId(), permId));
                }
                List<SysTenantGroupDto> userGroupEntrusts = groupEntrustService.listEntrustGroupsByGroupAndUserAndPerm(user.getTenantId(), user.getCurrentGroupId(), user.getId(), permId);
                if (!beIgnoreGroupScope) {
                    groupEntrusts.addAll(userGroupEntrusts);
                    exceptGroupEntrusts = groupExceptEntrustService.listEntrustGroupsByGroupAndUserAndPerm(user.getTenantId(), user.getCurrentGroupId(), user.getId(), permId);
                }
                companyEntrusts.addAll(userGroupEntrusts);
                break;
            case CURRENT_GROUP:
                if (beNotEmpty(user.getCurrentGroupId())) {
                    SysTenantGroupDto currentGroupEntrust = groupService.getIt(user.getTenantId(), user.getCurrentGroupId());
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
                    userNoEntrusts.addAll(usetUserEntrustService.listEntrustUserCodesByUsetAndPerm(usetIds, permId, user.getTenantId()));
                    exceptUserNoEntrusts.addAll(usetUserExceptEntrustService.listEntrustUserCodesByUsetAndPerm(usetIds, permId, user.getTenantId()));
                }
                List<SysTenantGroupDto> usetGroupEntrusts = usetGroupEntrustService.listEntrustGroupsByUsetAndPerm(usetIds, permId, user.getTenantId());
                if (!beIgnoreGroupScope) {
                    groupEntrusts.addAll(usetGroupEntrusts);
                    exceptGroupEntrusts.addAll(usetGroupExceptEntrustService.listEntrustGroupsByUsetAndPerm(usetIds, permId, user.getTenantId()));
                }
                companyEntrusts.addAll(usetGroupEntrusts);
                break;
            case CURRENT_GROUP:
                if (beNotEmpty(user.getCurrentGroupId())) {
                    SysTenantGroupDto usetGroup = groupService.getIt(user.getTenantId(), user.getCurrentGroupId());
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
                if (Checker.beNull(companyGroup)) {
                    wrap.setCompanyWrap(new GroupIntervalWrap(SnowflakeIdWorker.getId(), -1, -2));
                } else {
                    wrap.setCompanyWrap(new GroupIntervalWrap(companyGroup.getId(), companyGroup.getGlft(), companyGroup.getGrht()));
                }
            }
        }


        SysTenantPositionDto position = positionService.getIt(UserContext.currentUserPositionId());
        if (Checker.beNotNull(position)) {
            PositionPermScopeEnum ppse = Checker.beEmpty(position.getPermScope()) ? null : PositionPermScopeEnum.valueOf(position.getPermScope());
            if (Checker.beNotNull(ppse)) {
                switch (ppse) {
                    case CURRENT_GROUP:
                        if (beNotEmpty(position.getOrgId())) {
                            SysTenantGroupDto positionGroupEntrust = groupService.getIt(user.getTenantId(), position.getOrgId());
                            if (!beIgnoreGroupScope) {
                                groupEntrusts.add(positionGroupEntrust);
                            }
                            companyEntrusts.add(positionGroupEntrust);
                        }
                        break;
                    case UNDERLING_GROUP:
                        List<SysTenantGroupDto> positionUnderGroupEntrusts = groupService.listChildrenByParent(user.getTenantId(), position.getOrgId());
                        if (!beIgnoreGroupScope) {
                            groupEntrusts.addAll(positionUnderGroupEntrusts);
                        }
                        companyEntrusts.addAll(positionUnderGroupEntrusts);
                        break;
                    case CUSTOMER_SPECIFIED:
                        permTypeEnum = BusinessPermScopeEnum.CUSTOMER_SPECIFIED;
                        if (!beIgnoreUserScope) {
                            userNoEntrusts.addAll(positionUserEntrustService.listEntrustUserCodesByPosition(user.getTenantId(), position.getId()));
                        }
                        List<SysTenantGroupDto> positionSpecGroupEntrusts = positionGroupEntrustService.listEntrustGroupsByPosition(user.getTenantId(), position.getId());
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

        wrap.setScope(permTypeEnum.name())
                .setUserNos(userNoEntrusts)
                .setGroupWraps(groupIntervalList)
                .setCompanyGroupWraps(companyGroupInterValList)
                .setExceptUserNos(exceptUserNoEntrusts)
                .setExceptGroupWraps(exceptGroupIntervalList)
                .setDataPerms(dataPerms)
                .setExceptColumns(exceptColumns);
        return wrap;
    }

    @Override
    public String getPermActionByUrlAndMethod(String url, String method) {
        BusinessPermTenantDto perm = permService.getByUrlAndMethod(url, method);
        if (Checker.beNotNull(perm)) {
            return perm.getPermAction();
        }
        return null;
    }

    @Override
    public String getPermIdByUrlAndMethod(String url, String method) {
        BusinessPermTenantDto perm = permService.getByUrlAndMethod(url, method);
        if (Checker.beNotNull(perm)) {
            return perm.getId();
        }
        return null;
    }

    @Override
    public String getPermIdByAction(String action) {
        BusinessPermTenantDto perm = permService.getByAction(action);
        if (Checker.beNotNull(perm)) {
            return perm.getId();
        }
        return null;
    }

    @NotAuthAop
    @Override
    public List<DataPermWrap> getEntrustDataPerm(TenantUser user, String permId) {
        List<DataPermWrap> list = Lists.newArrayList();
        if (Checker.beNull(user) || Checker.beEmpty(permId)) {
            return list;
        }
        SysTenantUserDataPermDto userDataPerm = userDataPermService.getUserDataPerm(user.getTenantId(), user.getId(), user.getCurrentGroupId(), permId);
        List<SysTenantUsetDto> usets = userUsetService.listUsetsByGroupAndUser(user.getTenantId(), user.getCurrentGroupId(), user.getId());
        Set<String> usetIds = usets.stream().map(SysTenantUsetDto::getId).collect(Collectors.toSet());
        List<SysTenantUsetDataPermDto> usetDataPerms = usetDataPermService.listUsetDataPerm(usetIds, permId, user.getTenantId());
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
            for (SysTenantUsetDataPermDto perm : usetDataPerms) {
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
    public List<ColumnPermWrap> getEntrustColumnPerm(TenantUser user, String permId) {
        List<ColumnPermWrap> list = Lists.newArrayList();
        if (Checker.beNull(user) || Checker.beEmpty(permId)) {
            return list;
        }
        SysTenantUserColumnPermDto userColumnPerm = userColumnPermService.getUserColumnPerm(user.getTenantId(), user.getId(), user.getCurrentGroupId(), permId);
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
        List<SysTenantUsetDto> usets = userUsetService.listUsetsByGroupAndUser(user.getTenantId(), user.getCurrentGroupId(), user.getId());
        Set<String> usetIds = usets.stream().map(SysTenantUsetDto::getId).collect(Collectors.toSet());
        List<SysTenantUsetColumnPermDto> usetColumnPerms = usetColumnPermService.listUsetColumnPerm(usetIds, permId, user.getTenantId());
        if (Checker.beNotEmpty(usetColumnPerms)) {
            for (SysTenantUsetColumnPermDto perm : usetColumnPerms) {
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
}
