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

import com.baomibing.authority.dto.GateLimitUtagDto;
import com.baomibing.authority.entity.SysGateLimitUtag;
import com.baomibing.authority.mapper.SysGateLimitUtagMapper;
import com.baomibing.authority.service.SysGateLimitUtagService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * SysGateLimitUtagServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysGateLimitUtagServiceImpl extends MBaseServiceImpl<SysGateLimitUtagMapper, SysGateLimitUtag, GateLimitUtagDto> implements SysGateLimitUtagService {

    @Override
    public List<GateLimitUtagDto> listByLimit(String limitId) {
        if (Checker.beEmpty(limitId)) {
            return Lists.newArrayList();
        }
        return mapper(baseMapper.selectList(lambdaQuery().eq(SysGateLimitUtag::getLimitId, limitId)));
    }

    @Override
    public void saveLimitUtag(GateLimitUtagDto limitUtag) {
        saveIt(limitUtag);
    }

    @Override
    public void updateLimitUtag(GateLimitUtagDto limitUtag) {
        updateIt(limitUtag);
    }

    @Override
    public void deleteLimitUtag(Set<String> ids) {
        deletes(ids);
    }

    @Override
    public void deleteByLimit(String limitId) {
        Assert.CheckArgument(limitId);
        baseMapper.delete(lambdaQuery().eq(SysGateLimitUtag::getLimitId, limitId));
    }
}
