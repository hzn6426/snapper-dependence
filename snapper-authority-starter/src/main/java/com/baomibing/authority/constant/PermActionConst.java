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
package com.baomibing.authority.constant;

/**
 * PermActionConst
 *
 * @author zening 2023/7/17 15:15
 * @version 1.0.0
 **/
public abstract class PermActionConst {


    //=======================================================================//
    //                             Customer Mapper                           //
    //=======================================================================//
    //获取角色列表（可分配的）
    public static final String ROLE_LIST_ALL = "ROLE_LISTALL";

    //获取菜单列表（可分配的）
    public static final String ROLE_TREE_ALL_MENUS = "ROLE_TREEALLMENUS";
    //获取按钮列表（可分配的）
    public static final String ROLE_LIST_ALL_BUTTONS_BY_MENU = "ROLE_LISTALLBUTTONSBYMENU";
    //用户查询
    public static final String USER_SEARCH = "USER_SEARCH";
    //组织查询所有用户
    public static final String USER_LIST_ALL_GROUP_USERS = "USER_LIST_ALL_GROUP_USERS";
    //角色查询
    public static final String ROLE_SEARCH = "ROLE_SEARCH";
    //用户组查询
    public static final String USET_SEARCH = "USET_SEARCH";
    //获取所有用户组
    public static final String USET_TREE_ALL_USET = "USET_TREE_ALL_USET";
    //职位查询
    public static final String POSITION_SEARCH = "POSITION_SEARCH";
    //组织查询用户
    public static final String GROUP_SEARCH_USER = "GROUP_SEARCHUSER";
    //组织查询职位树
    public static final String GROUP_TREE_ALL_GROUPS_AND_POSITIONS = "GROUP_TREEALLGROUPSANDPOSITIONS";
    //组织获取所有组织和用户
    public static final String GROUP_TREE_ALL_GROUPS_AND_USERS = "GROUP_TREEALLGROUPSANDUSERS";
}
