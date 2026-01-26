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


import com.baomibing.authority.dto.SysTenantUserDto;
import com.baomibing.core.base.MBaseService;
import com.baomibing.core.common.SearchResult;

import java.util.List;
import java.util.Set;

/**
 * SysTenantUserService
 *
 * @author zening
 * @version 1.0.0
 **/
public interface SysTenantUserService extends MBaseService<SysTenantUserDto> {


    /**
     * 查询用户
     * @param user
     * @param pageNumber
     * @param pageSize
     * @return
     */
    SearchResult<SysTenantUserDto> search(SysTenantUserDto user, int pageNumber, int pageSize);

    /**
     * 根据用户工号获取用户信息
     *
     * @param userNo 工号
     * @return 用户对象信息
     */
    SysTenantUserDto getByUserNo( String userNo);


    /**
     * 根据用户ID列表删除用户信息
     *
     * @param uids 用户ID列表
     */
    void deleteByIds(Set<String> uids, String tenantId);

    /**
     * 根据租户ID列表删除用户信息
     * @param tenantIds 租户ID列表
     */
    void deleteByTenant(Set<String> tenantIds);

    /**
     * 更新用户的密钥
     *
     * @param userId 用户ID
     * @param secret 密钥
     */
    void updateUserSecret(String userId, String secret);

    /**
     * 保存用户
     *
     * @param user 待保存的用户
     */
    void doSave(SysTenantUserDto user);

    /**
     * 更新用户
     *
     * @param user 待更新的用户
     */
    void doUpdate(SysTenantUserDto user);

    /**
     * 修改用户密码
     *
     * @param user 待修改的用户
     */
    void updatePassword(SysTenantUserDto user);


    /**
     * 激活ID列表的用户
     *
     * @param ids 用户的ID列表
     */
    void activeUsers(Set<String> ids);

    /**
     * 所用ID列表的用户
     *
     * @param ids 用户的ID列表
     */
    void lockUsers(Set<String> ids);

    /**
     * 解锁ID列表的用户
     *
     * @param ids 用户的ID列表
     */
    void unlockUsers(Set<String> ids);

    /**
     * 停用ID列表的用户
     *
     * @param ids 用户的ID列表
     */
    void stopUsers(Set<String> ids);

    /**
     * 取消停用ID列表的用户
     *
     * @param ids 用户的ID列表
     */
    void unstopUsers(Set<String> ids);

    /**
     * 重置用户密码-发送邮件
     *
     * @param ids 用户的ID列表
     */
    void resetPasswd(Set<String> ids);

    /**
     * 获取租户对应的所有组织用户
     * @param tenantId
     * @return
     */
    List<SysTenantUserDto> listAllGroupUsers(String tenantId);

    /**
     * 通过组织条件获取用户查询结果信息
     *
     * @param user       用户组织
     * @param pageNumber 页码
     * @param pageSize   页数据条数
     * @return
     */
    SearchResult<SysTenantUserDto> searchByGroupCondition(SysTenantUserDto user,  int pageNumber, int pageSize);

    /**
     * 查询未分配组织的用户信息
     *
     * @param user
     * @param pageNumber
     * @param pageSize
     * @return
     */
    SearchResult<SysTenantUserDto> searchForNotAssignGroup(SysTenantUserDto user, int pageNumber, int pageSize);

    /**
     * 根据租户ID初始化租户用户
     * @param tenantId 租户ID
     * @return
     */
    SysTenantUserDto doInitUser(String tenantId);

    /**
     * 是否已经初始化超级管理员
     * @param tenantId 租户ID
     * @return
     */
    SysTenantUserDto getSuper(String tenantId);

    /**
     * 通过用户标签和关键字来匹配
     *
     * @param tenantId
     * @param userTag
     * @param KeyWord
     * @Return: java.util.List<com.baomibing.authority.dto.UserDto>
     */
    List<SysTenantUserDto> listByKeyWordAndType(String tenantId, String userTag, String KeyWord);

    /**
     * 获取所有建立用户组织关系的对应TAG的用户列表
     *
     * @return
     */
    List<SysTenantUserDto> listAllGroupUsersByTag(String tenantId, String tag);

}
