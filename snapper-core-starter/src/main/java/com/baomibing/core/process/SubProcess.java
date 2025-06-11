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
 * 子流程
 * 
 * @param <S> 状态
 * @param <A> 动作
 * @author zening
 * @since 1.0.0
 */
@SuppressWarnings("serial")
@Data
public class SubProcess<S, A> implements Serializable {

	private Action<A> action;
	
	private Source<S> source;
	
	private Target<S> target;
	
//	private Predicate<S> predicate;//添加条件（分支） 成立为状态A，否则为状态B
	
	public SubProcess() {
	}
	
	public SubProcess<S, A> target(S target) {
		this.target = new Target<S>(target);
		return this;
	}
	
	public SubProcess<S, A> source(S source) {
		this.source = new Source<S>(source);
		return this;
	}
	
	public SubProcess<S, A> action(A action) {
		this.action = new Action<A>(action);
		return this;
	}
	
	
}
