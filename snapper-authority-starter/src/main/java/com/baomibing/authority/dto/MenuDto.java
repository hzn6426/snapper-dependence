/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.dto;

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
 * 
 * @author zening
 * @since 1.0.0
 */
@JsonInclude(Include.NON_NULL)
@Data @Accessors(chain = true) 
public class MenuDto implements Serializable {

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

	private Boolean beLoginUnauth;

	private String icon;

	private String redirect;

	private Boolean beHidden;

	private String menuType;

	private Boolean beLock;

	private List<MenuDto> children;

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
	 */
	@JsonInclude
	private String permScope;

	/**
	 * 业务权限ID
	 */
	private String permId;
	/**
	 * 业务权限动作
	 */
	private String permAction;
	/**
	 * 权限时效开始时间
	 */
	private Date permStartTime;
	/**
	 * 权限时效结束时间
	 */
	private Date permEndTime;


	private String reqMethod;

	private String dataPerm;

	private String columnPerm;
	//对应的按钮是否有@Action数据权限标识
	private Boolean beHaveAction = Boolean.FALSE;

}
