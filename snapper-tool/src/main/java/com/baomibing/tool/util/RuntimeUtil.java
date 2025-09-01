/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
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
