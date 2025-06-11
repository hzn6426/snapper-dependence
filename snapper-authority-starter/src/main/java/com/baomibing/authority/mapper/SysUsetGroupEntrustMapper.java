/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.mapper;

import com.baomibing.authority.entity.SysGroup;
import com.baomibing.authority.entity.SysUsetGroupEntrust;
import com.baomibing.orm.base.MBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * SysUsetGroupEntrustMapper
 *
 * @author zening 2022/6/23 11:57
 * @version 1.0.0
 */
public interface SysUsetGroupEntrustMapper extends MBaseMapper<SysUsetGroupEntrust> {

    /**
     * 根据用户组和权限获取改用组对应权限的组织委托列表
     * @param usetIds 用户组ID列表
     * @param permId 权限ID
     * @return
     */
    List<SysGroup> listEntrustGroupsByUsetAndPerm(@Param("usetIds") Set<String> usetIds, @Param("permId") String permId);

    /**
     * 删除用户组和权限关联的组织委托信息
     * @param usetId 用户组ID
     * @param permId 权限ID
     */
    void deleteByUsetAndPerm(@Param("usetId") String usetId, @Param("permId") String permId);
}
