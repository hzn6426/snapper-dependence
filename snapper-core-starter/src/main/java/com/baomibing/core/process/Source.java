/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.process;

import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

/**
 * 来源
 * @author zening
 * @date 2019-07-20 15:01:33
 * @version 1.0.0
 */
@SuppressWarnings("serial")
@Data
public class Source<E> implements Serializable {

	@Getter
	E state;
	
	public Source(E e) {
		state = e;
	}
}
