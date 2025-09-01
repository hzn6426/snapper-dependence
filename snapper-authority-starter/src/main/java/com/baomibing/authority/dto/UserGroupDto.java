/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 用户组织关联（一个用户对应多个组织）
 * 
 * @author zening
 * @version  1.0.0
 */
@Data
@Accessors(chain = true)
public class UserGroupDto implements Serializable {

	private  String id;
	private String userNo;
	private String userId;
	private String groupId;

	private String groupName;
	private String companyName;
	private String companyId;
	
	
}
