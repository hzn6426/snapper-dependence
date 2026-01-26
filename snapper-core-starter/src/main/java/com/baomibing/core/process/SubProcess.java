/*
 * Copyright (c) 2020-2025, zening (316279828@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
