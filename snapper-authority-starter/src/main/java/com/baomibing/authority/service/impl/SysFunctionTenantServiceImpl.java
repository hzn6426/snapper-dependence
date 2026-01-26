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

import com.baomibing.authority.action.FunctionTenantAction;
import com.baomibing.authority.constant.enums.FunctionFeeTypeEnum;
import com.baomibing.authority.dto.SysFunctionTenantDto;
import com.baomibing.authority.dto.SysTenantDto;
import com.baomibing.authority.dto.SysTenantFunctionDto;
import com.baomibing.authority.dto.SysTenantRoleDto;
import com.baomibing.authority.entity.SysFunctionTenant;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.mapper.SysFunctionTenantMapper;
import com.baomibing.authority.service.*;
import com.baomibing.authority.state.FunctionTenantState;
import com.baomibing.core.common.Assert;
import com.baomibing.core.common.SearchResult;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.core.process.StateWorkFlow;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.tenant.TenantFunctionConsume;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ObjectUtil;
import com.baomibing.tool.util.SnowflakeIdWorker;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * SysFunctionTenantServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Slf4j
@Service
public class SysFunctionTenantServiceImpl extends MBaseServiceImpl<SysFunctionTenantMapper, SysFunctionTenant, SysFunctionTenantDto> implements SysFunctionTenantService {

    @Autowired private SysTenantFunctionService tenantFunctionService;
    @Autowired private SysTenantService tenantService;
    @Autowired private SysTenantRoleService tenantRoleService;
    @Autowired private SysTenantRoleResourceService tenantRoleResourceService;
    @Autowired private SysTenantFeeService tenantFeeService;

    @Override
    public SearchResult<SysFunctionTenantDto> search(SysFunctionTenantDto v, int pageNo, int pageSize) {
        return search(lambdaQuery(),  pageNo, pageSize);
    }

    @Override
    public void saveFunction(SysFunctionTenantDto functionTenant) {
        StateWorkFlow.doInitState(functionTenant);
        SysFunctionTenantDto exist = getByUrlAndMethod(functionTenant.getRequestUrl(), functionTenant.getRequestMethod());
        if (Checker.beNotNull(exist)) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.TENANT_FUNCTION_HAVE_EXIST);
        }
        FunctionFeeTypeEnum feeTypeEnum = EnumUtils.getEnum(FunctionFeeTypeEnum.class, functionTenant.getFeeType());
        if (Checker.beNull(feeTypeEnum)) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.INVALID_FUNCTION_FEE_TYPE, functionTenant.getFeeType());
        }
        if (feeTypeEnum.equals(FunctionFeeTypeEnum.MONTH)) {
            functionTenant.setExpireTime(new DateTime().plusMonths(1).toDate());
        }
        functionTenant.setId(SnowflakeIdWorker.getId());
//        functionTenant.setId(functionTenant.getFunctionId());
        super.saveIt(functionTenant);
    }

    @Override
    public void updateFunction(SysFunctionTenantDto functionTenant) {
        super.updateIt(functionTenant);
    }

    @Override
    public void deleteFunction(Set<String> ids) {
        super.deletes(ids);
    }

    @Override
    public SysFunctionTenantDto getByUrlAndMethod(String url, String method) {
        if (Checker.beEmpty(url) || Checker.beEmpty(method)) {
            return null;
        }
        List<SysFunctionTenant> list = baseMapper.selectList(lambdaQuery().eq(SysFunctionTenant::getRequestUrl, url).eq(SysFunctionTenant::getRequestMethod, method));
        return Checker.beEmpty(list) ? null : mapper2v(list.get(0));
    }


    @Override
    public TenantFunctionConsume doConsume(String tenantId, String requestUrl, String requestMethod) {
        Assert.CheckArgument(tenantId);
        Assert.CheckArgument(requestUrl);
        Assert.CheckArgument(requestMethod);
        SysFunctionTenantDto function = getByUrlAndMethod(requestUrl, requestMethod);
        if (Checker.beNull(function)) {
            return null;
        }
        if (FunctionTenantState.OFFLINE.name().equals(function.getState())) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.FUNCTION_HAVE_BEEN_OFFLINE);
        }
        SysTenantFunctionDto tenantFunction = tenantFunctionService.getByFunction(tenantId, function.getFunctionId());
        if (Checker.beNull(tenantFunction)) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.TENANT_NOT_OPEN_FUNCTION_PRIVILEGE, function.getFunctionName());
        }
        if (Checker.beNull(tenantFunction.getExpireTime()) || tenantFunction.getExpireTime().before(new Date())) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.TENANT_FUNCTION_EXPIRED, function.getFunctionName());
        }
        TenantFunctionConsume consume = new TenantFunctionConsume();
        consume.setFunctionId(function.getFunctionId()).setFunctionName(function.getFunctionName())
                .setTenantId(tenantId).setState(function.getState()).setFeeType(function.getFeeType())
                .setRequestMethod(requestMethod).setRequestUrl(requestUrl).setUnitPrice(function.getUnitPrice())
                .setRequestQty(1);

//        FunctionFeeTypeEnum feeTypeEnum = EnumUtils.getEnum(FunctionFeeTypeEnum.class, function.getFeeType());
//        if (Checker.beNull(feeTypeEnum)) {
//            throw new ServerRuntimeException(AuthorizationExceptionEnum.INVALID_FUNCTION_FEE_TYPE, function.getFeeType());
//        }
//
//        if (feeTypeEnum.equals(FunctionFeeTypeEnum.MONTH)) {
//            if (Checker.beNull(tenantFunction.getExpireTime()) || tenantFunction.getExpireTime().before(new Date())) {
//                throw new ServerRuntimeException(AuthorizationExceptionEnum.TENANT_FUNCTION_EXPIRED, function.getFunctionName());
//            }
//            consume.setAfterMoney(((SysTenantDto)this.tenantService.getIt(tenantId)).getBalance());
//        } else if (FunctionFeeTypeEnum.REQUEST.equals(feeTypeEnum)) {
//            BigDecimal totalMoney = consume.getUnitPrice().multiply(BigDecimal.valueOf(consume.getRequestQty())).setScale(2, RoundingMode.HALF_UP);
//            log.info("********************* SysFunctionTenantServiceImpl say: function consume the qty :{}, unit price :{}, total money: {}", consume.getRequestQty(), consume.getUnitPrice(), totalMoney);
//            tenantService.doConsumeMoney(tenantId, totalMoney);
//            consume.setAfterMoney(tenantService.getIt(tenantId).getBalance());
//        }
        return consume;
    }

    @Override
    public void recoveryConsume(TenantFunctionConsume consume) {
        Assert.CheckArgument(consume);
        Assert.CheckArgument(consume.getTenantId());
        Assert.CheckArgument(consume.getFunctionId());
        Assert.CheckArgument(consume.getUnitPrice());
        Assert.CheckArgument(consume.getRequestQty());
        SysFunctionTenantDto function = getByFunctionId(consume.getFunctionId());
        if (Checker.beNull(function)) {
            throw new ServerRuntimeException(ExceptionEnum.CAN_NOT_FIND_ID_OF_FUNCTION, consume.getFunctionId());
        }
        FunctionFeeTypeEnum feeTypeEnum = EnumUtils.getEnum(FunctionFeeTypeEnum.class, function.getFeeType());
        if (Checker.beNull(feeTypeEnum)) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.INVALID_FUNCTION_FEE_TYPE, function.getFeeType());
        }
        if (FunctionFeeTypeEnum.REQUEST.equals(feeTypeEnum)) {
            BigDecimal totalMoney = consume.getUnitPrice().multiply(BigDecimal.valueOf(consume.getRequestQty())).setScale(2, RoundingMode.HALF_UP);
            log.info("********************* SysFunctionTenantServiceImpl say: function recovery the qty :{}, unit price :{}, total money: {}", consume.getRequestQty(), consume.getUnitPrice(), totalMoney);
            tenantService.doRecoveryMoney(consume.getTenantId(), totalMoney);
        }
    }

    private void doProcess(SysFunctionTenantDto function, FunctionTenantAction action) {
        SysFunctionTenantDto f = new SysFunctionTenantDto();
        f.setId(function.getId()).setState(function.getState());
        StateWorkFlow.doProcess(f, action);
        super.updateIt(f);
    }

    @Override
    public void doOffline(Set<String> ids) {
        List<SysFunctionTenantDto> functions = super.gets(ids);
        for (SysFunctionTenantDto function : functions) {
            doProcess(function, FunctionTenantAction.OFFLINE);
        }
    }

    @Override
    public void doOnline(Set<String> ids) {
        List<SysFunctionTenantDto> functions = super.gets(ids);
        for (SysFunctionTenantDto function : functions) {
            doProcess(function, FunctionTenantAction.ONLINE);
        }
    }

    @Override
    public void doOpenFunction(String tenantId, String functionId) {
        Assert.CheckArgument(tenantId);
        Assert.CheckArgument(functionId);
        SysTenantDto tenant = tenantService.getIt(tenantId);
        if (Checker.beNull(tenant)) {
            throw new ServerRuntimeException(ExceptionEnum.CAN_NOT_FIND_ID_OF_TENANT, tenantId);
        }
        SysFunctionTenantDto function = getByFunctionId(functionId);
        if (Checker.beNull(function)) {
            throw new ServerRuntimeException(ExceptionEnum.CAN_NOT_FIND_ID_OF_FUNCTION, functionId);
        }

        Date expireTime = null;
//        //月付扣费
//        if (FunctionFeeTypeEnum.MONTH.name().equals(function.getFeeType())) {
//            tenantService.doConsumeMoney(tenantId, function.getUnitPrice());
//            SysTenantFeeDto fee = new SysTenantFeeDto();
//            fee.setTenantId(tenantId).setExchangeMethod(function.getRequestMethod())
//                    .setExchangeTime(new Date()).setExchangeName(function.getFunctionName())
//                    .setExchangeUrl(function.getRequestUrl()).setFeeType(function.getFeeType()).setFunctionId(function.getFunctionId()).setOs(currentOs()).setBrowser(currentBrowser())
//                    .setRequestQty(1).setUnitPrice(function.getUnitPrice()).setTotalPrice(function.getUnitPrice()).setAfterMoney(tenantService.getIt(tenantId).getBalance());
//            tenantFeeService.saveFeeAsync(fee);
//            expireTime = new DateTime().plusMonths(1).toDate();
//        }

        SysTenantFunctionDto tenantFunction = new SysTenantFunctionDto();
        tenantFunction.setFunctionId(functionId).setTenantId(tenantId).setFunctionName(function.getFunctionName()).setExpireTime(expireTime);
        tenantFunctionService.saveTenantFunction(tenantFunction);

        //角色加入权限
        SysTenantRoleDto role = tenantRoleService.getRootRole(tenantId);
        tenantRoleResourceService.addButtonPermIfNotExistByRole(tenantId, role.getId(), Sets.newHashSet(functionId));

    }

    @Override
    public void doDeferFunction(String tenantId, String functionId) {
        Assert.CheckArgument(tenantId);
        Assert.CheckArgument(functionId);
        SysTenantDto tenant = tenantService.getIt(tenantId);
        if (Checker.beNull(tenant)) {
            throw new ServerRuntimeException(ExceptionEnum.CAN_NOT_FIND_ID_OF_TENANT, tenantId);
        }
        SysFunctionTenantDto function = getByFunctionId(functionId);
        if (Checker.beNull(function)) {
            throw new ServerRuntimeException(ExceptionEnum.CAN_NOT_FIND_ID_OF_FUNCTION, functionId);
        }
//        if (!FunctionFeeTypeEnum.MONTH.name().equals(function.getFeeType())) {
//            throw new ServerRuntimeException(AuthorizationExceptionEnum.FUNCTION_FEE_TYPE_NOT_MONTH_CAN_NOT_DEFER);
//        }

        SysTenantFunctionDto tenantFunction = tenantFunctionService.getByFunction(tenantId, function.getFunctionId());
        if (Checker.beNull(tenantFunction)) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.TENANT_NOT_OPEN_FUNCTION_PRIVILEGE, function.getFunctionName());
        }


//        tenantService.doConsumeMoney(tenantId, function.getUnitPrice());
//        SysTenantFeeDto fee = new SysTenantFeeDto();
//        fee.setTenantId(tenantId).setExchangeMethod(function.getRequestMethod())
//                .setExchangeTime(new Date()).setExchangeName(function.getFunctionName())
//                .setExchangeUrl(function.getRequestUrl()).setFeeType(function.getFeeType()).setFunctionId(function.getFunctionId()).setOs(currentOs()).setBrowser(currentBrowser())
//                .setRequestQty(1).setUnitPrice(function.getUnitPrice()).setTotalPrice(function.getUnitPrice()).setAfterMoney(tenantService.getIt(tenantId).getBalance());
//        tenantFeeService.saveFeeAsync(fee);
        Date expireTime = new DateTime(ObjectUtil.defaultIfNull(tenantFunction.getExpireTime(), new Date())).plusMonths(1).toDate();
        tenantFunction.setExpireTime(expireTime);
        tenantFunctionService.updateIt(tenantFunction);
    }

    @Override
    public void doCloseFunction(String tenantId, String functionId) {
        Assert.CheckArgument(tenantId);
        Assert.CheckArgument(functionId);
        SysTenantDto tenant = tenantService.getIt(tenantId);
        if (Checker.beNull(tenant)) {
            throw new ServerRuntimeException(ExceptionEnum.CAN_NOT_FIND_ID_OF_TENANT, tenantId);
        }
        SysFunctionTenantDto function = super.getIt(functionId);
        if (Checker.beNull(function)) {
            throw new ServerRuntimeException(ExceptionEnum.CAN_NOT_FIND_ID_OF_FUNCTION, functionId);
        }

        //角色删除权限
        SysTenantRoleDto role = tenantRoleService.getRootRole(tenantId);
        tenantRoleResourceService.deleteButtonPermByRole(tenantId, role.getId(), Sets.newHashSet(functionId));
        //删除关联
        tenantFunctionService.deleteByFunctions(tenantId, Sets.newHashSet(functionId));
    }

    @Override
    public List<SysFunctionTenantDto> listAllOnlineFunction() {
        return mapper(baseMapper.listAllOnlineFunction());
    }

    @Override
    public SysFunctionTenantDto getByFunctionId(String functionId) {
        return mapper2v(baseMapper.selectOne(lambdaQuery().eq(SysFunctionTenant::getFunctionId, functionId)));
    }

    @Override
    public List<SysFunctionTenantDto> listAllOnlineMonthFunction() {
        return mapper(baseMapper.listAllOnlineMonthFunction());
    }
}
