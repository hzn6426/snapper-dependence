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
import com.baomibing.authority.dto.*;
import com.baomibing.authority.entity.SysTenantUserBusinessPerm;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.mapper.SysTenantUserBusinessPermMapper;
import com.baomibing.authority.service.*;
import com.baomibing.core.common.Assert;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SysTenantUserBusinessPermServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysTenantUserBusinessPermServiceImpl extends MBaseServiceImpl<SysTenantUserBusinessPermMapper, SysTenantUserBusinessPerm, SysTenantUserBusinessPermDto> implements SysTenantUserBusinessPermService {

    @Autowired private SysBusinessPermService businessPermService;
    @Autowired private SysTenantUserEntrustService userEntrustService;
    @Autowired private SysTenantGroupEntrustService groupEntrustServie;
    @Autowired private SysTenantGroupExceptEntrustService groupExceptEntrustService;
    @Autowired private SysTenantUserExceptEntrustService userExceptEntrustService;
    @Override
    public BusinessPermScopeEnum getUserBusinessPermScopeByAction(String tenantId, String orgId, String userId, String action) {
        BusinessPermScopeEnum permTypeEnum = BusinessPermScopeEnum.CURRENT_USER;
        if (Checker.beNull(action) || Checker.beEmpty(userId) || Checker.beEmpty(orgId) || Checker.beEmpty(tenantId)) {
            return permTypeEnum;
        }
        String permType = this.baseMapper.getUserBusinessPermScopeByAction(tenantId, orgId, userId, action);

        if (Checker.beNotEmpty(permType)) {
            permTypeEnum = BusinessPermScopeEnum.valueOf(permType);
        }
        return permTypeEnum;
    }

    @Override
    public BusinessPermScopeEnum getUserBusinessByMethodAndUrl(String tenantId, String orgId, String userId, String method, String url) {
        BusinessPermScopeEnum permTypeEnum = BusinessPermScopeEnum.CURRENT_USER;
        if (Checker.beEmpty(method) || Checker.beEmpty(url) || Checker.beEmpty(userId)) {
            return permTypeEnum;
        }
        String permType = this.baseMapper.getUserBusinessPermScopeByMethodAndUrl(tenantId, orgId, userId, method, url);

        if (Checker.beNotEmpty(permType)) {
            permTypeEnum = BusinessPermScopeEnum.valueOf(permType);
        }
        return permTypeEnum;
    }

    @Override
    public BusinessPermScopeEnum getUserBusiness(String tenantId, String orgId, String userId, String permId) {
        BusinessPermScopeEnum permTypeEnum = BusinessPermScopeEnum.CURRENT_USER;
        if (Checker.beEmpty(userId) || Checker.beEmpty(permId) || Checker.beEmpty(orgId) || Checker.beEmpty(tenantId)) {
            return permTypeEnum;
        }
        LambdaQueryWrapper<SysTenantUserBusinessPerm> wrapper = lambdaQuery();
        wrapper.eq(SysTenantUserBusinessPerm::getUserId, userId).eq(SysTenantUserBusinessPerm::getPermId, permId).eq(SysTenantUserBusinessPerm::getOrgId, orgId).eq(SysTenantUserBusinessPerm::getTenantId, tenantId);
        SysTenantUserBusinessPerm bp = this.baseMapper.selectOne(wrapper);
        if (Checker.beNull(bp)) {
            return permTypeEnum;
        }
        // 用户权限须在时间段内有效
        if (Checker.beNotNull(bp.getPermStartTime()) && Checker.beNotNull(bp.getPermEndTime())) {
            Date now = new Date();
            boolean isOk = now.before(bp.getPermEndTime()) && now.after(bp.getPermStartTime());
            if (!isOk) {
                return permTypeEnum;
            }
        }
        permTypeEnum = BusinessPermScopeEnum.valueOf(bp.getPermScope());
        return permTypeEnum;
    }

    @Override
    public void saveUserBusinessPerm(SysTenantUserBusinessPermDto v) {
        Assert.CheckArgument(v);
        doSetTenantId(v);
        Assert.CheckArgument(v.getTenantId());
        BusinessPermDto bp = businessPermService.getIt(v.getPermId());
        if (Checker.beNull(bp)) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.CANNOT_FIND_THE_ID_OF_BUSINESS_PERM, v.getPermId());
        }
        BusinessPermScopeEnum scope = null;
        if (Checker.beEmpty(v.getPermScope()) || Checker.beNull(scope = EnumUtils.getEnum(BusinessPermScopeEnum.class, v.getPermScope()))) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.INVALID_BUSINESS_PERM_SCOPE, v.getPermScope());
        }

        //删除委托信息
        userEntrustService.deleteByGroupAndUserAndPerm(v.getTenantId(), v.getOrgId(), v.getUserId(), v.getPermId());
        groupEntrustServie.deleteByGroupAndUserAndPerm(v.getTenantId(), v.getOrgId(), v.getUserId(), v.getPermId());

        userExceptEntrustService.deleteByGroupAndUserAndPerm(v.getTenantId(), v.getOrgId(), v.getUserId(), v.getPermId());
        groupExceptEntrustService.deleteByGroupAndUserAndPerm(v.getTenantId(), v.getOrgId(), v.getUserId(), v.getPermId());
        List<SysTenantGroupEntrustDto> groupEntrustList = Lists.newArrayList();
        List<SysTenantUserEntrustDto> userEntrustList = Lists.newArrayList();
        List<SysTenantGroupExceptEntrustDto> exceptGroupEntrustList = Lists.newArrayList();
        List<SysTenantUserExceptEntrustDto> exceptUserEntrustList = Lists.newArrayList();
        //保存可视范围的委托信息
        switch (scope) {
            case CUSTOMER_SPECIFIED:
                List<String> groupEntrusts = ObjectUtil.defaultIfNull(v.getGroupEntrusts(), Lists.newArrayList());
                List<String> userEntrusts = ObjectUtil.defaultIfNull(v.getUserEntrusts(), Lists.newArrayList());
                List<String> exceptGroupEntrusts = ObjectUtil.defaultIfNull(v.getExceptGroupEntrusts(),Lists.newArrayList());
                List<String> exceptUserEntrusts = ObjectUtil.defaultIfNull(v.getExceptUserEntrusts(),Lists.newArrayList());
                groupEntrusts.forEach(id ->
                        groupEntrustList.add(new SysTenantGroupEntrustDto().setPermId(bp.getId()).setGroupEntrustId(id)
                                .setUserId(v.getUserId()).setOrgId(v.getOrgId()).setTenantId(v.getTenantId())));

                userEntrusts.forEach(id ->
                        userEntrustList.add(new SysTenantUserEntrustDto().setPermId(bp.getId()).setUserEntrustId(id)
                                .setUserId(v.getUserId()).setOrgId(v.getOrgId()).setTenantId(v.getTenantId())));

                exceptGroupEntrusts.forEach(id ->
                        exceptGroupEntrustList.add(new SysTenantGroupExceptEntrustDto().setPermId(bp.getId()).setGroupExceptEntrustId(id)
                                .setUserId(v.getUserId()).setOrgId(v.getOrgId()).setTenantId(v.getTenantId())));

                exceptUserEntrusts.forEach(id ->
                        exceptUserEntrustList.add(new SysTenantUserExceptEntrustDto().setPermId(bp.getId()).setUserExceptEntrustId(id)
                                .setUserId(v.getUserId()).setOrgId(v.getOrgId()).setTenantId(v.getTenantId())));
                break;
            case CURRENT_USER:
                this.deleteGroupUserPerm(v.getTag(), v.getOrgId(), v.getUserId(), v.getPermId());
                return;
            default:
                break;
        }

        if (Checker.beNotEmpty(groupEntrustList)) {
            groupEntrustServie.saveItBatch(groupEntrustList);
        }
        if (Checker.beNotEmpty(userEntrustList)) {
            userEntrustService.saveItBatch(userEntrustList);
        }

        if (Checker.beNotEmpty(exceptGroupEntrustList)) {
            groupExceptEntrustService.saveItBatch(exceptGroupEntrustList);
        }

        if (Checker.beNotEmpty(exceptUserEntrustList)) {
            userExceptEntrustService.saveItBatch(exceptUserEntrustList);
        }
        this.deleteGroupUserPerm(v.getTenantId(), v.getOrgId(), v.getUserId(), v.getPermId());
        super.saveIt(v);
    }

    @Override
    public List<String> listUserEntrustIdsByPerm(String tenantId, String orgId, String userId, String permId) {
        BusinessPermDto bp = businessPermService.getIt(permId);
        if (Checker.beNull(bp)) {
            return Lists.newArrayList();
        }

        List<String> list = userEntrustService.listEntrustUserIdsByGroupAndUserAndPerm(tenantId, orgId, userId, permId);
        return Checker.beEmpty(list) ? Lists.newArrayList() : list;
    }

    @Override
    public List<String> listUserExceptEntrustIdsByPerm(String tenantId, String orgId, String userId, String permId) {
        BusinessPermDto bp = businessPermService.getIt(permId);
        if (Checker.beNull(bp)) {
            return Lists.newArrayList();
        }

        List<String> list = userExceptEntrustService.listEntrustUserIdsByGroupAndUserAndPerm(tenantId, orgId, userId, permId);
        return Checker.beEmpty(list) ? Lists.newArrayList() : list;
    }

    @Override
    public List<String> listGroupEntrustIdsByPerm(String tenantId, String orgId, String userId, String permId) {
        BusinessPermDto bp = businessPermService.getIt(permId);
        if (Checker.beNull(bp)) {
            return Lists.newArrayList();
        }
        List<SysTenantGroupDto> list = groupEntrustServie.listEntrustGroupsByGroupAndUserAndPerm(tenantId, orgId, userId, permId);
        if (Checker.beEmpty(list)) return Lists.newArrayList();
        return list.stream().map(SysTenantGroupDto::getId).collect(Collectors.toList());
    }

    @Override
    public List<String> listGroupExceptEntrustIdsByPerm(String tenantId, String orgId, String userId, String permId) {
        BusinessPermDto bp = businessPermService.getIt(permId);
        if (Checker.beNull(bp)) {
            return Lists.newArrayList();
        }
        List<SysTenantGroupDto> list = groupExceptEntrustService.listEntrustGroupsByGroupAndUserAndPerm(tenantId, orgId, userId, permId);
        if (Checker.beEmpty(list)) return Lists.newArrayList();
        return list.stream().map(SysTenantGroupDto::getId).collect(Collectors.toList());
    }

    @Override
    public void deleteByGroupAndUsers(String tenantId, String orgId, Set<String> uids) {
        Assert.CheckArgument(uids);
        Assert.CheckArgument(orgId);
        Assert.CheckArgument(tenantId);
        this.baseMapper.delete(lambdaQuery().eq(SysTenantUserBusinessPerm::getOrgId, orgId).in(SysTenantUserBusinessPerm::getUserId, uids));
    }

    @Override
    public void deleteByUsers(Set<String> uids, String tenantId) {
        Assert.CheckArgument(uids);
        Assert.CheckArgument(tenantId);
        this.baseMapper.delete(lambdaQuery().in(SysTenantUserBusinessPerm::getUserId, uids));
    }

    @Override
    public void deleteGroupUserPerm(String tenantId, String orgId, String userId, String permId) {
        Assert.CheckArgument(tenantId);
        Assert.CheckArgument(userId);
        Assert.CheckArgument(permId);
        Assert.CheckArgument(orgId);
        this.baseMapper.delete(lambdaQuery().eq(SysTenantUserBusinessPerm::getOrgId, orgId).eq(SysTenantUserBusinessPerm::getUserId, userId).eq(SysTenantUserBusinessPerm::getPermId, permId));
    }
}
