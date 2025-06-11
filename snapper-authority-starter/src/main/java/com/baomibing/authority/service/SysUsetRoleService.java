
package com.baomibing.authority.service;


import com.baomibing.authority.dto.UsetRoleDto;
import com.baomibing.core.base.MBaseService;

import java.util.Set;

/**
 * SysUsetRoleService
 *
 * @author zening
 * @version 1.0.0
 */
public interface SysUsetRoleService extends MBaseService<UsetRoleDto> {

    /**
     * 保存用户组和角色列表
     * @param usetId 用户组ID
     * @param roleIds 角色列表
     */
    void saveFromUset(String usetId, Set<String> roleIds);

    /**
     * 根据用户组获取所有角色ID列表
     * @param usetId 用户组ID
     * @return
     */
    Set<String> listRolesByUset(String usetId);

    /**
     * 根据用户组ID删除对应的角色关系
     * @param usetIds 用户组ID列表
     */
    void deleteByUsets(Set<String> usetIds);

}
