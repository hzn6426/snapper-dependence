/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
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
