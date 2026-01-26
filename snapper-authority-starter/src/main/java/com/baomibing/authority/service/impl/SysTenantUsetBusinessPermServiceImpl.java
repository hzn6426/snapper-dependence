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
import com.baomibing.authority.entity.SysTenantUsetBusinessPerm;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.mapper.SysTenantUsetBusinessPermMapper;
import com.baomibing.authority.service.*;
import com.baomibing.core.common.Assert;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ObjectUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SysTenantUsetBusinessPermServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysTenantUsetBusinessPermServiceImpl extends MBaseServiceImpl<SysTenantUsetBusinessPermMapper, SysTenantUsetBusinessPerm, SysTenantUsetBusinessPermDto> implements SysTenantUsetBusinessPermService {

    @Autowired private SysBusinessPermService businessPermService;
    @Autowired private SysTenantUsetUserEntrustService usetUserEntrustService;
    @Autowired private SysTenantUsetGroupEntrustService usetGroupEntrustService;
    @Autowired private SysTenantUsetUserExceptEntrustService usetUserExceptEntrustService;
    @Autowired private SysTenantUsetGroupExceptEntrustService usetGroupExceptEntrustService;
    @Autowired private SysTenantUserUsetService userUsetService;

    @Override
    public void saveUserBusinessPerm(SysTenantUsetBusinessPermDto v) {
        Assert.CheckArgument(v);
        Assert.CheckArgument(v.getTenantId());
        BusinessPermDto bp = businessPermService.getIt(v.getPermId());
        if (Checker.beNull(bp)) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.CANNOT_FIND_THE_ID_OF_BUSINESS_PERM, v.getPermId());
        }
        BusinessPermScopeEnum scope = EnumUtils.getEnum(BusinessPermScopeEnum.class, v.getPermScope());
        if (Checker.beEmpty(v.getPermScope()) || Checker.beNull(scope)) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.INVALID_BUSINESS_PERM_SCOPE, v.getPermScope());
        }

        //删除委托信息
        usetUserEntrustService.deleteByUsetAndPerm(v.getTenantId(), v.getUsetId(), v.getPermId());
        usetGroupEntrustService.deleteByUsetAndPerm(v.getTenantId(), v.getUsetId(), v.getPermId());
        usetUserExceptEntrustService.deleteByUsetAndPerm(v.getTenantId(), v.getUsetId(), v.getPermId());
        usetGroupExceptEntrustService.deleteByUsetAndPerm(v.getTenantId(), v.getUsetId(), v.getPermId());
        List<SysTenantUsetGroupEntrustDto> groupEntrustList = Lists.newArrayList();
        List<SysTenantUsetUserEntrustDto> userEntrustList = Lists.newArrayList();
        List<SysTenantUsetGroupExceptEntrustDto> exceptGroupEntrustList = Lists.newArrayList();
        List<SysTenantUsetUserExceptEntrustDto> exceptUserEntrustList = Lists.newArrayList();
        //保存可视范围的委托信息
        switch (scope) {
            case CUSTOMER_SPECIFIED:
                List<String> groupEntrusts = ObjectUtil.defaultIfNull(v.getGroupEntrusts(), Lists.newArrayList());
                List<String> userEntrusts = ObjectUtil.defaultIfNull(v.getUserEntrusts(), Lists.newArrayList());

                List<String> exceptGroupEntrusts = ObjectUtil.defaultIfNull(v.getExceptGroupEntrusts(), Lists.newArrayList());
                List<String> exceptUserEntrusts = ObjectUtil.defaultIfNull(v.getExceptUserEntrusts(), Lists.newArrayList());
//                if (Checker.beEmpty(groupEntrusts) || Checker.beEmpty(userEntrusts)) break;
                groupEntrusts.forEach(id ->
                        groupEntrustList.add(new SysTenantUsetGroupEntrustDto().setPermId(bp.getId()).setGroupEntrustId(id).setTenantId(v.getTenantId())
                                .setUsetId(v.getUsetId())));

                userEntrusts.forEach(id ->
                        userEntrustList.add(new SysTenantUsetUserEntrustDto().setPermId(bp.getId()).setUserEntrustId(id).setTenantId(v.getTenantId())
                                .setUsetId(v.getUsetId())));


                exceptGroupEntrusts.forEach(id ->
                        exceptGroupEntrustList.add(new SysTenantUsetGroupExceptEntrustDto().setPermId(bp.getId()).setGroupExceptEntrustId(id).setTenantId(v.getTenantId())
                                .setUsetId(v.getUsetId())));

                exceptUserEntrusts.forEach(id ->
                        exceptUserEntrustList.add(new SysTenantUsetUserExceptEntrustDto().setPermId(bp.getId()).setUserExceptEntrustId(id).setTenantId(v.getTenantId())
                                .setUsetId(v.getUsetId())));

                break;
            case CURRENT_USER:
                this.deleteUsetPerm(v.getTenantId(), v.getUsetId(), v.getPermId());
                return;
            default:
                break;
        }

        if (Checker.beNotEmpty(groupEntrustList)) {
            usetGroupEntrustService.saveItBatch(groupEntrustList);
        }

        if (Checker.beNotEmpty(userEntrustList)) {
            usetUserEntrustService.saveItBatch(userEntrustList);
        }

        if (Checker.beNotEmpty(exceptGroupEntrustList)) {
            usetGroupExceptEntrustService.saveItBatch(exceptGroupEntrustList);
        }

        if (Checker.beNotEmpty(exceptUserEntrustList)) {
            usetUserExceptEntrustService.saveItBatch(exceptUserEntrustList);
        }

        this.deleteUsetPerm(v.getTenantId(), v.getUsetId(), v.getPermId());
        super.saveIt(v);
    }

    @Override
    public List<String> listUserEntrustIdsByPerm(String tenantId, String usetId, String permId) {
        if (Checker.beEmpty(usetId) || Checker.beEmpty(permId) || Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }
        return usetUserEntrustService.listEntrustUserIdsByUsetAndPerm(Sets.newHashSet(usetId), permId, tenantId);
    }

    @Override
    public List<String> listUserExceptEntrustIdsByPerm(String tenantId, String usetId, String permId) {
        if (Checker.beEmpty(usetId) || Checker.beEmpty(permId) || Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }
        return usetUserExceptEntrustService.listEntrustUserIdsByUsetAndPerm(Sets.newHashSet(usetId), permId, tenantId);
    }

    @Override
    public List<String> listGroupEntrustIdsByPerm(String tenantId, String usetId, String permId) {
        if (Checker.beEmpty(usetId) || Checker.beEmpty(permId) || Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }
        List<SysTenantGroupDto> list = usetGroupEntrustService.listEntrustGroupsByUsetAndPerm(Sets.newHashSet(usetId), permId, tenantId);
        if (Checker.beEmpty(list)) return Lists.newArrayList();
        return list.stream().map(SysTenantGroupDto::getId).collect(Collectors.toList());
    }

    @Override
    public List<String> listGroupExceptEntrustIdsByPerm(String tenantId, String usetId, String permId) {
        if (Checker.beEmpty(usetId) || Checker.beEmpty(permId) || Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }
        List<SysTenantGroupDto> list = usetGroupExceptEntrustService.listEntrustGroupsByUsetAndPerm(Sets.newHashSet(usetId), permId, tenantId);
        if (Checker.beEmpty(list)) return Lists.newArrayList();
        return list.stream().map(SysTenantGroupDto::getId).collect(Collectors.toList());
    }

    @Override
    public void deleteUsetPerm(String tenantId, String usetId, String permId) {
        Assert.CheckArgument(usetId);
        Assert.CheckArgument(permId);
        Assert.CheckArgument(tenantId);
        this.baseMapper.delete(lambdaQuery().eq(SysTenantUsetBusinessPerm::getPermId, permId).eq(SysTenantUsetBusinessPerm::getUsetId, usetId).eq(SysTenantUsetBusinessPerm::getTenantId, tenantId));
    }

    @Override
    public BusinessPermScopeEnum getUsetBusiness(Set<String> usetIds, String permId, String tenantId) {
        BusinessPermScopeEnum permTypeEnum = BusinessPermScopeEnum.CURRENT_USER;
        if (Checker.beEmpty(usetIds) || Checker.beEmpty(usetIds) || Checker.beEmpty(permId)) {
            return permTypeEnum;
        }
        List<SysTenantUsetBusinessPerm> bps = this.baseMapper.selectList(lambdaQuery().in(SysTenantUsetBusinessPerm::getUsetId, usetIds)
                .eq(SysTenantUsetBusinessPerm::getPermId, permId).eq(SysTenantUsetBusinessPerm::getTenantId, tenantId));

        ImmutableMap<String, Integer> map = ImmutableMap.of(BusinessPermScopeEnum.CURRENT_USER.name(),
                1, BusinessPermScopeEnum.CUSTOMER_SPECIFIED.name(), 2, BusinessPermScopeEnum.SCOPE_ALL.name(), 3);

        for (SysTenantUsetBusinessPerm bp : bps) {
            if (Checker.beNotNull(bp.getPermStartTime()) && Checker.beNotNull(bp.getPermEndTime())) {
                Date now = new Date();
                boolean isOk = now.before(bp.getPermEndTime()) && now.after(bp.getPermStartTime());
                if (!isOk) {
                    continue;
                }
            }
            BusinessPermScopeEnum temp = BusinessPermScopeEnum.valueOf(bp.getPermScope());
            if (map.get(temp.name()) > map.get(permTypeEnum.name())) {
                permTypeEnum = temp;
            }
        }
        return permTypeEnum;
    }
}
