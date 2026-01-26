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

package com.baomibing.authority.dto;

import com.baomibing.authority.constant.enums.BusinessPermScopeEnum;
import com.baomibing.authority.constant.enums.ResourceTypeEnum;
import com.baomibing.authority.wrap.CheckGroupWrap;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.github.dozermapper.core.Mapping;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 菜单对象
 * @author zening
 * @version 1.0.0
 */
@JsonInclude(Include.NON_NULL)
@Data @Accessors(chain = true) 
public class MenuTenantDto implements Serializable {

	private String id;
    
	@Mapping("menuName")
    private String name;
	
	private String entitle;
	
	@Mapping("reqUrl")
	private String path;

	@Mapping("parentId")
    private String parent;
	
	private Short priority;

	private Boolean beUnauth;
	
	private String icon;
	
	private String redirect;
	
	private Boolean beHidden;
	
	private String menuType;

	private Boolean beCopyOrigin;

	private List<MenuTenantDto> children;
	
	private List<CheckGroupWrap> buttonWraps;
	
	/**
	 * 按钮还是菜单
	 * 
	 * @see ResourceTypeEnum
	 */
	private String tag = ResourceTypeEnum.MENU.name();
	
	/**
	 * 业务权限范围
	 * 
	 * @see BusinessPermScopeEnum
	 */
	@JsonInclude
	private String permScope;

	/**
	 * 业务权限ID
	 */
	private String permId;
	/**
	 * 权限时效开始时间
	 */
	private Date permStartTime;
	/**
	 * 权限时效结束时间
	 */
	private Date permEndTime;

//	private String reqUrl;

	private String reqMethod;

//	private String menuName;
//
//	private String parentId;

}
