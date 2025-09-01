/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.wrap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 通用树组建对象封装
 * 
 * @author zening
 * @since 1.0.0
 */
@JsonInclude(Include.NON_NULL)
@Data @Accessors(chain = true) 
public class CommonTreeWrap implements Serializable {

	private static final long serialVersionUID = -8467761934399645955L;
	private boolean disabled = Boolean.FALSE;
	private String key;
	private String value;
	private String title;
	private String url;
	private String parentId;
	private String parentGroupName;
	private String tag;
	private String iconCls;
	private String menuType;
	private String state;
	private Boolean isLeaf;
	private Boolean selectable = Boolean.TRUE;
	private Boolean disableCheckbox = Boolean.FALSE;
	private Boolean beUnAuth = Boolean.FALSE;
	private String permId;
	private List<CommonTreeWrap> children;
	
}
