/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.user;


import com.baomibing.tool.constant.Strings;

/**
 * 用户工具类
 *
 * @author zening
 * @version 1.0.0
 */
public class UserUtil {

	public static Boolean beSuper(String userName) {
		return Strings.SUPER.equalsIgnoreCase(userName);
	}
}
