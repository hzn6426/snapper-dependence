
package com.baomibing.authority.service;



import com.baomibing.authority.dto.UsetDataPermDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

public interface SysUsetDataPermService extends MBaseService<UsetDataPermDto> {

    /**
     * 根据用户组获取对应的数据权限
     * @param usetId
     * @param permId
     * @return
     */
    UsetDataPermDto getUsetDataPerm(String usetId, String permId);

    /**
     * 根据用户组列表获取对应的数据权限
     * @param usetIds
     * @param permId
     * @return
     */
    List<UsetDataPermDto> listUsetDataPerm(Set<String> usetIds, String permId);

    /**
     * 保存用户组数据权限
     * @param perm
     */
    void saveUserDataPerm(UsetDataPermDto perm);
}
