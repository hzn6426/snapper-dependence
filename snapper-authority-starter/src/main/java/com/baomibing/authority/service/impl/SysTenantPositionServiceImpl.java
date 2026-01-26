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

import com.baomibing.authority.action.TenantPositionAction;
import com.baomibing.authority.constant.enums.PositionPermScopeEnum;
import com.baomibing.authority.dto.SysTenantGroupDto;
import com.baomibing.authority.dto.SysTenantPositionDto;
import com.baomibing.authority.dto.SysTenantPositionGroupEntrustDto;
import com.baomibing.authority.dto.SysTenantPositionUserEntrustDto;
import com.baomibing.authority.entity.SysTenantPosition;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.mapper.SysTenantPositionMapper;
import com.baomibing.authority.service.*;
import com.baomibing.authority.state.PositionState;
import com.baomibing.core.common.Assert;
import com.baomibing.core.common.SearchResult;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.core.process.StateWorkFlow;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SysTenantPositionServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysTenantPositionServiceImpl extends MBaseServiceImpl<SysTenantPositionMapper, SysTenantPosition, SysTenantPositionDto> implements SysTenantPositionService {

    @Autowired
    private SysTenantUserPositionService userPositionService;
    @Autowired
    private SysTenantGroupService groupService;
    @Autowired
    private SysTenantPositionUserEntrustService puserEntrustService;
    @Autowired
    private SysTenantPositionGroupEntrustService pgroupEntrustService;
    @Autowired
    private SysTenantPositionRoleService positionRoleService;

    @Override
    public SearchResult<SysTenantPositionDto> search(SysTenantPositionDto v, int pageNumber, int pageSize) {
        doSetTenantId(v);
        if (Checker.beEmpty(v.getOrgId()) || Checker.beEmpty(v.getTenantId())) {
            return new SearchResult<>();
        }
        LambdaQueryWrapper<SysTenantPosition> wrapper = lambdaQuery().eq(SysTenantPosition::getOrgId, v.getOrgId()).eq(SysTenantPosition::getTenantId, v.getTenantId());
        return super.search(wrapper, pageNumber, pageSize);
    }

    @Override
    public List<SysTenantPositionDto> listPositionByGroup(String tenantId, String groupId) {
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        if (Checker.beEmpty(groupId) || Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }
        LambdaQueryWrapper<SysTenantPosition> wrapper = lambdaQuery();
        wrapper.eq(SysTenantPosition::getOrgId, groupId).eq(SysTenantPosition::getTenantId, tenantId);
        return mapper(super.baseMapper.selectList(wrapper));
    }

    @Override
    public List<SysTenantPositionDto> listActivePositionByGroup(String tenantId, String groupId) {
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        if (Checker.beEmpty(groupId) || Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }
        LambdaQueryWrapper<SysTenantPosition> wrapper = lambdaQuery();
        wrapper.eq(SysTenantPosition::getOrgId, groupId).eq(SysTenantPosition::getState, PositionState.ACTIVE).eq(SysTenantPosition::getTenantId, tenantId);
        return mapper(super.baseMapper.selectList(wrapper));
    }

    /**
     * 更新职位对应的委托信息
     *
     * @param positionId 职位ID
     * @param entrusts   委托列表（包含组织ID和用户ID）
     */
    private void doUpdateEntrust(String tenantId, String positionId, List<String> entrusts) {
        Assert.CheckArgument(positionId);
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        Assert.CheckArgument(tenantId);
        puserEntrustService.deleteByPosition(tenantId, positionId);
        pgroupEntrustService.deleteByPosition(tenantId, positionId);
        if (Checker.beEmpty(entrusts)) {
            return;
        }
        Set<String> userEntrusts = Sets.newHashSet();
        Set<String> groupEntrusts = Sets.newHashSet();
        entrusts.forEach(e -> {
            // 组织ID都是以R开头
            if (e.startsWith(Strings.R)) {
                groupEntrusts.add(e);
            } else {
                userEntrusts.add(e);
            }
        });
        List<SysTenantPositionUserEntrustDto> ulist = Lists.newArrayList();
        List<SysTenantPositionGroupEntrustDto> glist = Lists.newArrayList();
        final String tid = tenantId;
        userEntrusts.forEach(u -> ulist.add(new SysTenantPositionUserEntrustDto().setPositionId(positionId).setUserId(u).setTenantId(tid)));
        groupEntrusts.forEach(g -> glist.add(new SysTenantPositionGroupEntrustDto().setGroupEntrustId(g).setPositionId(positionId).setTenantId(tid)));
        if (Checker.beNotEmpty(ulist)) {
            puserEntrustService.saveItBatch(ulist);
        }
        if (Checker.beNotEmpty(glist)) {
            pgroupEntrustService.saveItBatch(glist);
        }
    }

    @Override
    public void savePosition(SysTenantPositionDto positionDto) {
        Assert.CheckArgument(positionDto);
        doSetTenantId(positionDto);
        Assert.CheckArgument(positionDto.getTenantId());
        doSetTenantId(positionDto);
        SysTenantGroupDto group = groupService.getIt(positionDto.getTenantId(), positionDto.getOrgId());
        if (Checker.beNull(group)) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.CANNOT_FIND_THE_ID_OF_GROUP,
                    positionDto.getOrgId());
        }
        StateWorkFlow.doInitState(positionDto);
        super.saveIt(positionDto);
        // 如果职位权限范围是自定义，对自定义进行处理
        if (PositionPermScopeEnum.CUSTOMER_SPECIFIED.name().equals(positionDto.getPermScope())) {
            doUpdateEntrust(positionDto.getTenantId(), positionDto.getId(), positionDto.getEntrusts());
        }
    }

    @Override
    public void updatePosition(SysTenantPositionDto positionDto) {
        Assert.CheckArgument(positionDto.getId());
        doSetTenantId(positionDto);
        Assert.CheckArgument(positionDto.getTenantId());
        updateIt(positionDto);
        // 如果职位权限范围是自定义，对自定义进行处理
        if (PositionPermScopeEnum.CUSTOMER_SPECIFIED.name().equals(positionDto.getPermScope())) {
            doUpdateEntrust(positionDto.getTenantId(), positionDto.getId(), positionDto.getEntrusts());
        }
    }

    private void doPositionState(List<String> ids, TenantPositionAction action) {
        Assert.CheckArgument(ids);
        Assert.CheckArgument(action);
        List<SysTenantPositionDto> positions = gets(new HashSet<>(ids));
        if (positions.size() != ids.size()) {
            throw new ServerRuntimeException(ExceptionEnum.IDS_CONTAINS_INVALID_ID);
        }
        for (SysTenantPositionDto position : positions) {
            StateWorkFlow.doProcess(position, action);
        }
        super.updateItBatch(positions);
    }

    @Override
    public SysTenantPositionDto getPosition(String id) {
        return getIt(id);
    }

    @Override
    public SysTenantPositionDto getManagerPositionByTenant(String tenantId) {
        Assert.CheckArgument(tenantId);
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        List<SysTenantPosition> positions = baseMapper.selectList(lambdaQuery().eq(SysTenantPosition::getTenantId, tenantId).eq(SysTenantPosition::getBeManager, true));
        return Checker.beEmpty(positions) ? null : mapper2v(positions.get(0));
    }

    @Override
    public void doUsePosition(List<String> ids) {
        doPositionState(ids, TenantPositionAction.USE);
    }

    @Override
    public void doStopPosition(List<String> ids) {
        doPositionState(ids, TenantPositionAction.STOP);
    }

    @Override
    public void deleteByGroups(Set<String> gids, String tenantId) {
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        Assert.CheckArgument(tenantId);
        Assert.CheckArgument(gids);
        List<SysTenantPosition> positions = baseMapper.selectList(lambdaQuery().in(SysTenantPosition::getOrgId, gids).eq(SysTenantPosition::getTenantId, tenantId));
        if (Checker.beEmpty(positions)) {
            return;
        }
        Set<String> pids = positions.stream().map(SysTenantPosition::getId).collect(Collectors.toSet());
        baseMapper.deleteBatchIds(pids);
        userPositionService.deleteByPositions(pids, tenantId);
    }

    @Override
    public void deletePositions(Set<String> pids, String tenantId) {
        Assert.CheckArgument(pids);
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        Assert.CheckArgument(tenantId);
        userPositionService.deleteByPositions(pids, tenantId);
        positionRoleService.deleteByPositions(tenantId, pids);
        puserEntrustService.deleteByPositions(tenantId, pids);
        pgroupEntrustService.deleteByPositions(tenantId, pids);
        deletes(pids);
    }

    @Override
    public void deleteByTenant(Set<String> tenantIds) {
        Assert.CheckArgument(tenantIds);
        userPositionService.deleteByTenant(tenantIds);
        baseMapper.delete(lambdaQuery().in(SysTenantPosition::getTenantId, tenantIds));
    }

    @Override
    public List<SysTenantPositionDto> listAllGroupPositions(String tenantId) {
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        if (Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }
        return mapper(this.baseMapper.selectList(lambdaQuery().isNotNull(SysTenantPosition::getOrgId).eq(SysTenantPosition::getTenantId, tenantId)));
    }
}
