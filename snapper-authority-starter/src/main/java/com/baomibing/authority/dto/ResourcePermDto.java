/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 前端用来封装分配权限数据的Dto
 * 
 * @author zening
 * @since 1.0.0
 */
@JsonInclude(Include.NON_NULL)
@Data @Accessors(chain = true)
public class ResourcePermDto {

	private List<String> menus;
	private List<String> buttons;
	private String roleId;
	private String menuId;// 保存按钮权限时候对应的菜单ID
}
