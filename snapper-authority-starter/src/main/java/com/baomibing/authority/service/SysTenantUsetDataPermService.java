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


import com.baomibing.authority.dto.SysTenantUsetDataPermDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

public interface SysTenantUsetDataPermService extends MBaseService<SysTenantUsetDataPermDto> {

    /**
     * 根据用户组获取对应的数据权限
     * @param tenantId 租户ID
     * @param usetId 用户组ID
     * @param permId 权限ID
     * @return
     */
    SysTenantUsetDataPermDto getUsetDataPerm(String tenantId, String usetId, String permId);

    /**
     * 根据用户组列表获取对应的数据权限
     * @param usetIds 用户组ID列表
     * @param permId 权限ID
     * @param tenantId 租户ID
     * @return
     */
    List<SysTenantUsetDataPermDto> listUsetDataPerm(Set<String> usetIds, String permId, String tenantId);

    /**
     * 保存用户组数据权限
     * @param perm
     */
    void saveUserDataPerm(SysTenantUsetDataPermDto perm);
}
