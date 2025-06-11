
package com.baomibing.authority.service;


import com.baomibing.authority.dto.UserDataPermDto;
import com.baomibing.core.base.MBaseService;

public interface SysUserDataPermService extends MBaseService<UserDataPermDto> {

    /**
     * 保存用户数据权限
     * @param perm
     */
    void saveUserDataPerm(UserDataPermDto perm);

    /**
     * 获取用户的数据权限
     * @param userId
     * @param orgId
     * @param permId
     */
    UserDataPermDto getUserDataPerm(String userId, String orgId, String permId);
}
