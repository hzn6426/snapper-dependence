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
 * 职位用户委托
 * 
 * @author zening
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public class PositionUserEntrustDto implements Serializable {
	private String id;
	private String userId;
	private String positionId;

}
