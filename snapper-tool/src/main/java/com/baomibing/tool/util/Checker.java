/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.util;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * 用于检查判断工具类，该类的方法总是返回boolean类型
 * 
 * @author zening
 * @since 1.0.0
 */
public abstract class Checker {

	/**
	 * 断言对象不为null
	 * 
	 * @param reference 判断的对象
	 * @return 断言正确返回true,否则为false
	 */
	public static <T> boolean beNotNull(T reference) {
		if (reference == null) {
			return false;
		}
		return true;
	}

	/**
	 * 应用是否为空
	 * 
	 * @param reference
	 * @return 断言正确返回true,否则为false
	 */
	public static <T> boolean beNull(T reference) {
		return !beNotNull(reference);
	}

	/**
	 * 断言集合不为null
	 * 
	 * @param collection 判断的集合
	 * @return 断言正确返回true,否则为false
	 */
	public static <T> boolean beNotNull(Iterable<T> collection) {
		if (IterableUtils.isEmpty(collection)) {
			return false;
		}
		return true;
	}

	public static <T> boolean beNull(Iterable<T> collection) {
		return !beNotNull(collection);
	}

	/**
	 * 断言集合不为null且不为空
	 * 
	 * @param coll 判断的集合
	 * @return 断言正确返回true,否则为false
	 */
	public static <T> Boolean beNotEmpty(Iterable<T> coll) {
		return !IterableUtils.isEmpty(coll);
	}

	/**
	 * 判断集合是否为空
	 * 
	 * @param <T>
	 * @param coll
	 * @return 断言正确返回true,否则为false
	 */
	public static <T> Boolean beEmpty(Iterable<T> coll) {
		return !beNotEmpty(coll);
	}

	/**
	 * 判断map不为null且不为空
	 * 
	 * @param map 判断的map
	 * @return 断言正确返回true,否则为false
	 */
	public static <K, V> boolean beNotEmpty(Map<K, V> map) {
		return MapUtils.isNotEmpty(map);
	}

	/**
	 * 判断Map为空
	 * 
	 * @param map 判断的Map
	 * @return 断言正确返回true,否则为false
	 */
	public static <K, V> boolean beEmpty(Map<K, V> map) {
		return !beNotEmpty(map);
	}

	/**
	 * 断言数组不能为空
	 * 
	 * @param arr 判断的数组
	 * @return 断言正确返回true,否则为false
	 */
	public static <T> Boolean beNotEmpty(T[] arr) {
		return ArrayUtils.isNotEmpty(arr);
	}

	/**
	 * 判断数组是否为空
	 * 
	 * @param arr 判断的数组
	 * @return 断言正确返回true,否则为false
	 */
	public static <T> Boolean beEmpty(T[] arr) {
		return !beNotEmpty(arr);
	}

	/**
	 * 断言字符串不为null，不为空
	 * 
	 * @param cs 判断的字符串
	 * @return 断言正确返回true,否则为false
	 */
	public static Boolean beNotEmpty(CharSequence cs) {
		return StringUtils.isNotBlank(cs);
	}

	/**
	 * 判断字符串是否为空
	 * 
	 * @param cs 判断的字符串
	 * @return 断言正确返回true,否则为false
	 */
	public static Boolean beEmpty(CharSequence cs) {
		return !beNotEmpty(cs);
	}

	/**
	 * 断言字符串不为null，不为空，不是全空格
	 * 
	 * @param cs 判断的字符串
	 * @return 断言正确返回true,否则为false
	 */
	public static Boolean beNotBlank(CharSequence cs) {
		return StringUtils.isNotBlank(cs);
	}

	/**
	 * 判断字符串是否为空（空格也算空）
	 * 
	 * @param cs 判断的字符串
	 * @return 断言正确返回true,否则为false
	 */
	public static Boolean beBlank(CharSequence cs) {
		return StringUtils.isBlank(cs);
	}

	/**
	 * 判断两个数字A是否大于B
	 * 
	 * @param a 数字A
	 * @param b 数字B
	 * @return 断言正确返回true,否则为false
	 */
	public static Boolean beGreaterThan(Number a, Number b) {
		if (a == null || b == null) {
			return false;
		}
		return a.doubleValue() - b.doubleValue() > 0;
	}

	/**
	 * 判断两个数字A大于等于B
	 * 
	 * @param a 数字A
	 * @param b 数字B
	 * @return 断言正确返回true,否则为false
	 */
	public static Boolean beGreaterOrEqualThan(Number a, Number b) {
		if (a == null || b == null) {
			return false;
		}
		return a.doubleValue() > b.doubleValue() || a.doubleValue() == b.doubleValue();
	}

	/**
	 * 判断两个数字是否相等
	 * 
	 * @param a 数字A
	 * @param b 数字B
	 * @return 断言正确返回true,否则为false
	 */
	public static Boolean beEqualThan(Number a, Number b) {
		if (a == null || b == null) {
			return false;
		}
		return a.doubleValue() == b.doubleValue();
	}

	/**
	 * 判断两个数字是否不相等
	 * 
	 * @param a 数字A
	 * @param b 数字B
	 * @return 断言正确返回true,否则为false
	 */
	public static Boolean beNotEqual(Object a, Object b) {
		return !a.toString().equals(b.toString());
	}

}
