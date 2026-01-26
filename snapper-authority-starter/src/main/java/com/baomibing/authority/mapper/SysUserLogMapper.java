
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
