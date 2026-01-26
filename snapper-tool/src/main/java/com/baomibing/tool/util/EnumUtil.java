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
