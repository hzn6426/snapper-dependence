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
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * SysTenantUserService
 *
 * @author zening 2024/7/16 下午4:02
 * @version 1.0.0
 **/
public interface SysTenantUserMapper extends BaseMapper<SysTenantUser> {


    List<SysTenantUser> listAllGroupUsers(String tenantId);

    List<SysTenantUser> searchByCondition(@Param("tenantId") String tenantId, @Param("userNo") String userNo, @Param("userRealCnName") String userRealCnName,
          @Param("userEmail") String userEmail, @Param("state") String state,
          @Param("roleId") String roleId, @Param("userTags") List<String> userTags, @Param("groupId") String groupId,
          @Param("limit") int limit, @Param("offset") int offset);

    int countByCondition(@Param("tenantId") String tenantId, @Param("userNo") String userNo, @Param("userRealCnName") String userRealCnName,
         @Param("userEmail") String userEmail, @Param("state") String state,
         @Param("roleId") String roleId, @Param("userTags") List<String> userTags,@Param("groupId") String groupId);

    /**
     * 根据组织获取用户信息
     *
     * @param groupId 组织ID
     * @param limit   偏移量
     * @param offset  当前页数
     * @return
     */
    List<SysTenantUser> searchByGroupCondition(@Param("groupId") String groupId, @Param("tenantId") String tenantId, @Param("limit") int limit, @Param("offset") int offset);

    /**
     * 根据组织获取用户信息的数量
     *
     * @param groupId 组织ID
     * @return
     */
    int countByGroupCondition(@Param("groupId") String groupId, @Param("tenantId") String tenantId);

    /**
     * 获取未分配组织的用户列表
     *
     * @param limit
     * @param offset
     * @return
     */
    List<SysTenantUser> listForNotAssignGroup(@Param("tenantId") String tenantId, @Param("limit") int limit, @Param("offset") int offset);

    /**
     * 获取未分配组织的用户条数
     *
     * @return
     */
    int countForNotAssignGroup(@Param("tenantId") String tenantId);

    /**
     * 通过用户标签和关键字来匹配
     *
     * @param tenantId 租户ID
     * @param userTag 用户标签
     * @param KeyWord 关键字
     * @Return: java.util.List<com.baomibing.authority.entity.SysUser>
     */
    List<SysTenantUser> listByKeyWordAndType(@Param("tenantId") String tenantId, @Param("userTag") String userTag, @Param("KeyWord") String KeyWord);

    /**
     * 获取所有建立组织关系的对应TAG的成员列表
     *
     * @return java.util.List<com.baomibing.authority.entity.SysTenantUser>
     */
    List<SysTenantUser> listAllGroupUsersByTag(@Param("tenantId") String tenantId, @Param("userTag") String userTag);
}
