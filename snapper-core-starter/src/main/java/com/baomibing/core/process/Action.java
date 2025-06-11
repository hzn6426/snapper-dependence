/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.process;

import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

/**
 * 任务
 * 
 * @param <T>
 * @author zening
 * @since 1.0.0
 */
@SuppressWarnings("serial")
@Data
public class Action<T> implements Serializable {

	@Getter
	private T task;
	
	public Action(T action) {
		this.task = action;
	}
}
