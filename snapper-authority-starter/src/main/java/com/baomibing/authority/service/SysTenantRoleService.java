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


import com.baomibing.authority.dto.SysTenantRoleDto;
import com.baomibing.core.base.MBaseService;
import com.baomibing.core.common.SearchResult;

import java.util.List;
import java.util.Set;

public interface SysTenantRoleService extends MBaseService<SysTenantRoleDto> {

    /**
     * 查询租户角色列表
     * @param role
     * @param pageNumber
     * @param pageSize
     * @return
     */
    SearchResult<SysTenantRoleDto> search(SysTenantRoleDto role, int pageNumber, int pageSize);

    /**
     * 保存角色
     * @param role
     */
    void doSaveIt(SysTenantRoleDto role);

    /**
     * 更新角色
     * @param role
     */
    void doUpdateIt(SysTenantRoleDto role);

    /**
     * 通过角色名称获得角色信息
     *
     * @param tenantId 租户ID
     * @param roleName 角色名称
     * @return
     */
    List<SysTenantRoleDto> listByRoleName(String tenantId, String roleName);

    /**
     * 获取所有角色列表，注意本方法未过滤组织
     *
     * @param tenantId 租户ID
     * @return
     */
    List<SysTenantRoleDto> listAllRoles(String tenantId);

    /**
     * 启用
     *
     * @param ids 角色ID列表
     */
    void use(Set<String> ids);

    /**
     * 停用
     *
     * @param ids 角色ID列表
     */
    void stop(Set<String> ids);

    /**
     * 删除
     *
     * @param ids 角色ID列表
     */
    void deleteRoles(Set<String> ids, String tenantId);

    /**
     * 根据租户删除角色列表
     * @param tenantIds 租户ID列表
     */
    void deleteByTenant(Set<String> tenantIds);

    /**
     * 初始化
     * @param tenantId 租户ID
     */
    SysTenantRoleDto doInitRole(String tenantId);

    /**
     * 获取租户的根角色
     * @param tenantId 租户ID
     * @return
     */
    SysTenantRoleDto getRootRole(String tenantId);
}
