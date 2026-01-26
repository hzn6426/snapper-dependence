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


import com.baomibing.authority.dto.SysTenantPositionRoleDto;
import com.baomibing.authority.dto.SysTenantRoleDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

public interface SysTenantPositionRoleService extends MBaseService<SysTenantPositionRoleDto> {

    /**
     * 根据职位ID获取角色列表
     *
     * @param tenantId 租户ID
     * @param positionId
     * @return
     */
    List<SysTenantRoleDto> listRolesByPosition(String tenantId, String positionId);

    /**
     * 保存职位角色（多个角色）
     *
     * @param tenantId 租户ID
     * @param positionId 职位ID
     * @param roles      角色ID列表
     */
    void savePositionRoles(String tenantId, String positionId, Set<String> roles);

    /**
     * 根据职位删除对应的职位角色关系
     *
     * @param tenantId 租户ID
     * @param positionId 职位ID
     *
     */
    void deleteByPosition(String tenantId, String positionId);

    /**
     * 根据职位ID列表删除职位角色关系
     * @param tenantId 租户ID
     * @param positionIds 职位ID列表
     */
    void deleteByPositions(String tenantId, Set<String> positionIds);

    /**
     * 根据角色ID列表删除角色职位关联
     *
     * @param roles 角色ID列表
     * @param tenantId 租户ID
     */
    void deleteByRoles(Set<String> roles, String tenantId);

    /**
     * 根据租户ID列表删除对应的职位角色关联
     * @param tenantIds 租户ID列表
     */
    void deleteByTenant(Set<String> tenantIds);
}
