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

import com.baomibing.authority.entity.SysTenantUserBusinessPerm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

public interface SysTenantUserBusinessPermMapper extends BaseMapper<SysTenantUserBusinessPerm> {

    /**
     * 根据用户及请求的方法和URL获取业务权限范围
     *
     * @param orgId  组织ID(用户当前选择组织)
     * @param userId 用户ID
     * @param method 请求方法
     * @param url    请求URL
     * @return
     */
    String getUserBusinessPermScopeByMethodAndUrl(@Param("tenantId") String tenantId, @Param("orgId") String orgId, @Param("userId") String userId,
                                                  @Param("method") String method, @Param("url") String url);

    /**
     * 根据用户及权限动作Action获取业务权限范围
     *
     * @param orgId  组织ID(用户当前选择组织)
     * @param userId 用户ID
     * @param action 业务权限动作
     * @return
     */
    String getUserBusinessPermScopeByAction(@Param("tenantId") String tenantId, @Param("orgId") String orgId, @Param("userId") String userId, @Param("action") String action);
}
