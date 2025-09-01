/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.util;

/**
 * 数组工具类
 * 
 * @author zening
 * @since 1.0.0
 */
public abstract class ArrayUtil {

	/**
	 * 查找字符数组，忽略大小写
	 * 
	 * @param array        字符串数组
	 * @param objectToFind 要查找的字符
	 * @return 是否包含
	 */
	public static boolean containsIgnoreCase(final String[] array, final String objectToFind) {
		boolean beContains = false;
		if (array == null || objectToFind == null || objectToFind.isEmpty())
			return beContains;
		for (String s : array) {
			if (s.equalsIgnoreCase(objectToFind)) {
				return true;
			}
		}
		return beContains;
	}

	public static boolean containsIgnoreCase(final char[] array, final char objectToFind) {
		boolean beContains = false;
		if (array == null ) return beContains;
		for (char s : array) {
			if (Character.toUpperCase(s) == Character.toUpperCase(objectToFind)) {
				return true;
			}
		}
		return beContains;
	}
}
