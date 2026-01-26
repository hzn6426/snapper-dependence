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

import com.baomibing.authority.dto.SysTenantGroupDto;
import com.baomibing.authority.dto.SysTenantGroupEntrustDto;
import com.baomibing.authority.entity.SysTenantGroup;
import com.baomibing.authority.entity.SysTenantGroupEntrust;
import com.baomibing.authority.mapper.SysTenantGroupEntrustMapper;
import com.baomibing.authority.service.SysTenantGroupEntrustService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * SysTenantGroupEntrustServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysTenantGroupEntrustServiceImpl extends MBaseServiceImpl<SysTenantGroupEntrustMapper, SysTenantGroupEntrust, SysTenantGroupEntrustDto> implements SysTenantGroupEntrustService {

    @Override
    public List<SysTenantGroupDto> listEntrustGroupsByGroupAndUserAndPerm(String tenantId, String orgId, String userId, String permId) {
        List<SysTenantGroupDto> emptyList = Lists.newArrayList();
        if(Checker.beNull(permId) || Checker.beEmpty(userId) || Checker.beEmpty(tenantId)) {
            return emptyList;
        }
        List<SysTenantGroup> list = this.baseMapper.listEntrustGroupsByGroupAndUserAndPerm(tenantId, orgId, userId, permId);
        return Checker.beEmpty(list) ? emptyList : Lists.newArrayList(this.collectionMapper.mapCollection(list, SysTenantGroupDto.class));
    }

    @Override
    public void deleteByGroupAndUserAndPerm(String tenantId, String orgId, String userId, String permId) {
        Assert.CheckArgument(tenantId);
        Assert.CheckArgument(orgId);
        Assert.CheckArgument(userId);
        Assert.CheckArgument(permId);
        this.baseMapper.deleteByGroupAndUserAndPerm(tenantId, orgId, userId, permId);
    }
}
