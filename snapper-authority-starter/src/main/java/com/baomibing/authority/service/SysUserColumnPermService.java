
package com.baomibing.authority.service;


import com.baomibing.authority.dto.UserColumnPermDto;

public interface SysUserColumnPermService {

    void saveColumnPerm(UserColumnPermDto perm);

    UserColumnPermDto getUserColumnPerm(String userId, String orgId, String permId);

    void deleteUserColumnPerm(String userId, String orgId, String permId);
}
