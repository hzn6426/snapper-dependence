
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


import com.baomibing.authority.dto.UsetDto;
import com.baomibing.core.base.MBaseService;
import com.baomibing.core.common.SearchResult;
import com.baomibing.core.wrap.CommonTreeWrap;

import java.util.List;
import java.util.Set;

public interface SysUsetService extends MBaseService<UsetDto> {

    SearchResult<UsetDto> search(UsetDto v, int pageNumber, int pageSize);
    
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
    void deleteUsets(Set<String> ids);

    /**
     * 获取所有用户组列表
     * @return
     */
    List<CommonTreeWrap> treeAllUset();

    void saveUset(UsetDto uset);

    void updateUset(UsetDto uset);


}
