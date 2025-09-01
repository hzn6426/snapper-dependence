/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.context;


import com.baomibing.core.wrap.EntrustWarpper;
import com.baomibing.tool.util.Checker;

import java.util.HashMap;
import java.util.Map;

/**
 * 业务权限上下文存储
 * 
 * @author zening
 * @since 1.0.0
 */
public class BusinessAuthContext {
	
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
		if (Checker.beNull(map))
			return;
    	 map.remove(key);
    }

}
