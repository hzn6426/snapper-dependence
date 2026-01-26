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

import com.baomibing.core.wrap.EntrustWarpper;
import com.baomibing.tool.util.Checker;

import java.util.HashMap;
import java.util.Map;

/**
 * mapper绑定业务权限上下文
 * 
 * @author zening
 * @since 1.0.0
 */
public class MapperAuthContext {

	private static final ThreadLocal<Map<String, EntrustWarpper>> THREAD_LOCAL = new ThreadLocal<>();

    public static void set(String key, EntrustWarpper value) {
		if (Checker.beEmpty(key) || Checker.beNull(value))
			return;
        Map<String, EntrustWarpper> map = getLocalMap();
        map.put(key, value);
    }


    public static EntrustWarpper get(String key) {
		if (Checker.beEmpty(key))
			return null;
        Map<String, EntrustWarpper> map = getLocalMap();
        return map.getOrDefault(key, null);
    }

    public static Map<String, EntrustWarpper> getLocalMap() {
        Map<String, EntrustWarpper> map = THREAD_LOCAL.get();
        if (map == null) {
            map = new HashMap<>(10);
            THREAD_LOCAL.set(map);
        }
        return map;
    }

    public static void setLocalMap(Map<String, EntrustWarpper> threadLocalMap) {
        THREAD_LOCAL.set(threadLocalMap);
    }
    
    public static void clear(String key) {
		if (Checker.beEmpty(key))
			return;
    	 Map<String, EntrustWarpper> map = THREAD_LOCAL.get();
    	 map.remove(key);
    }
    
    public static void clearAll() {
	   	 Map<String, EntrustWarpper> map = THREAD_LOCAL.get();
	   	 map.clear();
    }
}
