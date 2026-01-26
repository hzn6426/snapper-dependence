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

import com.baomibing.authority.dto.SysTenantPositionDto;
import com.baomibing.authority.dto.SysTenantUserPositionDto;
import com.baomibing.authority.entity.SysTenantPosition;
import com.baomibing.authority.entity.SysTenantUserPosition;
import com.baomibing.authority.mapper.SysTenantUserPositionMapper;
import com.baomibing.authority.service.SysTenantPositionService;
import com.baomibing.authority.service.SysTenantUserPositionService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SysTenantUserPositionServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysTenantUserPositionServiceImpl extends MBaseServiceImpl<SysTenantUserPositionMapper, SysTenantUserPosition, SysTenantUserPositionDto> implements SysTenantUserPositionService {

    @Autowired
    private SysTenantPositionService positionService;

    @Override
    public List<String> listPositionRoleIdsByUserAndGroup(String tenantId, String userId, String groupId) {
        if (Checker.beEmpty(userId) || Checker.beEmpty(groupId) || Checker.beEmpty(tenantId)) return Lists.newArrayList();
        return this.baseMapper.listPositionRoleIdsByUserAndGroup(tenantId, userId, groupId);
    }

    @Override
    public SysTenantPositionDto getPositionByUserAndGroup(String tenantId, String userId, String groupId) {
        if (Checker.beEmpty(userId) || Checker.beEmpty(groupId) || Checker.beEmpty(tenantId)) return null;
        List<SysTenantPosition> positions = this.baseMapper.listPositionByUserAndGroup(tenantId, userId, groupId);
        return Checker.beEmpty(positions) ? null : this.beanMapper.map(positions.get(0), SysTenantPositionDto.class);
    }

    @Override
    public List<SysTenantUserPositionDto> getPositionByUsersAndPosition(Set<String> users, String pid, String tenantId) {
        if (Checker.beEmpty(users) || Checker.beEmpty(pid) || Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }
        return mapper(baseMapper.selectList(lambdaQuery().eq(SysTenantUserPosition::getPositionId, pid).eq(SysTenantUserPosition::getTenantId, tenantId)
                .in(SysTenantUserPosition::getUserId, users)));
    }

    @Override
    public void removePositionUsers(String tenantId, String gid, Set<String> users) {
        Assert.CheckArgument(tenantId);
        Assert.CheckArgument(gid);
        Assert.CheckArgument(users);
        List<SysTenantPositionDto> positionDtos = positionService.listPositionByGroup(tenantId, gid);
        if (Checker.beNotNull(positionDtos)) {
            baseMapper.delete(lambdaQuery().in(SysTenantUserPosition::getPositionId, positionDtos.stream().map(SysTenantPositionDto::getId).collect(Collectors.toSet()))
                    .in(SysTenantUserPosition::getUserId, users).eq(SysTenantUserPosition::getTenantId, tenantId));
        }
    }

    @Override
    public void deleteByPositions(Set<String> pids, String tenantId) {
        Assert.CheckArgument(pids);
        Assert.CheckArgument(tenantId);
        baseMapper.delete(lambdaQuery().in(SysTenantUserPosition::getPositionId, pids).eq(SysTenantUserPosition::getTenantId, tenantId));
    }

    @Override
    public void deleteByUsers(Set<String> uids, String tenantId) {
        Assert.CheckArgument(uids);
        Assert.CheckArgument(tenantId);
        baseMapper.delete(lambdaQuery().in(SysTenantUserPosition::getUserId, uids).eq(SysTenantUserPosition::getTenantId, tenantId));
    }

    @Override
    public void deleteByGroupAndUsers(String tenantId, String gid, Set<String> users) {
        Assert.CheckArgument(gid);
        Assert.CheckArgument(users);
        this.baseMapper.delete(lambdaQuery().eq(SysTenantUserPosition::getGroupId, gid).in(SysTenantUserPosition::getUserId, users).eq(SysTenantUserPosition::getTenantId, tenantId));
    }

    @Override
    public void deleteByTenant(Set<String> tenantIds) {
        Assert.CheckArgument(tenantIds);
        baseMapper.delete(lambdaQuery().in(SysTenantUserPosition::getTenantId, tenantIds));
    }

    @Override
    public List<String> listUserByPosition(String positionId, String tenantId) {
        Assert.CheckArgument(positionId);
        Assert.CheckArgument(tenantId);
        List<SysTenantUserPosition> list = baseMapper.selectList(this.lambdaQuery().eq(SysTenantUserPosition::getPositionId, positionId).eq(SysTenantUserPosition::getTenantId, tenantId));
        if (Checker.beEmpty(list)){
            return new ArrayList<>();
        }
        return list.stream().map(SysTenantUserPosition::getUserId).collect(Collectors.toList());
    }
}
