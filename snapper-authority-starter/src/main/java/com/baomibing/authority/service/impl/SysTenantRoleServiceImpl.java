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

import com.baomibing.authority.action.TenantRoleAction;
import com.baomibing.authority.dto.SysTenantDto;
import com.baomibing.authority.dto.SysTenantRoleDto;
import com.baomibing.authority.entity.SysTenantRole;
import com.baomibing.authority.mapper.SysTenantRoleMapper;
import com.baomibing.authority.service.*;
import com.baomibing.core.common.Assert;
import com.baomibing.core.common.SearchResult;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.core.process.StateWorkFlow;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * SysTenantRoleServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysTenantRoleServiceImpl extends MBaseServiceImpl<SysTenantRoleMapper, SysTenantRole, SysTenantRoleDto> implements SysTenantRoleService {

    @Autowired private SysTenantUserRoleService userRoleService;
    @Autowired private SysTenantPositionRoleService positionRoleService;
    @Autowired private SysTenantRoleResourceService roleResourceService;
    @Autowired private SysTenantService tenantService;
    @Autowired private SysTenantMenuService tenantMenuService;
    @Autowired private SysTenantButtonService tenantButtonService;
    @Autowired private SysTenantUsetRoleService usetRoleService;

    @Override
    public SearchResult<SysTenantRoleDto> search(SysTenantRoleDto role, int pageNumber, int pageSize) {
        doSetTenantId(role);
        if (Checker.beNull(role) || Checker.beEmpty(role.getTenantId())) {
            return new SearchResult<>();
        }
        LambdaQueryWrapper<SysTenantRole> wrapper = lambdaQuery().eq(SysTenantRole::getTenantId, role.getTenantId()).like(Checker.beNotEmpty(role.getRoleName()), SysTenantRole::getRoleName, role.getRoleName())
                .eq(Checker.beNotEmpty(role.getState()), SysTenantRole::getState, role.getState());
        return super.search(wrapper, pageNumber, pageSize);
    }

    @Override
    public void doUpdateIt(SysTenantRoleDto role) {
        Assert.CheckArgument(role);
        doSetTenantId(role);
        super.updateIt(role);
    }

    @Override
    public void doSaveIt(SysTenantRoleDto role) {
        Assert.CheckArgument(role);
        doSetTenantId(role);
        Assert.CheckArgument(role.getTenantId());
        StateWorkFlow.doInitState(role);
        super.saveIt(role);
    }

    @Override
    public List<SysTenantRoleDto> listByRoleName(String tenantId, String roleName) {
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        if (Checker.beEmpty(tenantId) || Checker.beEmpty(roleName)) {
            return Lists.newArrayList();
        }
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        return mapper(baseMapper.selectList(lambdaQuery().eq(SysTenantRole::getTenantId, tenantId).like(SysTenantRole::getRoleName, roleName)));
    }

    @Override
    public List<SysTenantRoleDto> listAllRoles(String tenantId) {
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        if (Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }
        return mapper(baseMapper.selectList(lambdaQuery().eq(SysTenantRole::getTenantId, tenantId)));
    }

    private void updateStateByIds(Set<String> ids, TenantRoleAction action) {
        Assert.CheckArgument(ids);
        Assert.CheckArgument(action);
        List<SysTenantRoleDto> roles = super.gets(ids);
        if (roles.size() != ids.size()) {
            throw new ServerRuntimeException(ExceptionEnum.IDS_CONTAINS_INVALID_ID);
        }
        for(SysTenantRoleDto role : roles) {
            StateWorkFlow.doProcess(role, action);
        }
        super.updateItBatch(roles);
    }

    @Override
    public void use(Set<String> ids) {
        updateStateByIds(ids, TenantRoleAction.USE);
    }

    @Override
    public void stop(Set<String> ids) {
        updateStateByIds(ids, TenantRoleAction.STOP);
    }

    @Override
    public void deleteRoles(Set<String> ids, String tenantId) {
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        Assert.CheckArgument(ids);
        List<SysTenantRoleDto> roles = super.gets(ids);
        for (SysTenantRoleDto r : roles) {
            StateWorkFlow.doAssertDelete(r);
        }
        userRoleService.deleteByRoles(ids, tenantId);
        positionRoleService.deleteByRoles(ids, tenantId);
        roleResourceService.deleteByRoles(ids, tenantId);
        super.deleteItBatch(roles);
    }

    @Override
    public void deleteByTenant(Set<String> tenantIds) {
        Assert.CheckArgument(tenantIds);
        userRoleService.deleteByTenant(tenantIds);
        positionRoleService.deleteByTenant(tenantIds);
        usetRoleService.deleteByTenant(tenantIds);
        baseMapper.delete(lambdaQuery().in(SysTenantRole::getTenantId, tenantIds));
    }

    @Override
    public SysTenantRoleDto doInitRole(String tenantId) {
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        Assert.CheckArgument(tenantId);
        SysTenantDto tenant = tenantService.getIt(tenantId);
        if (Checker.beNull(tenant)) {
            throw new ServerRuntimeException(ExceptionEnum.CAN_NOT_FIND_ID_OF_TENANT, tenantId);
        }
        SysTenantRole role = baseMapper.selectOne(lambdaQuery().eq(SysTenantRole::getTenantId, tenantId).eq(SysTenantRole::getBeRoot, true));
        if (Checker.beNotNull(role)) {
            return mapper2v(role);
        }
        SysTenantRoleDto r = new SysTenantRoleDto();
        r.setTenantId(tenantId).setBeRoot(Boolean.TRUE).setRoleName("全权限角色");
        StateWorkFlow.doInitState(r);
        super.saveIt(r);
        return r;
    }

    @Override
    public SysTenantRoleDto getRootRole(String tenantId) {
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        if (Checker.beEmpty(tenantId)) {
            return null;
        }
        SysTenantRole role = baseMapper.selectOne(lambdaQuery().eq(SysTenantRole::getTenantId, tenantId).eq(SysTenantRole::getBeRoot, true));
        return mapper2v(role);
    }
}
