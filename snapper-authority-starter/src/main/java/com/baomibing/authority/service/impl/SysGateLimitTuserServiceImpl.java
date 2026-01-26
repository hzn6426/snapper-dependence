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

import com.baomibing.authority.dto.GateLimitTuserDto;
import com.baomibing.authority.entity.SysGateLimitTuser;
import com.baomibing.authority.mapper.SysGateLimitTuserMapper;
import com.baomibing.authority.service.SysGateLimitTuserService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * SysGateLimitTuserServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysGateLimitTuserServiceImpl extends MBaseServiceImpl<SysGateLimitTuserMapper, SysGateLimitTuser, GateLimitTuserDto> implements SysGateLimitTuserService {

    @Override
    public List<GateLimitTuserDto> listByLimit(String limitId) {
        if (Checker.beEmpty(limitId)) {
            return Lists.newArrayList();
        }
        return mapper(baseMapper.selectList(lambdaQuery().eq(SysGateLimitTuser::getLimitId, limitId)));
    }

    @Override
    public void saveLimitUser(GateLimitTuserDto limitUser) {
        saveIt(limitUser);
    }

    @Override
    public void updateLimitUser(GateLimitTuserDto limitUser) {
        updateIt(limitUser);
    }

    @Override
    public void deleteLimitUser(Set<String> limitUserIds) {
        deletes(limitUserIds);
    }

    @Override
    public void deleteByLimit(String limitId) {
        Assert.CheckArgument(limitId);
        baseMapper.delete(lambdaQuery().eq(SysGateLimitTuser::getLimitId, limitId));
    }
}
