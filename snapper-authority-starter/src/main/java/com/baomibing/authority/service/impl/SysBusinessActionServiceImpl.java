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

import com.alibaba.fastjson.JSONObject;
import com.baomibing.authority.action.BusinessActionAction;
import com.baomibing.authority.dto.BusinessActionDto;
import com.baomibing.authority.entity.SysBusinessAction;
import com.baomibing.authority.mapper.SysBusinessActionMapper;
import com.baomibing.authority.service.SysBusinessActionService;
import com.baomibing.authority.state.CommonState;
import com.baomibing.core.common.Assert;
import com.baomibing.core.common.SearchResult;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.core.process.StateWorkFlow;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static com.baomibing.tool.constant.RedisKeyConstant.KEY_ACTION_CONNECT_PREFIX;

/**
 * SysBusinessActionServiceImpl
 *
 * @author zening (316279828@qq.com) 2025/6/26 09:40
 * @version 1.0.0
 **/
@Service
public class SysBusinessActionServiceImpl extends MBaseServiceImpl<SysBusinessActionMapper, SysBusinessAction, BusinessActionDto> implements SysBusinessActionService {

    @Override
    public SearchResult<BusinessActionDto> search(BusinessActionDto v, int pageNumber, int pageSize) {
        LambdaQueryWrapper<SysBusinessAction> wrapper = lambdaQuery().like(Checker.beNotEmpty(v.getActionValue()), SysBusinessAction::getActionValue, v.getActionValue())
                .like(Checker.beNotEmpty(v.getConnectValue()), SysBusinessAction::getConnectValue, v.getConnectValue())
                .like(Checker.beNotEmpty(v.getConnectUserAuthColumn()), SysBusinessAction::getConnectUserAuthColumn, v.getConnectUserAuthColumn())
                .like(Checker.beNotEmpty(v.getConnectGroupAuthColumn()), SysBusinessAction::getConnectGroupAuthColumn, v.getConnectGroupAuthColumn())
                .eq(Checker.beNotEmpty(v.getState()), SysBusinessAction::getState, v.getState());
        return search(wrapper, pageNumber, pageSize);
    }

    @Override
    public void saveAction(BusinessActionDto action) {
        StateWorkFlow.doInitState( action);
        assertBeLock(action);
        super.saveIt(action);
    }

    @Override
    public void updateAction(BusinessActionDto action) {
        assertBeLock(action);
        super.updateIt(action);
    }

    @Override
    public void deleteActions(Set<String> ids) {
        List<BusinessActionDto> actions = gets(ids);
        for (BusinessActionDto action : actions) {
            assertBeLock(action);
        }
        super.deletes(ids);
    }

    private List<BusinessActionDto> listAllActions() {
        LambdaQueryWrapper<SysBusinessAction> wrapper = lambdaQuery();
        wrapper.eq(SysBusinessAction::getState, CommonState.ACTIVE);
        return mapper(baseMapper.selectList(wrapper));
    }


    private void refreshActionCache(List<BusinessActionDto> actions) {
        for (BusinessActionDto action : actions) {
            cacheService.set(KEY_ACTION_CONNECT_PREFIX + action.getActionValue() , JSONObject.toJSONString(action));
        }
    }

    @Override
    public void refreshAllActionCache() {
        cacheService.deleteByKeyPrefix(KEY_ACTION_CONNECT_PREFIX);
        List<BusinessActionDto> actions = listAllActions();
        refreshActionCache(actions);
    }

    @Override
    public void refreshTheActionsCache(Set<String> ids) {
        List<BusinessActionDto> actions = super.gets(ids);
        for (BusinessActionDto action : actions) {
            assertBeLock(action);
        }
        refreshActionCache(actions);
    }

    private void updateStateByIds(Set<String> ids, BusinessActionAction action) {
        Assert.CheckArgument(ids);
        Assert.CheckArgument(action);
        List<BusinessActionDto> actions = super.gets(ids);
        if (actions.size() != ids.size()) {
            throw new ServerRuntimeException(ExceptionEnum.IDS_CONTAINS_INVALID_ID);
        }
        for (BusinessActionDto a : actions) {
            assertBeLock(a);
            StateWorkFlow.doProcess(a, action);
        }
        super.updateItBatch(actions);
    }


    @Transactional
    @Override
    public void use(Set<String> ids) {
        updateStateByIds(ids, BusinessActionAction.USE);
    }

    @Transactional
    @Override
    public void stop(Set<String> ids) {
        updateStateByIds(ids, BusinessActionAction.STOP);
    }
}
