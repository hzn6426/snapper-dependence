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

import com.baomibing.authority.dto.SysTenantUsetColumnPermDto;
import com.baomibing.authority.entity.SysTenantUsetColumnPerm;
import com.baomibing.authority.mapper.SysTenantUsetColumnPermMapper;
import com.baomibing.authority.service.SysTenantUsetColumnPermService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * SysTenantUsetColumnPermServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysTenantUsetColumnPermServiceImpl extends MBaseServiceImpl<SysTenantUsetColumnPermMapper, SysTenantUsetColumnPerm, SysTenantUsetColumnPermDto> implements SysTenantUsetColumnPermService {

    @Override
    public void saveColumnPerm(SysTenantUsetColumnPermDto perm) {
        Assert.CheckArgument(perm);
        Assert.CheckArgument(perm.getPermId());
        Assert.CheckArgument(perm.getTenantId());
        deleteUsetColumnPerm(perm.getTenantId(), perm.getUsetId(), perm.getPermId());
        if (Checker.beNotEmpty(perm.getTableColumns())) {
            perm.setColumnExpress(JSONArray.toJSONString(perm.getTableColumns()));
            saveIt(perm);
        }
    }

    @Override
    public SysTenantUsetColumnPermDto getUsetColumnPerm(String tenantId, String usetId, String permId) {
        if (Checker.beEmpty(usetId) || Checker.beEmpty(permId) || Checker.beEmpty(tenantId)) {
            return null;
        }
        return mapper2v(baseMapper.selectOne(lambdaQuery().eq(SysTenantUsetColumnPerm::getPermId, permId).eq(SysTenantUsetColumnPerm::getUsetId, usetId).eq(SysTenantUsetColumnPerm::getTenantId, tenantId)));
    }

    @Override
    public List<SysTenantUsetColumnPermDto> listUsetColumnPerm(Set<String> usetIds, String permId, String tenantId) {
        if (Checker.beEmpty(usetIds) || Checker.beEmpty(permId) || Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }
        return mapper(baseMapper.selectList(lambdaQuery().eq(SysTenantUsetColumnPerm::getPermId, permId).in(SysTenantUsetColumnPerm::getUsetId, usetIds).eq(SysTenantUsetColumnPerm::getTenantId, tenantId)));
    }

    @Override
    public void deleteUsetColumnPerm(String tenantId, String usetId, String permId) {
        Assert.CheckArgument(usetId, permId);
        Assert.CheckArgument(tenantId);
        baseMapper.delete(lambdaQuery().eq(SysTenantUsetColumnPerm::getPermId, permId).eq(SysTenantUsetColumnPerm::getUsetId, usetId).eq(SysTenantUsetColumnPerm::getTenantId, tenantId));
    }
}
