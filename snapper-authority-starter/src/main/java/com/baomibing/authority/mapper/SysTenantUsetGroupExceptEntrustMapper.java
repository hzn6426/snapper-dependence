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

import com.baomibing.authority.entity.SysTenantGroup;
import com.baomibing.authority.entity.SysTenantUsetGroupExceptEntrust;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface SysTenantUsetGroupExceptEntrustMapper extends BaseMapper<SysTenantUsetGroupExceptEntrust> {

    /**
     * 根据用户组和权限获取改用组对应权限的组织委托列表
     * @param usetIds 用户组ID列表
     * @param permId 权限ID
     * @param tenantId 租户ID
     * @return
     */
    List<SysTenantGroup> listEntrustGroupsByUsetAndPerm(@Param("usetIds") Set<String> usetIds, @Param("permId") String permId, @Param("tenantId") String tenantId);

    /**
     * 删除用户组和权限关联的组织委托信息
     * @param tenantId 租户ID
     * @param usetId 用户组ID
     * @param permId 权限ID
     */
    void deleteByUsetAndPerm(@Param("tenantId") String tenantId, @Param("usetId") String usetId, @Param("permId") String permId);
}
