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


import com.baomibing.authority.dto.MenuTenantDto;
import com.baomibing.authority.dto.SysTenantMenuDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

public interface SysTenantMenuService extends MBaseService<SysTenantMenuDto> {

    void doSave(Set<String> menuIds, String tenantId);

    List<MenuTenantDto> listAllMenusForGrant(String tenantId);

    List<String> listAllMenus(String tenantId);


    /**
     * 删除菜单ID对应的数据
     * @param menuIds 菜单ID列表
     */
    void deleteByMenus(Set<String> menuIds);

    /**
     * 删除租户对应的菜单列表
     * @param tenantId 租户ID
     */
    void deleteByTenant(String tenantId);
}
