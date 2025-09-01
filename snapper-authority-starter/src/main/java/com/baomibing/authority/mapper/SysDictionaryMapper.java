/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.mapper;

import com.baomibing.authority.entity.SysDictionary;
import com.baomibing.orm.base.MBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashSet;

public interface SysDictionaryMapper extends MBaseMapper<SysDictionary> {

    void updateDictDeleteByIds(@Param("ids") HashSet<String> ids);

}
