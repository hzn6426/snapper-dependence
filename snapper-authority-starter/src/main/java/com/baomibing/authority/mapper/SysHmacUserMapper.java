
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


import com.baomibing.authority.entity.SysHmacUser;
import com.baomibing.orm.base.MBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * SysHmacUserMapper
 *
 * @author zening 2022/5/9 15:39
 * @version 1.0.0
 */
public interface SysHmacUserMapper extends MBaseMapper<SysHmacUser> {

    List<SysHmacUser> listForCache(@Param("ids") List<String> ids);

    List<SysHmacUser> listForTenantCache(@Param("ids") List<String> ids);

    List<SysHmacUser> searchHamcUser(@Param("systemName") String systemName, @Param("appId") String appId, @Param("userId") String userId, @Param("bindType") String bindType, @Param("offset") int offset, @Param("limit") int limit);

    int countHmacUser(@Param("systemName") String systemName, @Param("appId") String appId, @Param("userId") String userId, @Param("bindType") String bindType);

    SysHmacUser getHmacUser(@Param("id") String id);

}
