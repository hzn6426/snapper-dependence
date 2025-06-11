/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.mapper;

import com.baomibing.authority.entity.SysUsetUserEntrust;
import com.baomibing.orm.base.MBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * SysUsetUserEntrustMapper
 *
 * @author zening 2022/6/23 11:45
 * @version 1.0.0
 */
public interface SysUsetUserEntrustMapper extends MBaseMapper<SysUsetUserEntrust> {

    /**
     * 根据用户组ID和权限ID获取用户组委托的用户列表
     * @param usetIds 用户组ID列表
     * @param permId 权限ID
     * @return
     */
    List<String> listEntrustUserCodesByUsetAndPerm(@Param("usetIds")Set<String> usetIds, @Param("permId") String permId);

    /**
     * 根据用户组ID和权限ID获取用户组委托的用户列表
     * @param usetIds 用户组ID列表
     * @param permId 权限ID
     * @return
     */
    List<String> listEntrustUserIdsByUsetAndPerm(@Param("usetIds")Set<String> usetIds, @Param("permId") String permId);


    /**
     * 根据用户组ID和权限ID删除用户组对应的权限关系
     * @param usetId 用户组ID
     * @param permId 权限ID
     */
    void deleteByUsetAndPerm(@Param("usetId") String usetId, @Param("permId") String permId);
}
