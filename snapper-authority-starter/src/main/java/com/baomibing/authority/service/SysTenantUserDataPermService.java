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


import com.baomibing.authority.dto.SysTenantUserDataPermDto;

public interface SysTenantUserDataPermService {

    /**
     * 保存用户数据权限
     * @param perm
     */
    void saveUserDataPerm(SysTenantUserDataPermDto perm);

    /**
     * 获取用户的数据权限
     * @param tenantId 租户ID
     * @param userId
     * @param orgId
     * @param permId
     */
    SysTenantUserDataPermDto getUserDataPerm(String tenantId, String userId, String orgId, String permId);
}
