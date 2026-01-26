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

import com.baomibing.authority.dto.SysTenantUsetDataPermDto;
import com.baomibing.authority.entity.SysTenantUsetDataPerm;
import com.baomibing.authority.mapper.SysTenantUsetDataPermMapper;
import com.baomibing.authority.service.SysTenantUsetDataPermService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.alibaba.fastjson.JSONArray;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * SysTenantUsetDataPermServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysTenantUsetDataPermServiceImpl extends MBaseServiceImpl<SysTenantUsetDataPermMapper, SysTenantUsetDataPerm, SysTenantUsetDataPermDto> implements SysTenantUsetDataPermService {

    @Override
    public SysTenantUsetDataPermDto getUsetDataPerm(String tenantId, String usetId, String permId) {
        if (Checker.beEmpty(usetId) || Checker.beEmpty(permId) || Checker.beEmpty(tenantId)) {
            return null;
        }
        return mapper2v(baseMapper.selectOne(lambdaQuery().eq(SysTenantUsetDataPerm::getUsetId, usetId).eq(SysTenantUsetDataPerm::getPermId, permId).eq(SysTenantUsetDataPerm::getTenantId, tenantId)));
    }

    @Override
    public List<SysTenantUsetDataPermDto> listUsetDataPerm(Set<String> usetIds, String permId, String tenantId) {
        if (Checker.beEmpty(usetIds) || Checker.beEmpty(permId) || Checker.beEmpty(tenantId)) {
            return null;
        }
        return mapper(baseMapper.selectList(lambdaQuery().in(SysTenantUsetDataPerm::getUsetId, usetIds).eq(SysTenantUsetDataPerm::getPermId, permId).eq(SysTenantUsetDataPerm::getTenantId, tenantId)));
    }

    @Override
    public void saveUserDataPerm(SysTenantUsetDataPermDto perm) {
        Assert.CheckArgument(perm);
        Assert.CheckArgument(perm.getPermId());
        Assert.CheckArgument(perm.getTenantId());
        baseMapper.delete(lambdaQuery().eq(SysTenantUsetDataPerm::getUsetId, perm.getUsetId()).eq(SysTenantUsetDataPerm::getPermId, perm.getPermId()).eq(SysTenantUsetDataPerm::getTenantId, perm.getTenantId()));
        if (Checker.beNotEmpty(perm.getSearchExpresses())) {
            perm.setPermExpress(JSONArray.toJSONString(perm.getSearchExpresses()));
            super.saveIt(perm);
        }
    }
}
