/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
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

    List<SysHmacUser> searchHamcUser(@Param("systemName") String systemName, @Param("appId") String appId, @Param("userId") String userId, @Param("bindType") String bindType, @Param("offset") int offset, @Param("limit") int limit);

    int countHmacUser(@Param("systemName") String systemName, @Param("appId") String appId, @Param("userId") String userId, @Param("bindType") String bindType);

    SysHmacUser getHmacUser(@Param("id") String id);

}
