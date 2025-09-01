/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.mapper;

import com.baomibing.authority.entity.SysDictChild;
import com.baomibing.orm.base.MBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface SysDictChildMapper extends MBaseMapper<SysDictChild> {

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
    List<SysDictChild> listByParentCode(@Param("parentCode") String parentCode);

    /**
     * 根据父编码列表获取子项列表
     * 
     * @param parentCodes 父编码列表
     * @return
     */
    List<SysDictChild> listByParentCodes(@Param("parentCodes") Set<String> parentCodes);
}
