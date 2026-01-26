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


import com.baomibing.authority.dto.SysTenantUsetDto;
import com.baomibing.core.base.MBaseService;
import com.baomibing.core.common.SearchResult;
import com.baomibing.core.wrap.CommonTreeWrap;

import java.util.List;
import java.util.Set;

public interface SysTenantUsetService extends MBaseService<SysTenantUsetDto> {

    /**
     * 查询用户组
     * @param uset
     * @param pageNumber
     * @param pageSize
     * @return
     */
    SearchResult<SysTenantUsetDto> search(SysTenantUsetDto uset, int pageNumber, int pageSize);

    /**
     * 添加用户组
     * @param uset
     */
    void doSave(SysTenantUsetDto uset);

    /**
     * 更新用户组
     * @param uset
     */
    void doUpdate(SysTenantUsetDto uset);

    /**
     * 启用
     *
     * @param ids 用户组ID列表
     */
    void use(Set<String> ids);

    /**
     * 停用
     *
     * @param ids 用户组ID列表
     */
    void stop(Set<String> ids);

    /**
     * 删除
     *
     * @param ids 用户组ID列表
     */
    void deleteUsets(Set<String> ids, String tenantId);

    /**
     * 根据租户ID列表删除对应的用户组
     * @param tenantIds 租户ID列表
     */
    void deleteByTenant(Set<String> tenantIds);

    /**
     * 获取所有用户组列表
     * @return
     */
    List<CommonTreeWrap> treeAllUset(String tenantId);

}
