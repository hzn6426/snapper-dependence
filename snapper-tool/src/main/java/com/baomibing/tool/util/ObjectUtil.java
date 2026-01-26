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

import com.google.common.collect.Iterables;

import java.util.*;

/**
 * 对象工具
 * 
 * @author zening
 * @since 1.0.0
 */
public abstract class ObjectUtil {

	/**
	 * 空返回默认值
	 * 
	 * @param t            判断对象
	 * @param defaultValue 默认值
	 * @return 操作后的数据
	 */
	public static <T> T defaultIfNull(T t, T defaultValue) {
		return t != null ? t : defaultValue;
	}
	
	/**
	 * 非NULL返回字符串
	 * 
	 * @param t 判断对象
	 * @return 操作后的数据
	 */
	public static <T> String toStringIfNotNull(T t) {
		return t == null ? null : t.toString().trim();
	}
	
	/**
	 * NULL返回空列表
	 * 
	 * @param list 判断列表
	 * @return 操作后的数据
	 */
	public static <T> List<T> nullIfEmptyList(List<T> list) {
		return list != null ? list : new ArrayList<T>();
	}
	
	/**
	 * NULL返回空SET
	 * 
	 * @param set 判断SET
	 * @return 操作后的数据
	 */
	public static <T> Set<T> nullIfEmptySet(Set<T> set) {
		return set != null ? set : new HashSet<>();
	}
	
	/**
	 * 集合的某个索引对应的值非NULL返回字符串
	 * 
	 * @param collection 集合
	 * @param index      索引
	 * @return 操作后的数据
	 */
	public static <T> String toStringIfNotNull(Iterable<T> collection, int index) {
		if (Checker.beEmpty(collection) || index < 0 || Iterables.size(collection) <= index)
			return null;
		T t = Iterables.get(collection, index);
		return toStringIfNotNull(t);
	}
	
	/**
	 * MAP 中 某个列表对应的索引 非NULL返回字符串
	 * 
	 * @param t     待判断Map对象
	 * @param key   map的KEY
	 * @param index 列表索引
	 * @return 操作后的数据
	 */
	public static <T> T getKeyOfIndexValueIfNotNull(Map<String, List<T>> t, String key, int index) {
		if (Checker.beEmpty(t) || Checker.beEmpty(key) || index < 0)
			return null;
		List<T> tlist = t.get(key);
		if (Checker.beEmpty(tlist) || tlist.size() <= index)
			return null;
		return tlist.get(index);
	}
}
