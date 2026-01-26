
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
@TableName("sys_menu")
public class SysMenu extends MBaseModel {
	private static final long serialVersionUID = -7187826710738588372L;

	@TableId(value = "id", type = IdType.INPUT)
    private String id;
    @TableField("menu_name")
    private String menuName;
    @TableField("entitle")
    private String entitle;
    @TableField("parent_id")
    private String parentId;
    @TableField("redirect")
	private String redirect;
    @TableField("be_unauth")
    private Boolean beUnauth;
    private Boolean beLoginUnauth;
    @TableField("be_hidden")
    private Boolean beHidden;
    @TableField("priority")
    private Short priority;
    @TableField("icon")
    private String icon;
    @TableField("menu_type")
    private String menuType;
    private String groupId;
    private Boolean beLock;
//	@TableField("root_parent")
//	private String rootParent;
    

    private transient String reqUrl;
    //菜单下的按钮，以"id-name"的方式，并以逗号间隔
    private transient String buttons;
    
    
}
