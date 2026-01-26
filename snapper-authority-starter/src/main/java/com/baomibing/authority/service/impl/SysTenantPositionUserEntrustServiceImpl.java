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

import com.baomibing.authority.dto.SysTenantPositionUserEntrustDto;
import com.baomibing.authority.dto.SysTenantUserDto;
import com.baomibing.authority.entity.SysTenantPositionUserEntrust;
import com.baomibing.authority.entity.SysTenantUser;
import com.baomibing.authority.mapper.SysTenantPositionUserEntrustMapper;
import com.baomibing.authority.service.SysTenantPositionUserEntrustService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SysTenantPositionUserEntrustServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysTenantPositionUserEntrustServiceImpl extends MBaseServiceImpl<SysTenantPositionUserEntrustMapper, SysTenantPositionUserEntrust, SysTenantPositionUserEntrustDto> implements SysTenantPositionUserEntrustService {

    @Override
    public List<String> listEntrustUserCodesByPosition(String tenantId, String positionId) {
        if (Checker.beEmpty(tenantId) || Checker.beEmpty(positionId)) {
            return Lists.newArrayList();
        }
        List<SysTenantUserDto> users = listEntrustUsersByPosition(tenantId, positionId);
        List<String> list = Checker.beEmpty(users) ? Lists.newArrayList()
                : users.stream().map(SysTenantUserDto::getUserName).collect(Collectors.toList());
        return list;
    }

    @Override
    public List<SysTenantUserDto> listEntrustUsersByPosition(String tenantId, String positionId) {
        if (Checker.beEmpty(positionId) || Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }
        List<SysTenantUser> users = this.baseMapper.listEntrustUsersByPosition(tenantId, positionId);
        return Checker.beEmpty(users) ? Lists.newArrayList()
                : new ArrayList<>(this.collectionMapper.mapCollection(users, SysTenantUserDto.class));
    }

    @Override
    public void deleteByUsers(Set<String> uids, String tenantId) {
        Assert.CheckArgument(uids);
        Assert.CheckArgument(tenantId);
        this.baseMapper.delete(lambdaQuery().in(SysTenantPositionUserEntrust::getUserId, uids).eq(SysTenantPositionUserEntrust::getTenantId, tenantId));
    }

    @Override
    public void deleteByPosition(String tenantId, String positionId) {
        Assert.CheckArgument(positionId);
        Assert.CheckArgument(tenantId);
        this.baseMapper.delete(lambdaQuery().eq(SysTenantPositionUserEntrust::getPositionId, positionId).eq(SysTenantPositionUserEntrust::getTenantId, tenantId));
    }

    @Override
    public void deleteByPositions(String tenantId, Set<String> positionIds) {
        Assert.CheckArgument(positionIds);
        Assert.CheckArgument(tenantId);
        this.baseMapper.delete(lambdaQuery().in(SysTenantPositionUserEntrust::getPositionId, positionIds).eq(SysTenantPositionUserEntrust::getTenantId, tenantId));
    }
}
