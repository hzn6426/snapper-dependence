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
