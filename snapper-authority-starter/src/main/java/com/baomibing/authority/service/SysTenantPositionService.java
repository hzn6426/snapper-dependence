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

package com.baomibing.authority.service;


import com.baomibing.authority.dto.SysTenantPositionDto;
import com.baomibing.core.base.MBaseService;
import com.baomibing.core.common.SearchResult;

import java.util.List;
import java.util.Set;

/**
 * SysTenantPositionService
 *
 * @author zening
 * @version 1.0.0
 **/
public interface SysTenantPositionService extends MBaseService<SysTenantPositionDto> {

    SearchResult<SysTenantPositionDto> search(SysTenantPositionDto v, int pageNumber, int pageSize);
    /**
     * 组织职位列表查询
     *
     * @param groupId
     * @Return: com.baomibing.core.common.SearchResult<com.baomibing.authority.dto.PositionDto>
     */
    List<SysTenantPositionDto> listPositionByGroup(String tenantId, String groupId);

    /**
     * 组织生效职位列表查询
     *
     * @param groupId
     * @Return: com.baomibing.core.common.SearchResult<com.baomibing.authority.dto.PositionDto>
     */
    List<SysTenantPositionDto> listActivePositionByGroup(String tenantId, String groupId);


    /**
     * 职位新增
     *
     * @param positionDto
     * @Return: void
     */
    void savePosition(SysTenantPositionDto positionDto);

    /**
     * 职位修改
     *
     * @param positionDto
     * @Return: void
     */
    void updatePosition(SysTenantPositionDto positionDto);

    /**
     * 职位明细
     *
     * @param id
     * @Return: com.baomibing.authority.dto.PositionDto
     */
    SysTenantPositionDto getPosition(String id);

    /**
     * 根据租户获取管理职位
     * @param tenantId 租户ID
     * @return
     */
    SysTenantPositionDto getManagerPositionByTenant(String tenantId);
    /**
     * 职位启用
     *
     * @param ids
     * @Return: void
     */
    void doUsePosition(List<String> ids);

    /**
     * 职位停用
     *
     * @param ids
     * @Return: void
     */
    void doStopPosition(List<String> ids);

    /**
     * 删除组织下的职位
     *
     * @param gids
     * @param tenantId 租户ID
     * @Return: void
     */
    void deleteByGroups(Set<String> gids, String tenantId);

    /**
     * 根据职位ID列表删除对应的职位
     *
     * @param pids 职位ID
     * @param tenantId 租户ID
     */
    void deletePositions(Set<String> pids, String tenantId);

    /**
     * 根据租户删除租户下的所有职位
     * @param tenantIds 租户ID列表
     */
    void deleteByTenant(Set<String> tenantIds);

    /**
     * 获取所有组织中的职位
     *
     * @return
     */
    List<SysTenantPositionDto> listAllGroupPositions(String tenantId);
}
