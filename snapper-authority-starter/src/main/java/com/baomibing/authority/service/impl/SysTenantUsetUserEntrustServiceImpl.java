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

import com.baomibing.authority.dto.SysTenantUsetUserEntrustDto;
import com.baomibing.authority.entity.SysTenantUsetUserEntrust;
import com.baomibing.authority.mapper.SysTenantUsetUserEntrustMapper;
import com.baomibing.authority.service.SysTenantUsetUserEntrustService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * SysTenantUsetUserEntrustServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysTenantUsetUserEntrustServiceImpl extends MBaseServiceImpl<SysTenantUsetUserEntrustMapper, SysTenantUsetUserEntrust, SysTenantUsetUserEntrustDto> implements SysTenantUsetUserEntrustService {

    @Override
    public List<String> listEntrustUserCodesByUsetAndPerm(Set<String> usetIds, String permId, String tenantId) {
        if (Checker.beEmpty(usetIds) || Checker.beEmpty(permId) || Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }
        return baseMapper.listEntrustUserCodesByUsetAndPerm(usetIds, permId, tenantId);
    }

    @Override
    public List<String> listEntrustUserIdsByUsetAndPerm(Set<String> usetIds, String permId, String tenantId) {
        if (Checker.beEmpty(usetIds) || Checker.beEmpty(permId) || Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }
        return baseMapper.listEntrustUserIdsByUsetAndPerm(usetIds, permId, tenantId);
    }

    @Override
    public void deleteByUsetAndPerm(String tenantId, String usetId, String permId) {
        Assert.CheckArgument(usetId);
        Assert.CheckArgument(permId);
        Assert.CheckArgument(tenantId);
        baseMapper.deleteByUsetAndPerm(tenantId, usetId, permId);
    }
}
