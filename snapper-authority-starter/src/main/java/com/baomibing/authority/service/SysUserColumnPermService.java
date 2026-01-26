
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


import com.baomibing.authority.dto.UserColumnPermDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

public interface SysUserColumnPermService extends MBaseService<UserColumnPermDto> {

    void saveColumnPerm(UserColumnPermDto perm);

    UserColumnPermDto getUserColumnPerm(String userId, String orgId, String permId);

    void deleteUserColumnPerm(String userId, String orgId, String permId);

    /**
     * 根据用户及组织 ID 获取用户列权限
     * @param userIds 用户 ID
     * @param orgId 组织 ID
     * @return
     */
    List<UserColumnPermDto> listUserColumnPerms(Set<String> userIds, String orgId);

    /**
     * 根据组织 ID 及用户列表删除用户对应组织的列权限
     * @param orgId
     * @param userIds
     */
    void deleteUserColumnPerms(String orgId, Set<String> userIds);

    /**
     * 复制用户列权限
     * @param uid 待复制的用户 ID
     * @param gid 待复制的用户组织 ID
     * @param toUserId 目标用户 ID
     */
    void doCopyUserColumnPerms(String uid, String gid, String toUserId);
}
