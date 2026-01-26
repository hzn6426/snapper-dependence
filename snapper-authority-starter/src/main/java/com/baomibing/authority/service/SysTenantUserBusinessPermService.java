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


import com.baomibing.authority.constant.enums.BusinessPermScopeEnum;
import com.baomibing.authority.dto.SysTenantUserBusinessPermDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

public interface SysTenantUserBusinessPermService extends MBaseService<SysTenantUserBusinessPermDto> {

    /**
     * 根据组织及用户及权限动作获取业务权限范围
     *
     * @param tenantId 租户ID
     * @param orgId  组织ID(用户当前选择组织)
     * @param userId 用户ID
     * @param action 业务权限
     * @return
     */
    BusinessPermScopeEnum getUserBusinessPermScopeByAction(String tenantId, String orgId, String userId, String action);

    /**
     * 根据组织及用户及请求的方法和URL获取业务权限范围
     *
     * @param tenantId 租户ID
     * @param orgId  组织ID(用户当前选择组织)
     * @param userId 用户ID
     * @param method 请求方法
     * @param url    请求URL
     * @return
     */
    BusinessPermScopeEnum getUserBusinessByMethodAndUrl(String tenantId, String orgId, String userId, String method, String url);

    /**
     * 根据组织及用户及业务权限ID获取业务权限范围
     *
     * @param tenantId 租户ID
     * @param orgId  组织ID(用户当前选择组织)
     * @param userId 用户ID
     * @param permId 业务权限ID
     * @return
     */
    BusinessPermScopeEnum getUserBusiness(String tenantId, String orgId, String userId, String permId);

    /**
     * 保存用户业务权限信息
     *
     * @param perm 用户业务权限
     */
    void saveUserBusinessPerm(SysTenantUserBusinessPermDto perm);

    /**
     * 根据组织及权限ID，用户ID获取用户委托的用户列表
     *
     * @param tenantId 租户ID
     * @param orgId  组织ID(用户当前选择组织)
     * @param userId 用户ID
     * @param permId 权限ID
     * @return
     */
    List<String> listUserEntrustIdsByPerm(String tenantId, String orgId, String userId, String permId);

    /**
     * 根据组织及权限ID,用户ID 获取用户委托的排除用户列表
     *
     * @param tenantId 租户ID
     * @param orgId 组织ID(用户当前选择组织)
     * @param userId 用户ID
     * @param permId 权限ID
     * @return
     */
    List<String> listUserExceptEntrustIdsByPerm(String tenantId, String orgId, String userId, String permId);

    /**
     * 根据组织及权限ID，用户ID获取用户委托的组织（部门）列表
     *
     * @param tenantId 租户ID
     * @param orgId  组织ID(用户当前选择组织)
     * @param userId 用户ID
     * @param permId 权限ID
     * @return
     */
    List<String> listGroupEntrustIdsByPerm(String tenantId, String orgId, String userId, String permId);

    /**
     * 根据组织及权限ID，用户ID获取用户委托的排除组织（部门）列表
     *
     * @param tenantId 租户ID
     * @param orgId  组织ID(用户当前选择组织)
     * @param userId 用户ID
     * @param permId 权限ID
     * @return
     */
    List<String> listGroupExceptEntrustIdsByPerm(String tenantId, String orgId, String userId, String permId);


    /**
     * 根据组织及用户ID列表删除对应的用户委托组织关系列表
     *
     * @param tenantId 租户ID
     * @param orgId 组织ID(用户当前选择组织)
     * @param uids  用户ID列表
     */
    void deleteByGroupAndUsers(String tenantId, String orgId, Set<String> uids);

    /**
     * 根据用户ID列表删除对应的用户委托组织关系列表
     *
     * @param uids 用户ID列表
     * @param tenantId 租户ID
     */
    void deleteByUsers(Set<String> uids, String tenantId);

    /**
     * 根据组织及用户和业务权限删除用户对应的业务权限
     *
     * @param tenantId 租户ID
     * @param orgId  组织ID(用户当前选择组织)
     * @param userId 用户ID
     * @param permId 权限ID
     */
    void deleteGroupUserPerm(String tenantId, String orgId, String userId, String permId);
}
