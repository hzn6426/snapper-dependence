
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

import com.baomibing.authority.entity.SysButton;
import com.baomibing.orm.base.MBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysButtonMapper extends MBaseMapper<SysButton> {

    /**
     * 根据条件分页查询按钮及API信息
     *
     * @param menuId 按钮ID
     * @return
     */
    List<SysButton> listButtonAndApiByMenu(@Param("menuId")String menuId, @Param("keyword") String keyword, @Param("limit") int limit, @Param("offset") int offset);

    /**
     * 条件获取按钮及API信息的记录数
     * @param menuId 按钮ID
     * @return
     */
    int countButtonAndApiByMenu(@Param("menuId")String menuId,  @Param("keyword") String keyword);

    /**
     * 关键字模糊匹配获取按钮
     * @param keyword
     * @return
     */
    List<SysButton> listButtonByKeyWork(@Param("keyword") String keyword);

    /**
     * 根据按钮ID获取按钮信息和API信息
     * @param id
     * @return
     */
    SysButton getButtonAndApiById(@Param("id") String id);
}
