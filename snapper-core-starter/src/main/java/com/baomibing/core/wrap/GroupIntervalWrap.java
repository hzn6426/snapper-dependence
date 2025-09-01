/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.wrap;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 组织区间（左右值）用于在mapper中映射group 以便在mapper中构造条件
 * 
 * @author zening
 * @since 1.0.0
 */
@Getter @AllArgsConstructor
public class GroupIntervalWrap implements Serializable {
	private static final long serialVersionUID = 5253363281273098752L;
	private String id;
	private int left;
	private int rght;
	
}
