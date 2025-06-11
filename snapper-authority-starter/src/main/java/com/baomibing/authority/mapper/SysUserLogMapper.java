/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.mapper;

import com.baomibing.authority.entity.SysUserLog;
import com.baomibing.orm.base.MBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface SysUserLogMapper extends MBaseMapper<SysUserLog> {

    Integer countLog(@Param("state") String state, @Param("createUserCnName") String createUserCnName, @Param("exchangeName") String exchangeName,
         @Param("startTime")Date startTime, @Param("endTime") Date endTime);

    List<SysUserLog> searchLog(@Param("state") String state, @Param("createUserCnName") String createUserCnName, @Param("exchangeName") String exchangeName,
         @Param("startTime")Date startTime, @Param("endTime") Date endTime, @Param("offset") int offset, @Param("pageSize") int pageSize);

}
