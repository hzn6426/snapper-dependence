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

import com.baomibing.core.process.ProcessBuilder.Builder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Sets;
import lombok.Getter;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 状态流超类,用于初始化状态流及执行状态流
 * 
 * @param <S> 状态
 * @param <A> 动作
 * @author zening
 * @since 1.0.0
 */
@SuppressWarnings("serial")
public abstract class StateProcess<S extends Enum<S>, A extends Enum<A>> implements Serializable {
	
	@SuppressWarnings("unchecked")
	public  StateProcess() {
		//获取泛型参数的Class类型
		Type genType = getClass().getGenericSuperclass();  
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Class<S> stateClassType = (Class<S>) params[0];
        Class<A> actionClassType = (Class<A>) params[1];
        builder = ProcessBuilder.create();
		builder.actions(Sets.newHashSet(actionClassType.getEnumConstants())).states(Sets.newHashSet(stateClassType.getEnumConstants()));
		process = initProcess();
	}
	
	/**
	 * 状态流程
	 * @ignore
	 */
	@Getter @JsonIgnore
	private SProcess<S, A> process;
	/**
	 * builder构建起
	 * @ignore
	 */
	@Getter @JsonIgnore
	protected Builder<S, A> builder;
	
	//初始化状态流程
	public abstract SProcess<S, A> initProcess();
	
	
	
}
