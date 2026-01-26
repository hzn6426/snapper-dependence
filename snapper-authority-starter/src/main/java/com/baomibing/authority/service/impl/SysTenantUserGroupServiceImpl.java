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

import com.baomibing.authority.dto.HmacUserDto;
import com.baomibing.authority.dto.SysTenantUserGroupDto;
import com.baomibing.authority.entity.SysTenantUserGroup;
import com.baomibing.authority.mapper.SysTenantUserGroupMapper;
import com.baomibing.authority.service.*;
import com.baomibing.cache.CacheService;
import com.baomibing.core.common.Assert;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ObjectUtil;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * SysTenantUserGroupServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysTenantUserGroupServiceImpl extends MBaseServiceImpl<SysTenantUserGroupMapper, SysTenantUserGroup, SysTenantUserGroupDto> implements SysTenantUserGroupService {

    @Autowired private SysTenantUserRoleService userRoleService;
    @Autowired private SysTenantUserPositionService userPositionService;
    @Autowired private SysTenantUserService userService;
    @Autowired private CacheService cacheService;
    @Autowired private SysTenantGroupService groupService;
    @Autowired private SysHmacUserService hmacUserService;

    @Override
    public List<SysTenantUserGroupDto> listByUser(String tenantId, String userId) {
        if (Checker.beEmpty(userId) || Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }
        return mapper(this.baseMapper.listByUser(tenantId, userId));
    }

    @Override
    public List<SysTenantUserGroupDto> listItAndChildByGroup(String tenantId, String groupId) {
        if (Checker.beEmpty(groupId) || Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }
        return mapper(this.baseMapper.listItAndChilldByGroup(tenantId, groupId));
    }

//    @Override
//    public void changeUser2Group(String tenantId, String userId, String groupId) {
//        Assert.CheckArgument(userId);
//        Assert.CheckArgument(groupId);
//        Assert.CheckArgument(tenantId);
//        SysTenantUserDto u = userService.getIt(userId);
//        if (Checker.beNull(u) ) {
//            throw new ServerRuntimeException(AuthorizationExceptionEnum.ID_OF_USER_NOT_EXIST, userId);
//        }
//        List<SysTenantUserGroupDto> ugs = listByUser(tenantId, userId);
//        boolean match = ugs.stream().anyMatch(g -> g.getGroupId().equals(groupId));
//        if (!match) {
//            throw new ServerRuntimeException(AuthorizationExceptionEnum.INVALID_GROUP_ID_FOR_USER_CHANGE);
//        }
//        //获取权限
//        Collection<GrantedAuthority> authorities = Sets.newHashSet();
//        List<SysTenantRoleDto> roles = userRoleService.listRolesByGroupAndUser(tenantId, groupId, userId);
//        List<String> roleIds = roles.stream().map(SysTenantRoleDto::getId).collect(Collectors.toList());
//        List<String> positionRoleIds = userPositionService.listPositionRoleIdsByUserAndGroup(tenantId, userId, groupId);
//        roleIds.addAll(positionRoleIds);
////		List<ResourceApiDto> apiAuthoritiesList = roleResourceService.listResourceApiByRoles(roleIds);
////		List<UserResourceApi> seriableAuthorities = Lists.newArrayList(collectionMapper.mapCollection(apiAuthoritiesList, UserResourceApi.class));
//        roleIds.forEach(r -> authorities.add(new SimpleGrantedAuthority((r))));
//        List<String> authorizations = Lists.newArrayList();
//        authorities.forEach(a -> authorizations.add(a.getAuthority()));
//        SysTenantGroupDto company = groupService.getParentCompanyById(tenantId, groupId);
//        SysTenantGroupDto group = groupService.getIt(tenantId, groupId);
//        //设置缓存
//        Map<String, String> cacheMap = Maps.newHashMap();
//        cacheMap.put(TenantRedisKeyConstant.KEY_USER_GROUP_ID, groupId);
//        cacheMap.put(TenantRedisKeyConstant.KEY_USER_DEPARTMENT,
//                (Checker.beNotNull(company) ? company.getGroupName() + " - " : "") + group.getGroupName());
//        cacheMap.put(TenantRedisKeyConstant.KEY_USER_SECURITY_AUTHORITY, JSONArray.toJSONString(authorizations));
//        cacheMap.put(TenantRedisKeyConstant.KEY_USER_ROLE_ID, Joiner.on(Strings.COMMA).join(authorizations));
//        cacheMap.put(TenantRedisKeyConstant.KEY_USER_COMPANY, Checker.beNotNull(company) ? company.getId() : "");
//        //获取用户职位
//        SysTenantPositionDto position = userPositionService.getPositionByUserAndGroup(tenantId, userId, groupId);
//        cacheMap.put(TenantRedisKeyConstant.KEY_USER_POSITION_ID, Checker.beNotNull(position) ? position.getId() : "");
//        cacheService.hSetAll(MessageFormat.format(TenantRedisKeyConstant.KEY_USER_CONTEXT, u.getUserName()), cacheMap, 0);
//    }

    @Override
    public List<SysTenantUserGroupDto> listByUsersAndGroup(Set<String> users, String gid, String tenantId) {
        if (Checker.beEmpty(users) || Checker.beEmpty(gid) || Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }
        return mapper(baseMapper.selectList(lambdaQuery().eq(SysTenantUserGroup::getGroupId, gid)
                .in(SysTenantUserGroup::getUserId, users).eq(SysTenantUserGroup::getTenantId, tenantId)));
    }

    @Override
    public void deleteByGroupIdAndUsers(Set<String> users, String gid, String tenantId) {
        Assert.CheckArgument(users);
        Assert.CheckArgument(gid);
        Assert.CheckArgument(tenantId);
        baseMapper.delete(lambdaQuery().eq(SysTenantUserGroup::getGroupId, gid)
                .in(SysTenantUserGroup::getUserId, users).eq(SysTenantUserGroup::getTenantId, tenantId));
    }

    @Override
    public void deleteByGroups(Set<String> gids, String tenantId) {
        Assert.CheckArgument(gids);
        Assert.CheckArgument(tenantId);
        baseMapper.delete(lambdaQuery().in(SysTenantUserGroup::getGroupId, gids).eq(SysTenantUserGroup::getTenantId, tenantId));
    }

    @Override
    public void deleteByUsers(Set<String> uids, String tenantId) {
        Assert.CheckArgument(uids);
        Assert.CheckArgument(tenantId);
        baseMapper.delete(lambdaQuery().in(SysTenantUserGroup::getUserId, uids).eq(SysTenantUserGroup::getTenantId, tenantId));
    }

    @Override
    public void deleteByTenant(Set<String> tenantIds) {
        Assert.CheckArgument(tenantIds);
        baseMapper.delete(lambdaQuery().in(SysTenantUserGroup::getTenantId, tenantIds));
    }

    @Override
    public String getGroupIdByUserNo(String userNo, String tenantId) {
        if (Checker.beEmpty(userNo) || Checker.beEmpty(tenantId)) {
            return Strings.EMPTY;
        }
        List<String> strings = super.baseMapper.listGroupIdByUserNo(tenantId, userNo);
        if (Checker.beEmpty(strings)){
            return Strings.EMPTY;
        }
        return strings.get(0);
    }

    @Override
    public void doBindHmacUser(String tenantId, String tenantUserId, String tenantUserOrgId, String hmacUserId) {
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        HmacUserDto hmacUser = hmacUserService.getIt(hmacUserId);
        if (Checker.beNull(hmacUser)) {
            throw new ServerRuntimeException(ExceptionEnum.CAN_NOT_FIND_ID_OF_HMAC_USER, hmacUserId);
        }
        SysTenantUserGroup tug = baseMapper.selectOne(lambdaQuery().eq(SysTenantUserGroup::getUserId, tenantUserId).eq(SysTenantUserGroup::getTenantId, tenantId).eq(SysTenantUserGroup::getGroupId, hmacUser.getGroupId()));
        if (Checker.beNull(tug)) {
            throw new ServerRuntimeException(ExceptionEnum.CAN_NOT_FIND_ID_OF_TENANT_USER_IN_THE_GROUP, tenantUserOrgId, tenantUserId);
        }
        hmacUser.setUserId(tenantUserId).setOrgId(tenantUserOrgId).setTenantId(tenantId);
        hmacUserService.updateIt(hmacUser);

    }
}
