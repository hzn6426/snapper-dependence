/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
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
