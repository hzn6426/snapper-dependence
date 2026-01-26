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

import com.baomibing.authority.dto.SysTenantGroupDto;
import com.baomibing.authority.dto.SysTenantPositionGroupEntrustDto;
import com.baomibing.authority.entity.SysTenantGroup;
import com.baomibing.authority.entity.SysTenantPositionGroupEntrust;
import com.baomibing.authority.mapper.SysTenantPositionGroupEntrustMapper;
import com.baomibing.authority.service.SysTenantPositionGroupEntrustService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * SysTenantPositionGroupEntrustServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysTenantPositionGroupEntrustServiceImpl extends MBaseServiceImpl<SysTenantPositionGroupEntrustMapper, SysTenantPositionGroupEntrust, SysTenantPositionGroupEntrustDto> implements SysTenantPositionGroupEntrustService {

    @Override
    public List<SysTenantGroupDto> listEntrustGroupsByPosition(String tenantId, String positionId) {
        if (Checker.beEmpty(positionId) || Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }
        List<SysTenantGroup> groups = this.baseMapper.listEntrustGroupsByPosition(tenantId, positionId);
        return Checker.beEmpty(groups) ? Lists.newArrayList() : Lists.newArrayList(this.collectionMapper.mapCollection(groups, SysTenantGroupDto.class));
    }

    @Override
    public void deleteByPosition(String tenantId, String positionId) {
        Assert.CheckArgument(positionId);
        Assert.CheckArgument(tenantId);
        this.baseMapper.delete(lambdaQuery().eq(SysTenantPositionGroupEntrust::getPositionId, positionId).eq(SysTenantPositionGroupEntrust::getTenantId, tenantId));
    }

    @Override
    public void deleteByPositions(String tenantId, Set<String> positionIds) {
        Assert.CheckArgument(positionIds);
        Assert.CheckArgument(tenantId);
        this.baseMapper.delete(lambdaQuery().in(SysTenantPositionGroupEntrust::getPositionId, positionIds).eq(SysTenantPositionGroupEntrust::getTenantId, tenantId));
    }
}
