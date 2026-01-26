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
