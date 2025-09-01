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
 * 用户职位（一个用户可以用多个职位）
 * 
 * @author zening
 * @version  1.0.0
 */
@Data
@Accessors(chain = true)
public class UserPositionDto implements Serializable {

	private String id;
	private String userId;
	private String positionId;
	private String groupId;
}
