/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 职位组织委托
 * 
 * @author zening
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public class PositionGroupEntrustDto implements Serializable {
	private String id;
	private String positionId;
	private String groupEntrustId;

}
