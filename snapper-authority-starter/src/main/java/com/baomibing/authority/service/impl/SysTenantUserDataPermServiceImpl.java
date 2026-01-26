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

import com.baomibing.authority.dto.SysTenantUserDataPermDto;
import com.baomibing.authority.entity.SysTenantUserDataPerm;
import com.baomibing.authority.mapper.SysTenantUserDataPermMapper;
import com.baomibing.authority.service.SysTenantUserDataPermService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.alibaba.fastjson.JSONArray;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * SysTenantUserDataPermServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysTenantUserDataPermServiceImpl extends MBaseServiceImpl<SysTenantUserDataPermMapper, SysTenantUserDataPerm, SysTenantUserDataPermDto> implements SysTenantUserDataPermService {

    @Override
    public void saveUserDataPerm(SysTenantUserDataPermDto perm) {
        Assert.CheckArgument(perm);
        doSetTenantId(perm);
        Assert.CheckArgument(perm.getPermId());
        baseMapper.delete(lambdaQuery().eq(SysTenantUserDataPerm::getOrgId, perm.getOrgId()).eq(SysTenantUserDataPerm::getUserId, perm.getUserId())
                .eq(SysTenantUserDataPerm::getPermId, perm.getPermId()).eq(SysTenantUserDataPerm::getTenantId, perm.getTenantId()));
        if (Checker.beNotEmpty(perm.getSearchExpresses())) {
            perm.setPermExpress(JSONArray.toJSONString(perm.getSearchExpresses()));
            super.saveIt(perm);
        }
    }

    @Override
    public SysTenantUserDataPermDto getUserDataPerm(String tenantId, String userId, String orgId, String permId) {
        if (Checker.beEmpty(userId) || Checker.beEmpty(orgId) || Checker.beEmpty(permId)) {
            return null;
        }
        SysTenantUserDataPerm perm = baseMapper.selectOne(lambdaQuery().eq(SysTenantUserDataPerm::getOrgId, orgId).eq(SysTenantUserDataPerm::getUserId, userId)
                .eq(SysTenantUserDataPerm::getPermId, permId).eq(SysTenantUserDataPerm::getTenantId, tenantId));
        if (Checker.beNotNull(perm)) {
            Date start = perm.getPermStartTime();
            Date end = perm.getPermEndTime();
            if (Checker.beNotNull(start) && Checker.beNotNull(end)) {
                Date now = new Date();
                boolean isOk = now.before(end) && now.after(start);
                if (!isOk) {
                    return null;
                }
            }
        }
        return mapper2v(perm);
    }
}
