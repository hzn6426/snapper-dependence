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

import com.baomibing.authority.dto.SysTenantFunctionDto;
import com.baomibing.authority.entity.SysTenantFunction;
import com.baomibing.authority.mapper.SysTenantFunctionMapper;
import com.baomibing.authority.service.SysTenantFunctionService;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ObjectUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SysTenantFunctionServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysTenantFunctionServiceImpl extends MBaseServiceImpl<SysTenantFunctionMapper, SysTenantFunction, SysTenantFunctionDto> implements SysTenantFunctionService {

    @Override
    public void saveTenantFunction(SysTenantFunctionDto tenantFunction) {
        checkArgument(tenantFunction);
        checkArgument(tenantFunction.getTenantId());
        checkArgument(tenantFunction.getFunctionId());
        SysTenantFunction function = baseMapper.selectOne(lambdaQuery().eq(SysTenantFunction::getTenantId, tenantFunction.getTenantId())
                .eq(SysTenantFunction::getFunctionId, tenantFunction.getFunctionId()));
        if (Checker.beNull(function)) {
            super.saveIt(tenantFunction);
        }

    }

    @Override
    public void deleteByFunctions(String tenantId, Set<String> functionIds) {
        checkArgument(functionIds);
        checkArgument(tenantId);
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        baseMapper.delete(lambdaQuery().eq(SysTenantFunction::getTenantId, tenantId).in(SysTenantFunction::getFunctionId, functionIds));
    }

    @Override
    public List<String> listByTenant(String tenantId) {
        checkArgument(tenantId);
        List<SysTenantFunction> functions = baseMapper.selectList(lambdaQuery().eq(SysTenantFunction::getTenantId, tenantId));
        return functions.stream().map(SysTenantFunction::getFunctionId).collect(Collectors.toList());
    }

    @Override
    public SysTenantFunctionDto getByFunction(String tenantI, String functionId) {
        checkArgument(tenantI);
        checkArgument(functionId);
        return mapper2v(baseMapper.selectOne(lambdaQuery().eq(SysTenantFunction::getTenantId, tenantI).eq(SysTenantFunction::getFunctionId, functionId)));
    }
}
