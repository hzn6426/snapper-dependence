
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
import com.baomibing.authority.entity.SysUserBusinessPerm;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.mapper.SysUserBusinessPermMapper;
import com.baomibing.authority.service.*;
import com.baomibing.core.annotation.NotAuthAop;
import com.baomibing.core.common.Assert;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户业务权限服务实现类
 *
 * @author zening
 * @since 1.0.0
 */
@Service
public class SysUserBusinessPermServiceImpl extends MBaseServiceImpl<SysUserBusinessPermMapper,
    SysUserBusinessPerm, UserBusinessPermDto> implements SysUserBusinessPermService {
    
    @Autowired private SysBusinessPermService businessPermService;
    @Autowired private SysUserEntrustService userEntrustService;
    @Autowired private SysGroupEntrustService groupEntrustServie;
    @Autowired private SysGroupExceptEntrustService groupExceptEntrustService;
    @Autowired private SysUserExceptEntrustService userExceptEntrustService;
    
    
    @NotAuthAop
    @Override
    public BusinessPermScopeEnum getUserBusiness(String orgId, String userId, String permId) {
        BusinessPermScopeEnum permTypeEnum = BusinessPermScopeEnum.CURRENT_USER;
        if (Checker.beEmpty(userId) || Checker.beEmpty(permId)) {
            return permTypeEnum;
        }
        LambdaQueryWrapper<SysUserBusinessPerm> wrapper = lambdaQuery();
//        QueryWrapper<SysUserBusinessPerm> wrapper = new QueryWrapper<>();
        wrapper.eq(SysUserBusinessPerm::getUserId, userId).eq(SysUserBusinessPerm::getPermId, permId).eq(SysUserBusinessPerm::getOrgId, orgId);
        SysUserBusinessPerm bp = this.baseMapper.selectOne(wrapper);
        if (Checker.beNull(bp))
            return permTypeEnum;
        // 用户权限须在时间段内有效
        if (Checker.beNotNull(bp.getPermStartTime()) && Checker.beNotNull(bp.getPermEndTime())) {
            Date now = new Date();
            boolean isOk = now.before(bp.getPermEndTime()) && now.after(bp.getPermStartTime());
            if (!isOk) {
                return permTypeEnum;
            }
        }
        permTypeEnum = EnumUtils.getEnum(BusinessPermScopeEnum.class, bp.getPermScope());
        return Checker.beNull(permTypeEnum) ? BusinessPermScopeEnum.CURRENT_USER : permTypeEnum;
    }
    
    
    @Transactional
    @Override
    public void saveUserBusinessPerm(UserBusinessPermDto v) {
        Assert.CheckArgument(v);
        BusinessPermDto bp = businessPermService.getIt(v.getPermId());
        if (Checker.beNull(bp)) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.CANNOT_FIND_THE_ID_OF_BUSINESS_PERM, v.getPermId());
        }
        BusinessPermScopeEnum scope;
        if (Checker.beEmpty(v.getPermScope()) || Checker.beNull(scope = BusinessPermScopeEnum.valueOf(v.getPermScope()))) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.INVALID_BUSINESS_PERM_SCOPE, v.getPermScope());
        }
        
        //删除委托信息
        userEntrustService.deleteByGroupAndUserAndPerm(v.getOrgId(), v.getUserId(), v.getPermId());
        groupEntrustServie.deleteByGroupAndUserAndPerm(v.getOrgId(), v.getUserId(), v.getPermId());

        userExceptEntrustService.deleteByGroupAndUserAndPerm(v.getOrgId(), v.getUserId(), v.getPermId());
        groupExceptEntrustService.deleteByGroupAndUserAndPerm(v.getOrgId(), v.getUserId(), v.getPermId());

        List<GroupEntrustDto> groupEntrustList = Lists.newArrayList();
        List<UserEntrustDto> userEntrustList = Lists.newArrayList();

        List<GroupExceptEntrustDto> exceptGroupEntrustList = Lists.newArrayList();
        List<UserExceptEntrustDto> exceptUserEntrustList = Lists.newArrayList();
        //保存可视范围的委托信息
        switch (scope) {
            case CUSTOMER_SPECIFIED:
                List<String> groupEntrusts = v.getGroupEntrusts();
                List<String> userEntrusts = v.getUserEntrusts();

                List<String> exceptGroupEntrusts = v.getExceptGroupEntrusts();
                List<String> exceptUserEntrusts = v.getExceptUserEntrusts();
                groupEntrusts.forEach(id ->
                    groupEntrustList.add(new GroupEntrustDto().setPermId(bp.getId()).setGroupEntrustId(id)
                        .setUserId(v.getUserId()).setOrgId(v.getOrgId())));
                
                userEntrusts.forEach(id ->
                    userEntrustList.add(new UserEntrustDto().setPermId(bp.getId()).setUserEntrustId(id)
                        .setUserId(v.getUserId()).setOrgId(v.getOrgId())));

                exceptUserEntrusts.forEach(id ->
                        exceptGroupEntrustList.add(new GroupExceptEntrustDto().setPermId(bp.getId()).setGroupExceptEntrustId(id)
                                .setUserId(v.getUserId()).setOrgId(v.getOrgId())));

                exceptUserEntrusts.forEach(id ->
                        exceptUserEntrustList.add(new UserExceptEntrustDto().setPermId(bp.getId()).setUserExceptEntrustId(id)
                                .setUserId(v.getUserId()).setOrgId(v.getOrgId())));
                break;
            case CURRENT_USER:
                this.deleteGroupUserPerm(v.getOrgId(), v.getUserId(), v.getPermId());
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
        this.deleteGroupUserPerm(v.getOrgId(), v.getUserId(), v.getPermId());
        super.saveIt(v);
    }
    
    @Override
    public List<String> listUserEntrustIdsByPerm(String orgId, String userId, String permId) {
        BusinessPermDto bp = businessPermService.getIt(permId);
        if (Checker.beNull(bp)) {
            return Lists.newArrayList();
        }
        
        List<String> list = userEntrustService.listEntrustUserIdsByGroupAndUserAndPerm(orgId, userId, permId);
        return Checker.beEmpty(list) ? Lists.newArrayList() : list;
    }

    @Override
    public List<String> listUserExceptEntrustIdsByPerm(String orgId, String userId, String permId) {
        BusinessPermDto bp = businessPermService.getIt(permId);
        if (Checker.beNull(bp)) {
            return Lists.newArrayList();
        }

        List<String> list = userExceptEntrustService.listEntrustUserIdsByGroupAndUserAndPerm(orgId, userId, permId);
        return Checker.beEmpty(list) ? Lists.newArrayList() : list;
    }
    
    @Override
    public List<String> listGroupEntrustIdsByPerm(String orgId, String userId, String permId) {
        BusinessPermDto bp = businessPermService.getIt(permId);
        if (Checker.beNull(bp)) {
            return Lists.newArrayList();
        }
        List<GroupDto> list = groupEntrustServie.listEntrustGroupsByGroupAndUserAndPerm(orgId, userId, permId);
        if (Checker.beEmpty(list))
            return Lists.newArrayList();
        return list.stream().map(GroupDto::getId).collect(Collectors.toList());
    }

    @Override
    public List<String> listGroupExceptEntrustIdsByPerm(String orgId, String userId, String permId) {
        BusinessPermDto bp = businessPermService.getIt(permId);
        if (Checker.beNull(bp)) {
            return Lists.newArrayList();
        }
        List<GroupDto> list = groupExceptEntrustService.listEntrustGroupsByGroupAndUserAndPerm(orgId, userId, permId);
        if (Checker.beEmpty(list)) return Lists.newArrayList();
        return list.stream().map(GroupDto::getId).collect(Collectors.toList());
    }
    
    @Transactional
    @Override
    public void deleteByGroupAndUsers(String orgId, Set<String> uids) {
        Assert.CheckArgument(uids);
        Assert.CheckArgument(orgId);
        this.baseMapper.delete(lambdaQuery().eq(SysUserBusinessPerm::getOrgId, orgId).in(SysUserBusinessPerm::getUserId, uids));
    }
    
    @Override
    public void deleteByUsers(Set<String> uids) {
        Assert.CheckArgument(uids);
        this.baseMapper.delete(lambdaQuery().in(SysUserBusinessPerm::getUserId, uids));
    }
    
    @Transactional
    @Override
    public void deleteGroupUserPerm(String orgId, String userId, String permId) {
        Assert.CheckArgument(userId);
        Assert.CheckArgument(permId);
        Assert.CheckArgument(orgId);
        this.baseMapper.delete(lambdaQuery().eq(SysUserBusinessPerm::getOrgId, orgId).eq(SysUserBusinessPerm::getUserId, userId)
            .eq(SysUserBusinessPerm::getPermId, permId));
    }

    @Override
    public List<UserBusinessPermDto> listUserBusinessPerms(Set<String> userIds, String orgId) {
        if (Checker.beEmpty(orgId) || Checker.beEmpty(userIds)) {
            return Lists.newArrayList();
        }
        return mapper(baseMapper.selectList(lambdaQuery().eq(SysUserBusinessPerm::getOrgId, orgId).in(SysUserBusinessPerm::getUserId, userIds)));
    }

    @Override
    public void doCopyUserBusinessPerm(String uid, String gid, String toUserId) {
        Assert.CheckArgument(uid, gid, toUserId);
        List<UserBusinessPermDto> userBusinessPerms = this.listUserBusinessPerms(Sets.newHashSet(uid), gid);
        if (Checker.beEmpty(userBusinessPerms)) {
            return;
        }

        List<UserBusinessPermDto> toUserBusinessPerms = this.listUserBusinessPerms(Sets.newHashSet(toUserId), gid);
        Set<String> permIds = toUserBusinessPerms.stream().map(UserBusinessPermDto::getPermId).collect(Collectors.toSet());
        List<UserBusinessPermDto> newUserBusinessPerms = Lists.newArrayList();

        userBusinessPerms.forEach(userBusinessPerm -> {
            //忽略已有的数据的权限
            if (!permIds.contains(userBusinessPerm.getPermId())) {
                newUserBusinessPerms.add(userBusinessPerm.setUserId(toUserId).setId(null));
            }
        });

        if (Checker.beNotEmpty(newUserBusinessPerms)) {
            super.saveItBatch(newUserBusinessPerms);
        }
    }
}
