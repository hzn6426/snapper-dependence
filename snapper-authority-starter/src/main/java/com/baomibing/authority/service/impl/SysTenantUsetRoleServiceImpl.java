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

import com.baomibing.authority.dto.SysTenantUsetRoleDto;
import com.baomibing.authority.entity.SysTenantUsetRole;
import com.baomibing.authority.mapper.SysTenantUsetRoleMapper;
import com.baomibing.authority.service.SysTenantUsetRoleService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ObjectUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SysTenantUsetRoleServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysTenantUsetRoleServiceImpl extends MBaseServiceImpl<SysTenantUsetRoleMapper, SysTenantUsetRole, SysTenantUsetRoleDto> implements SysTenantUsetRoleService {

    @Override
    public void saveFromUset(String tenantId, String usetId, Set<String> roleIds) {
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        Assert.CheckArgument(tenantId);
        Assert.CheckArgument(usetId);
        //删除于当前用户组关联的所有角色
        this.baseMapper.delete(lambdaQuery().eq(SysTenantUsetRole::getUsetId, usetId).eq(SysTenantUsetRole::getTenantId, tenantId));
        List<SysTenantUsetRoleDto> urs = Lists.newArrayList();
        final String tid = tenantId;
        roleIds.forEach(r -> urs.add(new SysTenantUsetRoleDto().setUsetId(usetId).setRoleId(r).setTenantId(tid)));
        //添加用户组角色关联
        if (Checker.beNotEmpty(urs)) {
            super.saveItBatch(urs);
        }
    }

    @Override
    public Set<String> listRolesByUset(String tenantId, String usetId) {
        if (Checker.beEmpty(usetId) || Checker.beEmpty(tenantId)) {
            return Sets.newHashSet();
        }
        List<SysTenantUsetRole> urs = baseMapper.selectList(lambdaQuery().eq(SysTenantUsetRole::getUsetId, usetId).eq(SysTenantUsetRole::getTenantId, tenantId));
        return urs.stream().map(SysTenantUsetRole::getRoleId).collect(Collectors.toSet());
    }

    @Override
    public void deleteByUsets(Set<String> usetIds, String tenantId) {
        Assert.CheckArgument(usetIds);
        Assert.CheckArgument(tenantId);
        baseMapper.delete(lambdaQuery().in(SysTenantUsetRole::getUsetId, usetIds).eq(SysTenantUsetRole::getTenantId, tenantId));
    }

    @Override
    public void deleteByTenant(Set<String> tenantIds) {
        Assert.CheckArgument(tenantIds);
        baseMapper.delete(lambdaQuery().in(SysTenantUsetRole::getTenantId, tenantIds));
    }
}
