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

import com.baomibing.authority.dto.ButtonTenantDto;
import com.baomibing.authority.dto.SysTenantButtonDto;
import com.baomibing.authority.entity.SysButtonTenant;
import com.baomibing.authority.entity.SysTenantButton;
import com.baomibing.authority.mapper.SysTenantButtonMapper;
import com.baomibing.authority.service.SysTenantButtonService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SysTenantButtonServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysTenantButtonServiceImpl extends MBaseServiceImpl<SysTenantButtonMapper, SysTenantButton, SysTenantButtonDto> implements SysTenantButtonService {


    private Set<String> getIdsByTenant(String tenantId) {
        if (Checker.beEmpty(tenantId)) {
            return Sets.newHashSet();
        }
        List<SysTenantButton> buttons = baseMapper.selectList(lambdaQuery().eq(SysTenantButton::getTenantId, tenantId));
        return buttons.stream().map(SysTenantButton::getButtonId).collect(Collectors.toSet());
    }

    @Override
    public void deleteButtonsByTenant(Set<String> buttonIds, String tenantId) {
        Assert.CheckArgument(buttonIds);
        Assert.CheckArgument(tenantId);
        baseMapper.delete(lambdaQuery().eq(SysTenantButton::getTenantId, tenantId).in(SysTenantButton::getButtonId, buttonIds));
    }



    @Override
    public List<String> listByMenuForGrant(String tenantId, String menuId) {
        if (Checker.beEmpty(tenantId) || Checker.beEmpty(menuId)) {
            return Lists.newArrayList();
        }
        return baseMapper.listByMenuForGrant(tenantId, menuId).stream().map(SysButtonTenant::getId).collect(Collectors.toList());
    }

    @Override
    public List<ButtonTenantDto> listAllButtonsForGrant(String tenantId) {
        if (Checker.beEmpty(tenantId)) {
            return  Lists.newArrayList();
        }
        return Lists.newArrayList(collectionMapper.mapCollection(baseMapper.listAllButtonsForGrant(tenantId), ButtonTenantDto.class));
    }

    @Override
    public void doSave(Set<String> buttonIds, String tenantId) {
        Assert.CheckArgument(tenantId);
        Assert.CheckArgument(buttonIds);
        List<SysTenantButtonDto> toAddButtons = Lists.newArrayList();
        buttonIds.forEach(b -> toAddButtons.add(new SysTenantButtonDto().setButtonId(b).setTenantId(tenantId)));
        super.saveItBatch(toAddButtons);
    }


    @Override
    public void deleteByButtons(Set<String> ids) {
        Assert.CheckArgument(ids);
        baseMapper.delete(lambdaQuery().in(SysTenantButton::getButtonId, ids));
    }
}
