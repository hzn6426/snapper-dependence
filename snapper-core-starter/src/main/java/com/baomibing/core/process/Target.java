/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.process;

import lombok.Data;

import java.io.Serializable;

/**
 * 流程走向
 * 
 * @author zening
 * @since 1.0.0
 */
@SuppressWarnings("serial")
@Data
public class Target<E> implements Serializable {

	private E state;
	
	public Target(E e) {
		this.state = e;
	}
}
