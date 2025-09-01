/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.spi;

import com.baomibing.core.wrap.EntrustWarpper;
import com.baomibing.tool.user.User;

/**
 * BusinessAuthService
 *
 * @author zening 2022/4/27 15:37
 * @version 1.0.0
 */
public interface BusinessAuthService {

    /**
     * 根据用户及权限ID获取用户委托的组织和用户信息
     *
     * @param user              当前登录用户
     * @param permId            业务权限ID
     * @param scope             业务范围
     * @param beIgnoreUserScope 是否忽略用户权限范围
     * @param beIgnoreGroupScope 是否忽略组织权限范围
     * @return
     */
    EntrustWarpper getEntrustBusinessPerm(User user, String permId, String scope, boolean beIgnoreUserScope, boolean beIgnoreGroupScope);

    /**
     * 根据请求URL和方法获取权限对应的动作标识
     * @param url 资源请求URL
     * @param method 资源请求方法
     * @return
     */
    String getPermActionByUrlAndMethod(String url, String method);

    /**
     * 根据请求URL和方法获取权限对应的业务权限ID
     * @param url 资源请求URL
     * @param method 资源请求方法
     * @return
     */
    String getPermIdByUrlAndMethod(String url, String method);

    /**
     * 根据权限动作action获取对应权限的ID
     * @param action 权限动作
     * @return
     */
    String getPermIdByAction(String action);
}
