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

import com.baomibing.authority.dto.SysTenantPositionRoleDto;
import com.baomibing.authority.dto.SysTenantRoleDto;
import com.baomibing.authority.entity.SysTenantPositionRole;
import com.baomibing.authority.entity.SysTenantRole;
import com.baomibing.authority.mapper.SysTenantPositionRoleMapper;
import com.baomibing.authority.service.SysTenantPositionRoleService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ObjectUtil;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * SysTenantPositionRoleServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysTenantPositionRoleServiceImpl extends MBaseServiceImpl<SysTenantPositionRoleMapper, SysTenantPositionRole, SysTenantPositionRoleDto> implements SysTenantPositionRoleService {

    @Override
    public List<SysTenantRoleDto> listRolesByPosition(String tenantId, String positionId) {
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        if (Checker.beEmpty(positionId) || Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }
        List<SysTenantRole> roles = this.baseMapper.listRolesByPosition(tenantId, positionId);
        return Checker.beEmpty(roles) ? Lists.newArrayList()
                : Lists.newArrayList(this.collectionMapper.mapCollection(roles, SysTenantRoleDto.class));
    }

    @Override
    public void savePositionRoles(String tenantId, String positionId, Set<String> roles) {
        Assert.CheckArgument(positionId);
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        Assert.CheckArgument(tenantId);
//        Assert.CheckArgument(roles);
        final String tid = tenantId;
        deleteByPosition(tenantId, positionId);
        List<SysTenantPositionRoleDto> prs = Lists.newArrayList();
        roles.forEach(r -> prs.add(new SysTenantPositionRoleDto().setRoleId(r).setPositionId(positionId).setTenantId(tid)));
        if (Checker.beNotEmpty(prs)) {
            super.saveItBatch(prs);
        }
    }

    @Override
    public void deleteByPosition(String tenantId, String positionId) {
        Assert.CheckArgument(positionId);
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        Assert.CheckArgument(tenantId);
        this.baseMapper.delete(lambdaQuery().eq(SysTenantPositionRole::getPositionId, positionId).eq(SysTenantPositionRole::getTenantId, tenantId));
    }

    @Override
    public void deleteByPositions(String tenantId, Set<String> positionIds) {
        Assert.CheckArgument(positionIds);
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        Assert.CheckArgument(tenantId);
        this.baseMapper.delete(lambdaQuery().in(SysTenantPositionRole::getPositionId, positionIds).eq(SysTenantPositionRole::getTenantId, tenantId));
    }

    @Override
    public void deleteByRoles(Set<String> roles, String tenantId) {
        Assert.CheckArgument(roles);
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        Assert.CheckArgument(tenantId);
        this.baseMapper.delete(lambdaQuery().in(SysTenantPositionRole::getRoleId, roles).eq(SysTenantPositionRole::getTenantId, tenantId));
    }

    @Override
    public void deleteByTenant(Set<String> tenantIds) {
        Assert.CheckArgument(tenantIds);
        this.baseMapper.delete(lambdaQuery().in(SysTenantPositionRole::getTenantId, tenantIds));
    }
}
