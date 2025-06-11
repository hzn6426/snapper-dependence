/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
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
	
}
