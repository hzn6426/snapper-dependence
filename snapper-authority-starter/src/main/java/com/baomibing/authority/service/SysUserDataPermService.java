
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


import com.baomibing.authority.dto.UserDataPermDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

public interface SysUserDataPermService extends MBaseService<UserDataPermDto> {

    /**
     * 保存用户数据权限
     * @param perm
     */
    void saveUserDataPerm(UserDataPermDto perm);

    /**
     * 获取用户的数据权限
     * @param userId
     * @param orgId
     * @param permId
     */
    UserDataPermDto getUserDataPerm(String userId, String orgId, String permId);

    /**
     * 根据用户和组织 ID 获取用户的数据权限
     * @param userIds 用户 ID
     * @param orgId 组织 ID
     * @return
     */
    List<UserDataPermDto> listUserDataPerms(Set<String> userIds, String orgId);

    /**
     * 根据用户 Id列表及其组织，删除对应的数据权限
     * @param orgId   组织 ID
     * @param userIds 用户ID 列表
     */
    void deleteUserDataPerms(String orgId, Set<String> userIds);

    /**
     * 复制用户的数据权限
     * @param uid 用户 ID
     * @param gid 用户组织 ID
     * @param toUserId 目标用户
     */
    void doCopyUserDataPerms(String uid, String gid, String toUserId);
}
