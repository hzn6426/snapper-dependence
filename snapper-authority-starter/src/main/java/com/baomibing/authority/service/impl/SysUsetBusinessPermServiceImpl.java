
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
import com.baomibing.authority.entity.SysUsetBusinessPerm;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.mapper.SysUsetBusinessPermMapper;
import com.baomibing.authority.service.*;
import com.baomibing.core.common.Assert;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
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
 * SysUsetBusinessPermServiceImpl
 *
 * @author zening
 * @version 1.0.0
 */
@Service
public class SysUsetBusinessPermServiceImpl extends MBaseServiceImpl<SysUsetBusinessPermMapper, SysUsetBusinessPerm, UsetBusinessPermDto> implements SysUsetBusinessPermService {

    @Autowired private SysBusinessPermService businessPermService;
    @Autowired private SysUsetUserEntrustService usetUserEntrustService;
    @Autowired private SysUsetGroupEntrustService usetGroupEntrustService;
    @Autowired private SysUserUsetService userUsetService;
    @Autowired private SysUsetUserExceptEntrustService usetUserExceptEntrustService;
    @Autowired private SysUsetGroupExceptEntrustService usetGroupExceptEntrustService;

    @Override
    public void saveUserBusinessPerm(UsetBusinessPermDto v) {
        Assert.CheckArgument(v);
        BusinessPermDto bp = businessPermService.getIt(v.getPermId());
        if (Checker.beNull(bp)) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.CANNOT_FIND_THE_ID_OF_BUSINESS_PERM, v.getPermId());
        }
        BusinessPermScopeEnum scope = null;
        if (Checker.beEmpty(v.getPermScope()) || Checker.beNull(scope = EnumUtils.getEnum(BusinessPermScopeEnum.class, v.getPermScope()))) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.INVALID_BUSINESS_PERM_SCOPE, v.getPermScope());
        }

        //删除委托信息
        usetUserEntrustService.deleteByUsetAndPerm(v.getUsetId(), v.getPermId());
        usetGroupEntrustService.deleteByUsetAndPerm(v.getUsetId(), v.getPermId());
        usetUserExceptEntrustService.deleteByUsetAndPerm(v.getUsetId(), v.getPermId());
        usetGroupExceptEntrustService.deleteByUsetAndPerm(v.getUsetId(), v.getPermId());
        List<UsetGroupEntrustDto> groupEntrustList = Lists.newArrayList();
        List<UsetUserEntrustDto> userEntrustList = Lists.newArrayList();
        List<UsetGroupExceptEntrustDto> exceptGroupEntrustList = Lists.newArrayList();
        List<UsetUserExceptEntrustDto> exceptUserEntrustList = Lists.newArrayList();
        //保存可视范围的委托信息
        switch (scope) {
            case CUSTOMER_SPECIFIED:
                List<String> groupEntrusts = v.getGroupEntrusts();
                List<String> userEntrusts = v.getUserEntrusts();

                List<String> exceptGroupEntrusts = v.getExceptGroupEntrusts();
                List<String> exceptUserEntrusts = v.getExceptUserEntrusts();
//                if (Checker.BeEmpty(groupEntrusts) || Checker.BeEmpty(userEntrusts)) break;
                groupEntrusts.forEach(id ->
                        groupEntrustList.add(new UsetGroupEntrustDto().setPermId(bp.getId()).setGroupEntrustId(id)
                                .setUsetId(v.getUsetId())));

                userEntrusts.forEach(id ->
                        userEntrustList.add(new UsetUserEntrustDto().setPermId(bp.getId()).setUserEntrustId(id)
                                .setUsetId(v.getUsetId())));

                exceptGroupEntrusts.forEach(id ->
                        exceptGroupEntrustList.add(new UsetGroupExceptEntrustDto().setPermId(bp.getId()).setGroupExceptEntrustId(id)
                                .setUsetId(v.getUsetId())));

                exceptUserEntrusts.forEach(id ->
                        exceptUserEntrustList.add(new UsetUserExceptEntrustDto().setPermId(bp.getId()).setUserExceptEntrustId(id)
                                .setUsetId(v.getUsetId())));

                break;
            case CURRENT_USER:
                this.deleteUsetPerm(v.getUsetId(), v.getPermId());
                return;
            default:
                break;
        }

        if (Checker.beNotEmpty(groupEntrustList)) {
            usetGroupEntrustService.saveItBatch(groupEntrustList);
        }
//        for (GroupEntrustDto ge : groupEntrustList) {
//            groupEntrustServie.saveIt(ge);
//        }
        if (Checker.beNotEmpty(userEntrustList)) {
            usetUserEntrustService.saveItBatch(userEntrustList);
        }

        if (Checker.beNotEmpty(exceptGroupEntrustList)) {
            usetGroupExceptEntrustService.saveItBatch(exceptGroupEntrustList);
        }

        if (Checker.beNotEmpty(exceptUserEntrustList)) {
            usetUserExceptEntrustService.saveItBatch(exceptUserEntrustList);
        }
//        for (UserEntrustDto ue : userEntrustList) {
//            userEntrustService.saveIt(ue);
//        }
        this.deleteUsetPerm(v.getUsetId(), v.getPermId());
        super.saveIt(v);
    }

    @Override
    public List<String> listUserEntrustIdsByPerm(String usetId, String permId) {
        if (Checker.beEmpty(usetId) || Checker.beEmpty(permId)) {
            return Lists.newArrayList();
        }
        return usetUserEntrustService.listEntrustUserCodesByUsetAndPerm(Sets.newHashSet(usetId), permId);

    }

    @Override
    public List<String> listUserExceptEntrustIdsByPerm(String usetId, String permId) {
        if (Checker.beEmpty(usetId) || Checker.beEmpty(permId)) {
            return Lists.newArrayList();
        }
        return usetUserExceptEntrustService.listEntrustUserIdsByUsetAndPerm(Sets.newHashSet(usetId), permId);
    }

    @Override
    public List<String> listGroupEntrustIdsByPerm(String usetId, String permId) {
        if (Checker.beEmpty(usetId) || Checker.beEmpty(permId)) {
            return Lists.newArrayList();
        }
        List<GroupDto> list = usetGroupEntrustService.listEntrustGroupsByUsetAndPerm(Sets.newHashSet(usetId), permId);
        if (Checker.beEmpty(list)) return Lists.newArrayList();
        return list.stream().map(g -> g.getId()).collect(Collectors.toList());
    }

    @Override
    public List<String> listGroupExceptEntrustIdsByPerm(String usetId, String permId) {
        if (Checker.beEmpty(usetId) || Checker.beEmpty(permId)) {
            return Lists.newArrayList();
        }
        List<GroupDto> list = usetGroupExceptEntrustService.listEntrustGroupsByUsetAndPerm(Sets.newHashSet(usetId), permId);
        if (Checker.beEmpty(list)) return Lists.newArrayList();
        return list.stream().map(GroupDto::getId).collect(Collectors.toList());
    }

    @Override
    public void deleteUsetPerm(String usetId, String permId) {
        Assert.CheckArgument(usetId);
        Assert.CheckArgument(permId);
        this.baseMapper.delete(lambdaQuery().eq(SysUsetBusinessPerm::getPermId, permId).eq(SysUsetBusinessPerm::getUsetId, usetId));
    }

    @Override
    public BusinessPermScopeEnum getUsetBusiness(Set<String> usetIds, String permId) {
        BusinessPermScopeEnum permTypeEnum = BusinessPermScopeEnum.CURRENT_USER;
        if (Checker.beEmpty(usetIds) || Checker.beEmpty(usetIds) || Checker.beEmpty(permId)) {
            return permTypeEnum;
        }
        List<SysUsetBusinessPerm> bps = this.baseMapper.selectList(lambdaQuery().in(SysUsetBusinessPerm::getUsetId, usetIds)
                .eq(SysUsetBusinessPerm::getPermId, permId));

        ImmutableMap<String, Integer> map = ImmutableMap.of(BusinessPermScopeEnum.CURRENT_USER.name(),
                1, BusinessPermScopeEnum.CUSTOMER_SPECIFIED.name(), 2, BusinessPermScopeEnum.SCOPE_ALL.name(), 3);

        for (SysUsetBusinessPerm bp : bps) {
            if (Checker.beNotNull(bp.getPermStartTime()) && Checker.beNotNull(bp.getPermEndTime())) {
                Date now = new Date();
                boolean isOk = now.before(bp.getPermEndTime()) && now.after(bp.getPermStartTime());
                if (!isOk) {
                    continue;
                }
            }
            BusinessPermScopeEnum temp = EnumUtils.getEnum(BusinessPermScopeEnum.class, bp.getPermScope());
            if (Checker.beNotNull(map.get(temp.name())) && Checker.beNotNull(map.get(permTypeEnum.name())) && map.get(temp.name()) > map.get(permTypeEnum.name())) {
                permTypeEnum = temp;
            }
        }
        return permTypeEnum;
    }
}
