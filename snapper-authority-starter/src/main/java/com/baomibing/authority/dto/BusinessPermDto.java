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

/**
 * 业务权限对象
 * 
 * @author zening
 * @since 1.0.0
 */
@JsonInclude(Include.NON_NULL)
@Data @Accessors(chain = true)
public class BusinessPermDto {
	private String id;
	private String menuId;
	private String permName;
	private String permAction;
	private String reqUrl;
	private String reqMethod;
	private String note;
	private String buttonId;
	private String actionRef;
	
}
