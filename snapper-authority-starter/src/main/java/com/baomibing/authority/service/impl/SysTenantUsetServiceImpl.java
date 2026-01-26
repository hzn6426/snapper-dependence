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

import com.baomibing.authority.action.TenantUsetAction;
import com.baomibing.authority.dto.SysTenantUsetDto;
import com.baomibing.authority.entity.SysTenantUset;
import com.baomibing.authority.mapper.SysTenantUsetMapper;
import com.baomibing.authority.service.SysTenantUserUsetService;
import com.baomibing.authority.service.SysTenantUsetRoleService;
import com.baomibing.authority.service.SysTenantUsetService;
import com.baomibing.core.common.Assert;
import com.baomibing.core.common.SearchResult;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.core.process.StateWorkFlow;
import com.baomibing.core.wrap.CommonTreeWrap;
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
 * SysTenantUsetServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysTenantUsetServiceImpl extends MBaseServiceImpl<SysTenantUsetMapper, SysTenantUset, SysTenantUsetDto> implements SysTenantUsetService {

    @Autowired private SysTenantUserUsetService userUsetService;
    @Autowired private SysTenantUsetRoleService usetRoleService;

    @Override
    public SearchResult<SysTenantUsetDto> search(SysTenantUsetDto uset, int pageNumber, int pageSize) {
        doSetTenantId(uset);
        if (Checker.beNull(uset) || Checker.beEmpty(uset.getTenantId())) {
            return new SearchResult<>();
        }
        LambdaQueryWrapper<SysTenantUset> query = lambdaQuery().eq(SysTenantUset::getTenantId, uset.getTenantId()).like(Checker.beNotEmpty(uset.getUsetName()), SysTenantUset::getUsetName, uset.getUsetName())
                .eq(Checker.beNotEmpty(uset.getState()), SysTenantUset::getState, uset.getState());
        return super.search(query, pageNumber, pageSize);
    }

    @Override
    public void doSave(SysTenantUsetDto uset) {
        Assert.CheckArgument(uset);
        doSetTenantId(uset);
        Assert.CheckArgument(uset.getTenantId());
        StateWorkFlow.doInitState(uset);
        super.saveIt(uset);
    }

    @Override
    public void doUpdate(SysTenantUsetDto uset) {
        Assert.CheckArgument(uset);
        doSetTenantId(uset);
        super.updateIt(uset);
    }

    private void updateStateByIds(Set<String> ids, TenantUsetAction action) {
        Assert.CheckArgument(ids);
        Assert.CheckArgument(action);
        List<SysTenantUsetDto> usets = super.gets(ids);
        if (usets.size() != ids.size()) {
            throw new ServerRuntimeException(ExceptionEnum.IDS_CONTAINS_INVALID_ID);
        }
        for(SysTenantUsetDto uset : usets) {
            StateWorkFlow.doProcess(uset, action);
        }
        super.updateItBatch(usets);
    }

    @Override
    public void use(Set<String> ids) {
        updateStateByIds(ids, TenantUsetAction.USE);
    }

    @Override
    public void stop(Set<String> ids) {
        updateStateByIds(ids, TenantUsetAction.STOP);
    }

    @Override
    public void deleteUsets(Set<String> ids, String tenantId) {
        Assert.CheckArgument(ids);
        List<SysTenantUsetDto> usets = super.gets(ids);
        for (SysTenantUsetDto r : usets) {
            StateWorkFlow.doAssertDelete(r);
        }
        userUsetService.deleteByUsets(ids, tenantId);
        usetRoleService.deleteByUsets(ids, tenantId);
        super.deleteItBatch(usets);
    }

    @Override
    public void deleteByTenant(Set<String> tenantIds) {
        Assert.CheckArgument(tenantIds);
        userUsetService.deleteByTenant(tenantIds);
        baseMapper.delete(lambdaQuery().in(SysTenantUset::getTenantId, tenantIds));
    }

    @Override
    public List<CommonTreeWrap> treeAllUset(String tenantId) {
        List<CommonTreeWrap> trees = Lists.newArrayList();
        if (Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        List<SysTenantUset> usets = baseMapper.selectList(lambdaQuery().eq(SysTenantUset::getTenantId, tenantId));
        if (Checker.beEmpty(usets)) {
            return trees;
        }
        usets.forEach(u -> trees.add(new CommonTreeWrap().setKey(u.getId()).setTitle(u.getUsetName()).setIsLeaf(Boolean.TRUE)));
        return trees;
    }
}
