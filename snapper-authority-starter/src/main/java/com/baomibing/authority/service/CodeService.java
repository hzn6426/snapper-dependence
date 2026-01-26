
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

package com.baomibing.authority.service;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface CodeService {

	/**
	 * 生成顺序的序号后缀
	 * 
	 * @param redisKey      缓存中的key
	 * @param paddingDigit  填充位数
	 * @param maxIdFunction 找到最大序号的方法
	 * @param maxIdFunction 找到最大序号所需参数
	 * @return
	 */
	<T> String makeOrderedSuffix(String redisKey, int paddingDigit, Function<T, Integer> maxIdFunction, T functionParam);

	/**
	 * 生成顺序的序号后缀
	 * 
	 * @param redisKey      缓存中的key
	 * @param paddingDigit  填充位数
	 * @param paddingDigit  填充字符
	 * @param maxIdFunction 找到最大序号的方法
	 * @param maxIdFunction 找到最大序号所需参数
	 * @return
	 */
	<T> String makeOrderedSuffix(String redisKey, int paddingDigit, String paddingChar, Function<T, Integer> maxIdFunction, T functionParam);

	/**
	 * 生成顺序的序号后缀
	 * @param redisKey 缓存中的key
	 * @param paddingDigit 填充位数
	 * @param maxIdFunction 找到最大序号的方法
	 * @param maxIdFunction 找到最大序号所需参数
	 * @return
	 */
	<T> String makeTenantOrderedSuffix(String redisKey, int paddingDigit, BiFunction<T,T, Integer> maxIdFunction, T functionParam, T functionParam2);

	/**
	 * 生成租户的前缀 采用3位数，最大支持租户46656个租户，后续扩展可以在前面添加位数
	 * @param redisKey
	 * @param paddingDigit
	 * @param maxIdFunction
	 * @return
	 */
	String makeTenantOrderedPrefix(String redisKey, int paddingDigit, Supplier<String> maxIdFunction);
}
