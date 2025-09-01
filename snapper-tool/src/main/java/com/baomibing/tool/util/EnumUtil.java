/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.util;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * 枚举工具
 * 
 * @author zening
 * @since 1.0.0
 */
public abstract class EnumUtil {

	public static <T extends Enum<T>> String[] getStringValues(Class<T> enumClass) {
		return getStringValuesWithStringExtractor(enumClass, Enum::name);
	}

	public static <T extends Enum<T>> String[] getStringValuesWithStringExtractor(Class<T> enumClass,
			Function<? super T, String> extractor) {
		return Stream.of(enumClass.getEnumConstants()).map(extractor).toArray(String[]::new);
	}
}
