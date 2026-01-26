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

/**
 * 运行时工具
 * 
 * @author zening
 * @since 1.0.0
 */
public abstract class RuntimeUtil {
	
	/**
	 * 正在执行的方法类名
	 * 
	 * @return 操作后的字符串
	 */
	public static String getExecutingClassName() {
		final StackTraceElement e = Thread.currentThread().getStackTrace()[2];
		return e.getClassName();
	}
	
	/**
	 * 正在执行的方法名
	 * 
	 * @return 操作后的字符串
	 */
	public static String getExecutingMethodName() {
		final StackTraceElement e = Thread.currentThread().getStackTrace()[2];
		return e.getMethodName();
	}
	
	/**
	 * 正在执行的方法+类名
	 * 
	 * @return 操作后的字符串
	 */
	public static String getExecutingDetailMethodName () {
		final StackTraceElement e = Thread.currentThread().getStackTrace()[2];
		final String c = e.getClassName();
	    final String m = e.getMethodName();
	    return c + "." + m;
	}

}
