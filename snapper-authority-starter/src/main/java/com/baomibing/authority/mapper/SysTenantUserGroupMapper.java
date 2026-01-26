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

import com.baomibing.authority.entity.SysTenantUserGroup;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * SysTenantUserGroupService
 *
 * @author zening 2024/7/16 下午4:27
 * @version 1.0.0
 **/
public interface SysTenantUserGroupMapper extends BaseMapper<SysTenantUserGroup> {
    /**
     * 根据用户ID查询用户组织列表
     *
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @return
     */
    List<SysTenantUserGroup> listByUser(@Param("tenantId") String tenantId, @Param("userId") String userId);

    /**
     * 根据组织ID查询用户组织列表
     *
     * @param tenantId 租户ID
     * @param groupId 组织ID
     * @return
     */
    List<SysTenantUserGroup> listItAndChilldByGroup(@Param("tenantId") String tenantId, @Param("groupId") String groupId);

    /**
     * 用户编号获取组织id
     *
     * @param tenantId 租户ID
     * @param userNo
     * @Return: java.util.List<java.lang.String>
     */
    List<String> listGroupIdByUserNo(@Param("tenantId") String tenantId, @Param("userNo") String userNo);
}
