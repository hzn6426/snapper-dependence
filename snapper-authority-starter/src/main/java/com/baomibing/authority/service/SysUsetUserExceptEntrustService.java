
package com.baomibing.authority.service;



import com.baomibing.authority.dto.UsetUserExceptEntrustDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

/**
 * SysUsetUserEntrustService
 *
 * @author zening
 * @version 1.0.0
 */
public interface SysUsetUserExceptEntrustService extends MBaseService<UsetUserExceptEntrustDto> {

    /**
     * 根据用户组ID和权限ID获取用户组委托的用户列表
     * @param usetIds 用户组列表
     * @param permId 权限ID
     * @return
     */
    List<String> listEntrustUserCodesByUsetAndPerm(Set<String> usetIds, String permId);

    /**
     * 根据用户组ID和权限ID获取用户组委托的用户列表
     * @param usetIds 用户组列表
     * @param permId 权限ID
     * @return
     */
    List<String> listEntrustUserIdsByUsetAndPerm(Set<String> usetIds, String permId);

    /**
     * 根据用户组ID和权限ID删除对应用户组的权限
     * @param usetId 用户组ID
     * @param permId 权限ID
     */
    void deleteByUsetAndPerm(String usetId, String permId);
}
