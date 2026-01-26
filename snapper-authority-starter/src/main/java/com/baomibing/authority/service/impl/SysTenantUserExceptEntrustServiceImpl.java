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

import com.baomibing.authority.dto.SysTenantUserExceptEntrustDto;
import com.baomibing.authority.entity.SysTenantUserExceptEntrust;
import com.baomibing.authority.mapper.SysTenantUserExceptEntrustMapper;
import com.baomibing.authority.service.SysTenantUserExceptEntrustService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * SysTenantUserExceptEntrustServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysTenantUserExceptEntrustServiceImpl extends MBaseServiceImpl<SysTenantUserExceptEntrustMapper, SysTenantUserExceptEntrust, SysTenantUserExceptEntrustDto> implements SysTenantUserExceptEntrustService {

    @Override
    public List<String> listEntrustUserIdsByGroupAndUserAndPerm(String tenantId, String orgId, String userId, String permId) {
        if (Checker.beEmpty(userId) || Checker.beNull(permId) || Checker.beEmpty(orgId) || Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }
        return this.baseMapper.listEntrustUserIdsByGroupAndUserAndPerm(tenantId, orgId, userId, permId);
    }

    @Override
    public List<String> listEntrustUserCodesByGroupAndUserAndPerm(String tenantId, String orgId, String userId, String permId) {
        if (Checker.beEmpty(userId) || Checker.beNull(permId) || Checker.beEmpty(tenantId) || Checker.beEmpty(orgId)) {
            return Lists.newArrayList();
        }
        return this.baseMapper.listEntrustUserCodesByGroupAndUserAndPerm(tenantId, orgId, userId, permId);
    }

    @Override
    public void deleteByGroupAndUserAndPerm(String tenantId, String orgId, String userId, String permId) {
        Assert.CheckArgument(userId);
        Assert.CheckArgument(permId);
        Assert.CheckArgument(tenantId);
        Assert.CheckArgument(orgId);
        this.baseMapper.deleteByGroupAndUserAndPerm(tenantId, orgId, userId, permId);
    }
}
