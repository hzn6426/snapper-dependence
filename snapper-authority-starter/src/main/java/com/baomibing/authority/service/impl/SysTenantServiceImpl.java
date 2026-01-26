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

import com.baomibing.authority.action.TenantAction;
import com.baomibing.authority.constant.enums.FunctionFeeTypeEnum;
import com.baomibing.authority.constant.enums.PositionPermScopeEnum;
import com.baomibing.authority.dto.*;
import com.baomibing.authority.entity.SysTenant;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.mapper.SysTenantMapper;
import com.baomibing.authority.service.*;
import com.baomibing.core.common.Assert;
import com.baomibing.core.common.SearchResult;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.core.process.StateWorkFlow;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.constant.TenantRedisKeyConstant;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ObjectUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SysTenantServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service @Slf4j
public class SysTenantServiceImpl extends MBaseServiceImpl<SysTenantMapper, SysTenant, SysTenantDto> implements SysTenantService {

    @Autowired private SysTenantMenuService tenantMenuService;
    @Autowired private SysTenantButtonService tenantButtonService;
    @Autowired private SysTenantRoleResourceService tenantRoleResourceService;
    @Autowired private SysTenantGroupService tenantGroupService;
    @Autowired private SysTenantUserService tenantUserService;
    @Autowired private SysTenantRoleService tenantRoleService;
    @Autowired private SysTenantUserRoleService tenantUserRoleService;
    @Autowired private SysTenantUserGroupService tenantUserGroupService;
    @Autowired private SysTenantFeeService tenantFeeService;
    @Autowired private SysTenantUsetService tenantUsetService;
    @Autowired private SysTenantPositionService tenantPositionService;
    @Autowired private SysTenantPositionGroupEntrustService tenantPositionGroupEntrustService;
    @Autowired private SysTenantUserPositionService tenantUserPositionService;


    @Override
    public void doSave(SysTenantDto tenant) {
        StateWorkFlow.doInitState(tenant);
        super.saveIt(tenant);
    }

    @Override
    public void doUpdate(SysTenantDto tenant) {
        super.updateIt(tenant);
    }

    @Override
    public void deleteByIds(Set<String> ids) {
        //删除组织
        tenantGroupService.deleteByTenant(ids);
        tenantRoleService.deleteByTenant(ids);
        tenantUserService.deleteByTenant(ids);
        tenantUsetService.deleteByTenant(ids);
        tenantPositionService.deleteByTenant(ids);
        super.deletes(ids);
    }

    private void updateStateByIds(Set<String> ids, TenantAction action) {
        Assert.CheckArgument(ids);
        Assert.CheckArgument(action);
        List<SysTenantDto> tenants = super.gets(ids);
        if (tenants.size() != ids.size()) {
            throw new ServerRuntimeException(ExceptionEnum.IDS_CONTAINS_INVALID_ID);
        }
        for (SysTenantDto t : tenants) {
            StateWorkFlow.doProcess(t, action);
        }
        super.updateItBatch(tenants);
    }

    @Override
    public void lockTenants(Set<String> ids) {
        updateStateByIds(ids, TenantAction.LOCK);
    }

    @Override
    public void unlockTenants(Set<String> ids) {
        updateStateByIds(ids, TenantAction.UNLOCK);
    }

    @Override
    public SearchResult<SysTenantDto> search(SysTenantDto tenant, int pageNumber, int pageSize) {
        if (Checker.beNull(tenant)) {
            return new SearchResult<>();
        }
        int count = baseMapper.countTenantByCondition(tenant.getName(), tenant.getState());
        if (count == 0) {
            return new SearchResult<>(count, emptyList());
        }
        int offset = offset(pageNumber,pageSize);
        List<SysTenant> list = baseMapper.listTenantByCondition(tenant.getName(), tenant.getState(), pageSize, offset);
        return new SearchResult<>(count, mapper(list));
    }

    private void doInitTenant(String tenantId, Boolean beCreateGroup) {
        Assert.CheckArgument(tenantId);
        SysTenantGroupDto group = tenantGroupService.getRootGroup(tenantId);
        if (Checker.beNull(group)) {
            group = tenantGroupService.doInitGroup(tenantId);
        }
        SysTenantGroupDto company = tenantGroupService.doGetOrMakeCompany(tenantId);
        SysTenantRoleDto role = tenantRoleService.getRootRole(tenantId);
        if (Checker.beNull(role)) {
            role = tenantRoleService.doInitRole(tenantId);
        }

        boolean beHaveUser = false;
        SysTenantUserDto user = tenantUserService.getSuper(tenantId);
        if (Checker.beNull(user)) {
            user = tenantUserService.doInitUser(tenantId);
        } else {
            beHaveUser = true;
        }

        if (!beHaveUser || beCreateGroup) {
            SysTenantUserGroupDto ug = new SysTenantUserGroupDto();
            ug.setUserId(user.getId()).setGroupId(company.getId()).setTenantId(tenantId);
            tenantUserGroupService.saveIt(ug);
            SysTenantUserRoleDto ur = new SysTenantUserRoleDto();
            ur.setOrgId(company.getId()).setTenantId(tenantId).setUserId(user.getId()).setRoleId(role.getId());
            tenantUserRoleService.saveIt(ur);
        }
        //获取职位
        SysTenantPositionDto position = tenantPositionService.getManagerPositionByTenant(tenantId);
        if (Checker.beNull(position)) {
            position = new SysTenantPositionDto().setBeManager(Boolean.TRUE).setTenantId(tenantId).setOrgId(company.getId()).setPostName("公司超管").setPermScope(PositionPermScopeEnum.CUSTOMER_SPECIFIED.name());
            tenantPositionService.savePosition(position);
            SysTenantPositionGroupEntrustDto entrust = new SysTenantPositionGroupEntrustDto().setTenantId(tenantId).setPositionId(position.getId()).setGroupEntrustId(group.getId());
            tenantPositionGroupEntrustService.saveIt(entrust);
        }

        tenantUserPositionService.deleteByPositions(Sets.newHashSet(position.getId()), tenantId);
        SysTenantUserPositionDto userPosition = new SysTenantUserPositionDto().setTenantId(tenantId).setUserId(user.getId()).setPositionId(position.getId());
        tenantUserPositionService.saveIt(userPosition);

        List<String> mids = tenantMenuService.listAllMenus(tenantId);
        tenantRoleResourceService.saveMenusPermsByRole(tenantId, role.getId(), Sets.newHashSet(mids));

        List<ButtonTenantDto> buttons = tenantButtonService.listAllButtonsForGrant(tenantId);
        Set<String> bids = buttons.stream().map(ButtonTenantDto::getId).collect(Collectors.toSet());
        tenantRoleResourceService.saveButtonPermsByRole(tenantId, role.getId(), bids);
    }

    @Override
    public void doInitSuperUser(String tenantId) {
        doInitTenant(tenantId, false);
//        Assert.CheckArgument(tenantId);
////        if (tenantUserService.beExistSuper(tenantId)) {
////            throw new ServerRuntimeException(ExceptionEnum.TENANT_SUPER_USER_HAS_EXIST);
////        }
//        SysTenantGroupDto group = tenantGroupService.getRootGroup(tenantId);
//        if (Checker.beNull(group)) {
//            group = tenantGroupService.doInitGroup(tenantId);
//        }
//        SysTenantGroupDto company = tenantGroupService.doGetOrMakeCompany(tenantId);
//        SysTenantRoleDto role = tenantRoleService.getRootRole(tenantId);
//        if (Checker.beNull(role)) {
//            role = tenantRoleService.doInitRole(tenantId);
//        }
//
//        boolean beHaveUser = false;
//        SysTenantUserDto user = tenantUserService.getSuper(tenantId);
//        if (Checker.beNull(user)) {
//            user = tenantUserService.doInitUser(tenantId);
//        } else {
//            beHaveUser = true;
//        }
//
//        if (!beHaveUser) {
//            SysTenantUserGroupDto ug = new SysTenantUserGroupDto();
//            ug.setUserId(user.getId()).setGroupId(company.getId()).setTenantId(tenantId);
//            tenantUserGroupService.saveIt(ug);
//            SysTenantUserRoleDto ur = new SysTenantUserRoleDto();
//            ur.setOrgId(company.getId()).setTenantId(tenantId).setUserId(user.getId()).setRoleId(role.getId());
//            tenantUserRoleService.saveIt(ur);
//        }
//
//        List<String> mids = tenantMenuService.listAllMenus(tenantId);
//        tenantRoleResourceService.saveMenusPermsByRole(tenantId, role.getId(), Sets.newHashSet(mids));
//
//        List<ButtonTenantDto> buttons = tenantButtonService.listAllButtonsForGrant(tenantId);
//        Set<String> bids = buttons.stream().map(ButtonTenantDto::getId).collect(Collectors.toSet());
//        tenantRoleResourceService.saveButtonPermsByRole(tenantId, role.getId(), bids);

    }

    @Override
    public void doAssignMenus(String tenantId, Set<String> menuIds) {
        Assert.CheckArgument(tenantId);
        Assert.CheckArgument(menuIds);
//        List<MenuTenantDto> menus =  tenantMenuService.listAllMenusForGrant(tenantId);
        tenantMenuService.deleteByTenant(tenantId);

//        List<MenuDto> menuList = menuService.gets(menuIds);
//        Set<String> parentIds = menuList.stream().map(MenuDto::getParent).collect(Collectors.toSet());
//        List<String> allParentMenuIds = menuService.listAllParentByIds(com.google.common.collect.Sets.newHashSet(parentIds));
//        Set<String> allParents = allParentMenuIds.stream().flatMap(t -> Splitter.on(",").splitToStream(t))
//                .filter(Checker::beNotEmpty).collect(Collectors.toSet());
//        menuIds.addAll(allParents);
        tenantMenuService.doSave(menuIds, tenantId);
        //删除角色中的对应的菜单分配信息
//        if (Checker.beNotEmpty(toDelList)) {
//            tenantRoleResourceService.deleteByResources(Sets.newHashSet(toDelList), ResourceTypeEnum.MENU, tenantId);
//        }
    }

    @Override
    public void doAssignButtons(String tenantId, String menuId, Set<String> buttonIds) {
        Assert.CheckArgument(tenantId);
        Assert.CheckArgument(menuId);
        List<String> dbIds =  tenantButtonService.listByMenuForGrant(tenantId, menuId);
//        List<String> dbIds = buttons.stream().map(ButtonTenantDto::getId).collect(Collectors.toList());
        List<String> toDelList = ListUtils.subtract(dbIds, Lists.newArrayList(buttonIds));
        List<String> toAddList = ListUtils.subtract(Lists.newArrayList(buttonIds), dbIds);
        if (Checker.beNotEmpty(toAddList)) {
            tenantButtonService.doSave(Sets.newHashSet(toAddList), tenantId);
        }
        //删除角色中的对应的按钮分配信息
        if (Checker.beNotEmpty(toDelList)) {
            tenantButtonService.deleteButtonsByTenant(Sets.newHashSet(toDelList), tenantId);
        }
    }

    @Override
    public List<SysTenantDto> listByKeyWord(String keyWord) {
        List<SysTenant> list = baseMapper.listByKeyWord(keyWord);
        return mapper(list);
    }

    private void doInnerChargeMoney(String tenantId, BigDecimal money) {
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);

        if (money.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.INVALID_CHARGE_MONEY);
        }

        String key = MessageFormat.format(TenantRedisKeyConstant.KEY_TENANT_CHARGE_LOCK, tenantId);
        //lock for charge
        while (!cacheService.setNxPx(key, "LOCK", 3000)) {}
        SysTenantDto tenant = super.getIt(tenantId);
        if (Checker.beNull(tenant)) {
            throw new ServerRuntimeException(ExceptionEnum.CAN_NOT_FIND_ID_OF_TENANT, tenantId);
        }
        BigDecimal balance = tenant.getBalance();
        if (Checker.beNull(balance)) {
            balance = BigDecimal.ZERO;
        }
        BigDecimal leftMoney = balance.add(money).setScale(2, RoundingMode.HALF_UP);
        log.info("********************* SysTenantServiceImpl say(CHARGE): tenant balance money :{}, charge money :{}, left money: {}", balance.toPlainString(), money.toPlainString(), leftMoney.toPlainString());
        tenant.setBalance(leftMoney);
        super.updateIt(tenant);
        cacheService.del(key);
    }

    @Override
    public void doChargeMoney(String tenantId, BigDecimal money) {
        Assert.CheckArgument(tenantId);
        Assert.CheckArgument(money);
        doInnerChargeMoney(tenantId, money);
        //记录充值记录
        SysTenantFeeDto fee = new SysTenantFeeDto();
        fee.setTenantId(tenantId).setFeeType(FunctionFeeTypeEnum.CHARGE.name()).setFunctionId(null).setExchangeName("在线充值").setExchangeTime(new Date())
                .setIpAddress(currentReqIp()).setRequestQty(1).setUnitPrice(money).setTotalPrice(money).setOs(currentOs()).setBrowser(currentBrowser()).setAfterMoney(super.getIt(tenantId).getBalance());
        tenantFeeService.saveFeeAsync(fee);
    }

    @Override
    public void doRecoveryMoney(String tenantId, BigDecimal money) {
        Assert.CheckArgument(tenantId);
        Assert.CheckArgument(money);
        doInnerChargeMoney(tenantId, money);
    }

    @Override
    public void doConsumeMoney(String tenantId, BigDecimal money) {
        Assert.CheckArgument(tenantId);
        Assert.CheckArgument(money);
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);

        if (money.compareTo(BigDecimal.ZERO) < 0) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.INVALID_CHARGE_MONEY);
        }
        String key = MessageFormat.format(TenantRedisKeyConstant.KEY_TENANT_CHARGE_LOCK, tenantId);
        //lock for consume
        while (!cacheService.setNxPx(key, "LOCK", 3000)) {}
        SysTenantDto tenant = super.getIt(tenantId);
        if (Checker.beNull(tenant)) {
            throw new ServerRuntimeException(ExceptionEnum.CAN_NOT_FIND_ID_OF_TENANT, tenantId);
        }
        BigDecimal balance = tenant.getBalance();
        if (Checker.beNull(balance)) {
            balance = BigDecimal.ZERO;
        }
        BigDecimal leftMoney = balance.subtract(money).setScale(2, RoundingMode.HALF_UP);
        if (leftMoney.compareTo(BigDecimal.ZERO) < 0) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.TENANT_ACCOUNT_NOT_HAVE_ENOUGH_MONEY);
        }
        log.info("********************* SysTenantServiceImpl say(CONSUME): tenant balance money :{}, consume money :{}, left money: {}", balance.toPlainString(), money.toPlainString(), leftMoney.toPlainString());
        tenant.setBalance(leftMoney);
        super.updateIt(tenant);
        cacheService.del(key);
    }

    private void doProcess(SysTenantDto tenant, TenantAction action) {
        StateWorkFlow.doProcess(tenant, action);
        tenant.setScore(null).setTenantRank(null).setBalance(null);
//        super.updateIt(tenant);
    }

    @Override
    public void doCommit(SysTenantDto tenant) {
        Assert.CheckArgument(tenant);
        doSetTenantId(tenant);
        doProcess(tenant, TenantAction.COMMIT);
    }

    @Override
    public void doApprove(String tenantId) {
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        SysTenantDto tenant = super.getIt(tenantId);
        if (Checker.beNull(tenant)) {
            throw new ServerRuntimeException(ExceptionEnum.CAN_NOT_FIND_ID_OF_TENANT, tenantId);
        }
        doProcess(tenant, TenantAction.APPROVE);
        doInitTenant(tenantId, true);
        super.updateIt(tenant);
    }

    @Override
    public void doReject(String tenantId) {
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        SysTenantDto tenant = super.getIt(tenantId);
        if (Checker.beNull(tenant)) {
            throw new ServerRuntimeException(ExceptionEnum.CAN_NOT_FIND_ID_OF_TENANT, tenantId);
        }
        doProcess(tenant, TenantAction.REJECT);
        super.updateIt(tenant);
    }

    @Override
    public void doCancel(String tenantId) {
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        SysTenantDto tenant = super.getIt(tenantId);
        if (Checker.beNull(tenant)) {
            throw new ServerRuntimeException(ExceptionEnum.CAN_NOT_FIND_ID_OF_TENANT, tenantId);
        }
        doProcess(tenant, TenantAction.CANCEL);
        super.updateIt(tenant);
    }
}
