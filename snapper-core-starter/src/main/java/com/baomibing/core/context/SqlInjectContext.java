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
package com.baomibing.core.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.baomibing.tool.constant.InjectConstant;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.util.Checker;

import java.util.HashMap;
import java.util.Map;

/**
 * SQL 注入上下文,用来进行高级查询 value值应该通过 {@link com.baomibing.core.wrap.AdvanceSearchWrap} 来构造
 *
 * @author zening 2023/3/16 10:11
 * @version 1.0.0
 **/
public class SqlInjectContext {

    private static final TransmittableThreadLocal<Map<String, String>> THREAD_LOCAL = new TransmittableThreadLocal<>();
    public static void set(String mapperId, String sql) {
        if (Checker.beEmpty(mapperId) || Checker.beNull(mapperId)) return;
        Map<String, String> map = getLocalMap();
        String prefix = map.get(InjectConstant.MAPPER_PREFIX_FOR_SQL_INJECT);
        if (Checker.beNotEmpty(prefix)) {
            mapperId = prefix + Strings.DOT + mapperId;
        }
        map.put(mapperId, sql);
    }

    public static void setIgnoreMapperPrefix(String mapperId, String sql) {
        if (Checker.beEmpty(mapperId) || Checker.beNull(mapperId)) return;
        Map<String, String> map = getLocalMap();
        map.put(mapperId, sql);
    }


    public static String get(String mapperId) {
        if (Checker.beEmpty(mapperId)) return null;
        Map<String, String> map = getLocalMap();
        return map.getOrDefault(mapperId, null);
    }

    public static Map<String, String> getLocalMap() {
        Map<String, String> map = THREAD_LOCAL.get();
        if (map == null) {
            map = new HashMap<>(10);
            THREAD_LOCAL.set(map);
        }
        return map;
    }

    public static void setLocalMap(Map<String, String> threadLocalMap) {
        THREAD_LOCAL.set(threadLocalMap);
    }

    public static void clear(String mapperId) {
        if (Checker.beEmpty(mapperId)) return;
        Map<String, String> map = THREAD_LOCAL.get();
        map.remove(mapperId);
    }

    public static void clearAll() {
        Map<String, String> map = THREAD_LOCAL.get();
        map.clear();
    }
}
