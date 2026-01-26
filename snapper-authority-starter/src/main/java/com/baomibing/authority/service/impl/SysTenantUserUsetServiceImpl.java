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

import com.baomibing.authority.dto.SysTenantUserDto;
import com.baomibing.authority.dto.SysTenantUserGroupDto;
import com.baomibing.authority.dto.SysTenantUserUsetDto;
import com.baomibing.authority.dto.SysTenantUsetDto;
import com.baomibing.authority.entity.SysTenantUser;
import com.baomibing.authority.entity.SysTenantUserUset;
import com.baomibing.authority.entity.SysTenantUset;
import com.baomibing.authority.mapper.SysTenantUserUsetMapper;
import com.baomibing.authority.service.SysTenantUserUsetService;
import com.baomibing.authority.service.SysTenantUsetService;
import com.baomibing.core.common.Assert;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ObjectUtil;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SysTenantUserUsetServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysTenantUserUsetServiceImpl extends MBaseServiceImpl<SysTenantUserUsetMapper, SysTenantUserUset, SysTenantUserUsetDto> implements SysTenantUserUsetService {

    @Autowired
    private SysTenantUsetService usetService;
    @Override
    public void deleteByUsers(Set<String> userIds, String tenantId) {
        Assert.CheckArgument(userIds);
        Assert.CheckArgument(tenantId);
        this.baseMapper.delete(lambdaQuery().in(SysTenantUserUset::getUserId, userIds).eq(SysTenantUserUset::getTenantId, tenantId));
    }

    @Override
    public void deleteByUsets(Set<String> usetIds, String tenantId) {
        Assert.CheckArgument(usetIds);
        Assert.CheckArgument(tenantId);
        this.baseMapper.delete(lambdaQuery().in(SysTenantUserUset::getUsetId, usetIds).eq(SysTenantUserUset::getTenantId, tenantId));
    }

    @Override
    public void deleteByTenant(Set<String> tenantIds) {
        Assert.CheckArgument(tenantIds);
        baseMapper.delete(lambdaQuery().in(SysTenantUserUset::getTenantId, tenantIds));
    }

    @Override
    public List<SysTenantUserDto> listGroupUsersByUset(String tenantId, String usetId) {
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        if (Checker.beEmpty(usetId) || Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }
        List<SysTenantUser> userList = this.baseMapper.listGroupUsersByUset(tenantId, usetId);
        return Checker.beEmpty(userList) ? Lists.newArrayList() : Lists.newArrayList(this.collectionMapper.mapCollection(userList, SysTenantUserDto.class));
    }

    @Override
    public void saveFromUset(String tenantId, String usetId, List<SysTenantUserGroupDto> userGroups) {
        Assert.CheckArgument(usetId);
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        Assert.CheckArgument(tenantId);
        // 检查userId合法性
        Assert.CheckArgument(usetService.getIt(usetId), ExceptionEnum.OBJECT_IS_NULL, usetId);
        if (Checker.beEmpty(userGroups)) {
            this.baseMapper.delete(lambdaQuery().eq(SysTenantUserUset::getUsetId, usetId).eq(SysTenantUserUset::getTenantId, tenantId));
            return;
        }
        // 根据userGroups中的groupId分组 key为groupId, value为用户ID列表
        Map<String, Set<String>> map = userGroups.stream().collect(Collectors.groupingBy(ug -> ug.getGroupId(), Collectors.mapping(SysTenantUserGroupDto::getUserId, Collectors.toSet())));

        // 删除用户组组织对应的用户角色关系
        this.baseMapper.delete(lambdaQuery().eq(SysTenantUserUset::getUsetId, usetId).in(SysTenantUserUset::getOrgId, map.keySet()).eq(SysTenantUserUset::getTenantId, tenantId));
//		this.baseMapper.deleteByRoleAndGroups(roleId, map.keySet());
        final String tid = tenantId;
        List<SysTenantUserUsetDto> toAdds = Lists.newArrayList();
        map.forEach((k, v) -> {
            v.forEach(u -> toAdds.add(new SysTenantUserUsetDto().setUsetId(usetId).setOrgId(k).setUserId(u).setTenantId(tid)));
        });

        if (Checker.beNotEmpty(toAdds)) {
            super.saveItBatch(toAdds);
        }
    }

    @Override
    public List<SysTenantUsetDto> listUsetsByGroupAndUser(String tenantId, String orgId, String userId) {
        List<SysTenantUsetDto> list = new ArrayList<>();
        if (Checker.beEmpty(userId) || Checker.beEmpty(orgId) || Checker.beEmpty(tenantId)) {
            return list;
        }
        List<SysTenantUset> usetList = this.baseMapper.listUsetsByGroupAndUser(tenantId, orgId, userId);
        if (Checker.beEmpty(usetList)) {
            return list;
        }

        return Lists.newArrayList(this.collectionMapper.mapCollection(usetList, SysTenantUsetDto.class));
    }

    @Override
    public List<SysTenantUserUsetDto> listByGroupAndUsers(String tenantId, String orgId, Set<String> userIds) {
        List<SysTenantUserUsetDto> list = Lists.newArrayList();
        if (Checker.beEmpty(orgId) || Checker.beEmpty(userIds) || Checker.beEmpty(tenantId)) {
            return list;
        }
        return mapper(baseMapper.selectList(lambdaQuery().eq(SysTenantUserUset::getOrgId, orgId).in(SysTenantUserUset::getUserId, userIds).eq(SysTenantUserUset::getTenantId, tenantId)));
    }
}
