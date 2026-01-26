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

package com.baomibing.authority.entity;

import com.baomibing.orm.base.MBaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 系统菜单,所有组织公用一套菜单系统,只有开发管理才能修改
 * @author zening
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_menu_tenant")
public class SysMenuTenant extends MBaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.INPUT)
    private String id;
    /**
     * 菜单名称
     */
    @TableField("menu_name")
    private String menuName;
    
    /**
     * 菜单英文名称
     */
    @TableField("entitle")
    private String entitle;
    /**
     * 父菜单ID
     */
    @TableField("parent_id")
    private String parentId;
    /**
     * 跳转链接
     */
    @TableField("redirect")
	private String redirect;
    /**
     * 是否缓存
     */
    @TableField("be_unauth")
    private Boolean beUnauth;
    /**
     * 是否隐藏
     */
    @TableField("be_hidden")
    private Boolean beHidden;
    /**
     * 优先级
     */
    @TableField("priority")
    private Short priority;
    /**
     * 图标icon
     */
    @TableField("icon")
    private String icon;
    /**
     * 菜单类型
     */
    @TableField("menu_type")
    private String menuType;

    private Boolean beCopyOrigin;
    
    private transient String reqUrl;
    
    //菜单下的按钮，以"id-name"的方式，并以逗号间隔
    private transient String buttons;
    
    
}
