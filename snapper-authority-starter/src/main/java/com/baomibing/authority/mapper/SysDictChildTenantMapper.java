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

import com.baomibing.authority.entity.SysDictChildTenant;
import com.baomibing.orm.base.MBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @Author: zhenyang 2021/3/8 15:19
 * @version: 1.0.0
 */
public interface SysDictChildTenantMapper extends MBaseMapper<SysDictChildTenant> {

	/**
	 * 根据子项列表逻辑删除子项
	 * 
	 * @param childIds
	 */
    void updateDictChildDeleteByIds(@Param("childIds")Set<String> childIds);

    /**
     * 根据父编码获取子项
     * 
     * @param parentCode 父编码
     * @return
     */
    List<SysDictChildTenant> listByParentCode(@Param("parentCode") String parentCode);

    /**
     * 根据父编码列表获取子项列表
     * 
     * @param parentCodes 父编码列表
     * @return
     */
    List<SysDictChildTenant> listByParentCodes(@Param("parentCodes") Set<String> parentCodes);
}
