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

import java.util.Date;

/**
 * 系统按钮-所有用户公用一套系统按钮,只有开发管理员才能有修改权限
 * @author zening
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_button_tenant")
public class SysButtonTenant extends MBaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * 按钮ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;
    
    /**
     * 按钮名称
     */
    @TableField("button_name")
    private String buttonName;
    
    
    /**
     * 菜单ID
     */
    @TableField("menu_id")
    private String menuId;
    
    /**
     * 按钮功能描述
     */
    @TableField("note")
    private String note;
    
	/**
	 * 是否设置为无权限
	 */
    @TableField("be_unauth")
    private Boolean beUnauth;
    
	@TableField("sub_menu")
	private String subMenu;

	private Boolean beCopyOrigin;

	private transient String reqMethod;

	private transient String reqUrl;

	// 业务权限范围
	private transient String permScope;

	// 业务权限ID
	private transient String permId;
	/**
	 * 权限时效开始时间
	 */
	private transient Date permStartTime;
	/**
	 * 权限时效结束时间
	 */
	private transient Date permEndTime;
	/**
	 * 权限动作
	 */
	private transient String permAction;
	/**
	 * 动作别名
	 */
	private transient String actionAlias;



}
