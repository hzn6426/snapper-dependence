package com.baomibing.tool.util;

/**
 * ClassUtil
 *
 * @author zening 2024/8/29 10:04
 * @version 1.0.0
 **/
public abstract class ClassUtil {

    public static boolean beNotClassOnly(Class<?> clazz, Object o) {
        return o != null && clazz.isAssignableFrom(o.getClass()) && o.getClass() != clazz;
    }
}
