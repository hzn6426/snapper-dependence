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


import com.baomibing.authority.dto.SysTenantGroupDto;
import com.baomibing.authority.dto.SysTenantGroupEntrustDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;

public interface SysTenantGroupEntrustService extends MBaseService<SysTenantGroupEntrustDto> {

    /**
     * 根据用户ID和业务权限ID获取该用户委托的组织列表
     *
     * @param orgId  组织ID(用户当前选择组织)
     * @param userId 用户ID
     * @param permId 业务权ID
     * @return
     */
    List<SysTenantGroupDto> listEntrustGroupsByGroupAndUserAndPerm(String tenantId, String orgId, String userId, String permId);

    /**
     * 根据用户ID和业务权限ID删除该用户对于该功能委托的组织
     *
     * @param orgId  组织ID(用户当前选择组织)
     * @param userId 用户ID
     * @param permId 业务权限ID
     */
    void deleteByGroupAndUserAndPerm(String tenantId, String orgId, String userId, String permId);
}
