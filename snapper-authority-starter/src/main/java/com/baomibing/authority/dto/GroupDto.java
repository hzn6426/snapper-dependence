
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 组织
 * 
 * @author zening
 * @since 1.0.0
 */
@JsonInclude(Include.NON_NULL)
@Data
@Accessors(chain = true)
public class GroupDto implements Serializable {
	private static final long serialVersionUID = -1271796986161665248L;

	private String id;
	private Integer glft;
	private Integer grht;
	private String parentId;
	private String groupName;
	private Integer groupLevel;
	private String groupId;
	private List<GroupDto> children;
	private String parentGroupName;
	private Boolean beChildCompany = Boolean.FALSE;
	private Boolean beLock;
    private Short priority;
	
}
