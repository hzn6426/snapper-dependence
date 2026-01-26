
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

import com.baomibing.authority.dto.UsetUserEntrustDto;
import com.baomibing.authority.entity.SysUsetUserEntrust;
import com.baomibing.authority.mapper.SysUsetUserEntrustMapper;
import com.baomibing.authority.service.SysUsetUserEntrustService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * SysUsetUserEntrustServiceImpl
 *
 * @author zening
 * @version 1.0.0
 */
@Service
public class SysUsetUserEntrustServiceImpl extends MBaseServiceImpl<SysUsetUserEntrustMapper, SysUsetUserEntrust, UsetUserEntrustDto> implements SysUsetUserEntrustService {

    @Override
    public List<String> listEntrustUserCodesByUsetAndPerm(Set<String> usetIds, String permId) {
        if (Checker.beEmpty(usetIds) || Checker.beEmpty(permId)) {
            return Lists.newArrayList();
        }
        return baseMapper.listEntrustUserCodesByUsetAndPerm(usetIds, permId);
    }

    @Override
    public List<String> listEntrustUserIdsByUsetAndPerm(Set<String> usetIds, String permId) {
        if (Checker.beEmpty(usetIds) || Checker.beEmpty(permId)) {
            return Lists.newArrayList();
        }
        return baseMapper.listEntrustUserIdsByUsetAndPerm(usetIds, permId);
    }

    @Override
    public void deleteByUsetAndPerm(String usetId, String permId) {
        Assert.CheckArgument(usetId);
        Assert.CheckArgument(permId);
        baseMapper.deleteByUsetAndPerm(usetId, permId);
    }
}
