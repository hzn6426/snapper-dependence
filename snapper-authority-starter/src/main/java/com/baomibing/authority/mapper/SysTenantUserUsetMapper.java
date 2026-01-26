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

package com.baomibing.authority.mapper;

import com.baomibing.authority.entity.SysTenantUser;
import com.baomibing.authority.entity.SysTenantUserUset;
import com.baomibing.authority.entity.SysTenantUset;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysTenantUserUsetMapper extends BaseMapper<SysTenantUserUset> {

    /**
     * 根据用户组ID获取用户对应的组织及用户信息
     *
     * @param tenantId 租户ID
     * @param usetId 用户组ID
     * @return
     */
    List<SysTenantUser> listGroupUsersByUset(@Param("tenantId") String tenantId, @Param("usetId") String usetId);

    /**
     * 根据组织和用户列出用户组列表
     *
     * @param tenantId 租户ID
     * @param orgId  组织ID
     * @param userId 用户ID
     * @return
     */
    List<SysTenantUset> listUsetsByGroupAndUser(@Param("tenantId") String tenantId, @Param("orgId") String orgId, @Param("userId") String userId);
}
