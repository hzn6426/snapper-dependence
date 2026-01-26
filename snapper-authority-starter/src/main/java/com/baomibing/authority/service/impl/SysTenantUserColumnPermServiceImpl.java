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

import com.baomibing.authority.dto.SysTenantUserColumnPermDto;
import com.baomibing.authority.entity.SysTenantUserColumnPerm;
import com.baomibing.authority.mapper.SysTenantUserColumnPermMapper;
import com.baomibing.authority.service.SysTenantUserColumnPermService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.alibaba.fastjson.JSONArray;
import org.springframework.stereotype.Service;

/**
 * SysTenantUserColumnPermServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysTenantUserColumnPermServiceImpl extends MBaseServiceImpl<SysTenantUserColumnPermMapper, SysTenantUserColumnPerm, SysTenantUserColumnPermDto> implements SysTenantUserColumnPermService {

    @Override
    public void saveColumnPerm(SysTenantUserColumnPermDto perm) {
        Assert.CheckArgument(perm);
        doSetTenantId(perm);
        Assert.CheckArgument(perm.getPermId());
        Assert.CheckArgument(perm.getTenantId());
        deleteUserColumnPerm(perm.getTenantId(), perm.getUserId(), perm.getOrgId(), perm.getPermId());
        if (Checker.beNotEmpty(perm.getTableColumns())) {
            perm.setColumnExpress(JSONArray.toJSONString(perm.getTableColumns()));
            saveIt(perm);
        }
    }

    @Override
    public SysTenantUserColumnPermDto getUserColumnPerm(String tenantId, String userId, String orgId, String permId) {
        if (Checker.beEmpty(orgId) || Checker.beEmpty(userId) || Checker.beEmpty(permId) || Checker.beEmpty(tenantId)) {
            return null;
        }

        return mapper2v(baseMapper.selectOne(lambdaQuery()
                .eq(SysTenantUserColumnPerm::getOrgId, orgId).eq(SysTenantUserColumnPerm::getUserId, userId).eq(SysTenantUserColumnPerm::getPermId, permId).eq(SysTenantUserColumnPerm::getTenantId, tenantId)));
    }

    @Override
    public void deleteUserColumnPerm(String tenantId, String userId, String orgId, String permId) {
        Assert.CheckArgument(orgId, userId, permId);
        Assert.CheckArgument(tenantId);
        baseMapper.delete(lambdaQuery()
                .eq(SysTenantUserColumnPerm::getOrgId, orgId).eq(SysTenantUserColumnPerm::getUserId, userId).eq(SysTenantUserColumnPerm::getPermId, permId));
    }
}
