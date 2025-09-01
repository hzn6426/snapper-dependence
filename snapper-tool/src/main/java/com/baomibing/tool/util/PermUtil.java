/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.util;

import com.baomibing.tool.constant.Strings;

/**
 * PermUtil
 *
 * @author zening 2023/7/20 10:16
 * @version 1.0.0
 **/
public abstract class PermUtil {

    public static String parseUser(String s) {
        if (Checker.beEmpty(s)) {
            return Strings.EMPTY;
        }
        return s.contains(Strings.HASH) ? s.split(Strings.HASH)[1] : s;
    }

    public static String parseGroup(String s) {
        if (Checker.beEmpty(s)) {
            return Strings.EMPTY;
        }
        return s.contains(Strings.HASH) ? s.split(Strings.HASH)[0] : s;
    }

}
