/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

/**
 * JSONUtil
 *
 * @author zening 2022/7/15 10:49
 * @version 1.0.0
 */
public abstract class JSONUtil {

    public static boolean isJson(String s) {
        return beJsonObject(s) || beJsonArray(s);
    }

    private static boolean beJsonObject(String s) {
        if (Checker.beEmpty(s)) {
            return false;
        }
        try {
            JSON.parseObject(s);
            return true;
        } catch (Exception je) {
            return false;
        }
    }

    private static boolean beJsonArray(String s) {
        if (Checker.beEmpty(s)) {
            return false;
        }
        try {
            JSONArray.parseArray(s);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
