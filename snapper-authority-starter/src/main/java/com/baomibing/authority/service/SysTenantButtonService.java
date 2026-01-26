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


import com.baomibing.authority.dto.ButtonTenantDto;

import java.util.List;
import java.util.Set;

public interface SysTenantButtonService {


    /**
     * 删除租户及按钮ID对应的按钮列表
     * @param buttonIds
     * @param tenantId
     */
    void deleteButtonsByTenant(Set<String> buttonIds, String tenantId);
    /**
     * 根据菜单和租户查询所有可供授权的按钮列表
     * @param tenantId
     * @param menuId
     * @return
     */
    List<String> listByMenuForGrant(String tenantId, String menuId);

    /**
     * 根据租户id获取所有按钮（分配的）
     * @param tenantId
     * @return
     */
    List<ButtonTenantDto> listAllButtonsForGrant(String tenantId);

    /**
     * 根据租户和按钮进行保存
     * @param buttonIds
     * @param tenantId
     */
    void doSave(Set<String> buttonIds, String tenantId);

    /**
     * 根据按删除对应的关系
     * @param ids
     */
    void deleteByButtons(Set<String> ids);

}
