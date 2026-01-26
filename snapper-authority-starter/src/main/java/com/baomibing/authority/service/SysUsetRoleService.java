
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


import com.baomibing.authority.dto.UsetRoleDto;
import com.baomibing.core.base.MBaseService;

import java.util.Set;

/**
 * SysUsetRoleService
 *
 * @author zening
 * @version 1.0.0
 */
public interface SysUsetRoleService extends MBaseService<UsetRoleDto> {

    /**
     * 保存用户组和角色列表
     * @param usetId 用户组ID
     * @param roleIds 角色列表
     */
    void saveFromUset(String usetId, Set<String> roleIds);

    /**
     * 根据用户组获取所有角色ID列表
     * @param usetId 用户组ID
     * @return
     */
    Set<String> listRolesByUset(String usetId);

    /**
     * 根据用户组ID删除对应的角色关系
     * @param usetIds 用户组ID列表
     */
    void deleteByUsets(Set<String> usetIds);

}
