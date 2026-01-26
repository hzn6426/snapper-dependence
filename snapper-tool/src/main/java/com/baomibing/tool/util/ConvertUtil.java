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
package com.baomibing.tool.util;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.Validate;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.*;

public abstract class ConvertUtil {

	/**
	 * 将对象值转换成BigDecimal类型
	 * 
	 * @param tobeConvertedValue 要转换的数值
	 * @return 转化后的值
	 */
	public static BigDecimal toBigDecimal(Number tobeConvertedValue) {
		return BigDecimal.valueOf(tobeConvertedValue.doubleValue());
	}

	/**
	 * 将数组转化成map
	 * 
	 * @param <K>   key的对象类型
	 * @param <V>   value的对象类型
	 * @param array 需要转化的数组
	 * @return 转化的map
	 */
	public static <K, V> Map<K, V> toMap(Object[] array) {
		Map<K, V> map = new LinkedHashMap<>();
		map = MapUtils.putAll(map, array);
		return map;
	}

	/**
	 * 将数组转化为list
	 * 
	 * @param <T>    数组类型
	 * @param arrays 数组
	 * @return 转化的list列表
	 */
	@SafeVarargs
	public static <T> List<T> toList(T... arrays) {
		return !Checker.beNotEmpty(arrays) ? Collections.<T>emptyList() : new ArrayList<>(Arrays.asList(arrays));
	}

	/**
	 * 将set集合转化成list
	 * 
	 * @param <T> 集合类型
	 * @param set 集合
	 * @return 转化的List列表
	 */
	public static <T> List<T> toList(Set<T> set) {
		return !Checker.beNotEmpty(set) ? Collections.<T>emptyList() : new ArrayList<>(set);
	}

	/**
	 * 将数组转化为set
	 * 
	 * @param <T>    数组类型
	 * @param arrays 数组
	 * @return 转化的set列表
	 */
	@SafeVarargs
	public static <T> Set<T> toSet(T... arrays) {
		return !Checker.beNotEmpty(arrays) ? Collections.<T>emptySet() : new LinkedHashSet<>(Arrays.asList(arrays));
	}

	/**
	 * 将列表转为set
	 * 
	 * @param <T>  列表中对象类型
	 * @param list 列表
	 * @return 转化的set列表
	 */
	public static <T> Set<T> toSet(List<T> list) {
		return !Checker.beNotEmpty(list) ? Collections.<T>emptySet() : new LinkedHashSet<>(list);
	}

	/**
	 * 将集合转化为<code>arrayComponentType</code>类型的数组
	 *
	 * @param <T>                集合类型
	 * @param collection         集合
	 * @param arrayComponentType 数组类型
	 * @return 转化后的数组
	 */
	public static <T> T[] toArray(Collection<T> collection, Class<T> arrayComponentType) {
		if (null == collection) {
			return null;
		}

		Validate.notNull(arrayComponentType, "arrayComponentType must not be null");

		// 如果采用大家常用的把a的length设为0,就需要反射API来创建一个大小为size的数组,而这对性能有一定的影响.
		// 所以最好的方式就是直接把a的length设为Collection的size从而避免调用反射API来达到一定的性能优化.
		@SuppressWarnings("unchecked")
		T[] array = (T[]) Array.newInstance(arrayComponentType, collection.size());

		// 注意,toArray(new Object[0]) 和 toArray() 在功能上是相同的.
		return collection.toArray(array);
	}


}
