
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
