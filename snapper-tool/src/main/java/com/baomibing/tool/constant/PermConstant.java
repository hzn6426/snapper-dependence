/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.constant;

/**
 * PermConstant
 *
 * @author zening 2023/5/11 22:11
 * @version 1.0.0
 **/
public abstract class PermConstant {

    //权限缓存预热时，资源不需要权限，此时角色ID做此标记
    public static final String RESOURCE_NO_NEED_ROLE = "RESOURCE_NO_NEED_ROLE";

    //权限缓存预热时，资源不需要登录，此时角色ID做此标记
    public static final String RESOURCE_NO_NEED_LOGIN = "RESOURCE_NO_NEED_LOGIN";

    public static final String CREATE_USER = "create_user";

    public static final String GROUP_ID = "group_id";

    public static final String TENANT_ID = "tenant_id";

    public static final String COLUMN_IGNORE = "_COLUMN_IGNORE";

    public static final String CURRENT_COMPANY = "CURRENT_COMPANY";

    public static final String ROOT_GROUP  = "R";
}
