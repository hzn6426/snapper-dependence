
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

import com.alibaba.fastjson.JSONArray;
import com.baomibing.authority.dto.UsetDataPermDto;
import com.baomibing.authority.entity.SysUsetDataPerm;
import com.baomibing.authority.mapper.SysUsetDataPermMapper;
import com.baomibing.authority.service.SysUsetDataPermService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * SysUsetDataPermServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysUsetDataPermServiceImpl extends MBaseServiceImpl<SysUsetDataPermMapper, SysUsetDataPerm, UsetDataPermDto> implements SysUsetDataPermService {

    @Override
    public UsetDataPermDto getUsetDataPerm(String usetId, String permId) {
        if (Checker.beEmpty(usetId) || Checker.beEmpty(permId)) {
            return null;
        }
        return mapper2v(baseMapper.selectOne(lambdaQuery().eq(SysUsetDataPerm::getUsetId, usetId).eq(SysUsetDataPerm::getPermId, permId)));
    }

    @Override
    public List<UsetDataPermDto> listUsetDataPerm(Set<String> usetIds, String permId) {
        if (Checker.beEmpty(usetIds) || Checker.beEmpty(permId)) {
            return null;
        }
        return mapper(baseMapper.selectList(lambdaQuery().in(SysUsetDataPerm::getUsetId, usetIds).eq(SysUsetDataPerm::getPermId, permId)));
    }

    @Override
    public void saveUserDataPerm(UsetDataPermDto perm) {
        Assert.CheckArgument(perm);
        Assert.CheckArgument(perm.getPermId());
        baseMapper.delete(lambdaQuery().eq(SysUsetDataPerm::getUsetId, perm.getUsetId()).eq(SysUsetDataPerm::getPermId, perm.getPermId()));
        if (Checker.beNotEmpty(perm.getSearchExpresses())) {
            perm.setPermExpress(JSONArray.toJSONString(perm.getSearchExpresses()));
            super.saveIt(perm);
        }
    }
}
