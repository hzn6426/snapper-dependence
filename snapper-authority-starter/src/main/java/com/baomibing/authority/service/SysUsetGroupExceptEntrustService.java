
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



import com.baomibing.authority.dto.GroupDto;
import com.baomibing.authority.dto.UsetGroupExceptEntrustDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

/**
 * SysUsetGroupEntrustService
 *
 * @author zening
 * @version 1.0.0
 */
public interface SysUsetGroupExceptEntrustService extends MBaseService<UsetGroupExceptEntrustDto> {

    /**
     * 根据用户组ID和权限ID获取用户组委托的权限对应的组织列表
     * @param usetIds 用户组ID列表
     * @param permId 权限ID
     * @return
     */
    List<GroupDto> listEntrustGroupsByUsetAndPerm(Set<String> usetIds, String permId);

    /**
     * 根据用户组ID和权限ID删除用户组对应的权限关系
     * @param usetId 用户组ID
     * @param permId 权限ID
     */
    void deleteByUsetAndPerm(String usetId, String permId);
}
