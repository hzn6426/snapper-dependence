/*
 * Copyright (c) 2020-2025, zening (316279828@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
