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

import com.baomibing.authority.entity.SysButtonTenant;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统按钮映射
 * @author zening
 * @date 2018年3月16日 上午11:09:56
 * @version 1.0.0
 */
public interface SysButtonTenantMapper extends BaseMapper<SysButtonTenant> {

    /**
     * 根据条件分页查询按钮及API信息
     *
     * @param menuId 按钮ID
     * @return
     */
    List<SysButtonTenant> listButtonAndApiByMenu(@Param("menuId")String menuId, @Param("keyword") String keyword, @Param("limit") int limit, @Param("offset") int offset);

    /**
     * 条件获取按钮及API信息的记录数
     * @param menuId 按钮ID
     * @return
     */
    int countButtonAndApiByMenu(@Param("menuId")String menuId);

    /**
     * 关键字模糊匹配获取按钮
     * @param keyword
     * @return
     */
    List<SysButtonTenant> listButtonByKeyWork(@Param("keyword") String keyword);

    SysButtonTenant getButtonAndApiById(@Param("id") String id);

}
