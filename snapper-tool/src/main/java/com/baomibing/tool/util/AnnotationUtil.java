/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * AnnotationUtil
 *
 * @author zening 2022/4/27 14:48
 * @version 1.0.0
 */
public abstract class AnnotationUtil {
    /**
     * Changes the annotation value for the given key of the given annotation to newValue and returns
     * the previous value.
     */
    @SuppressWarnings("unchecked")
    public static Object changeAnnotationValue(Annotation annotation, String key, Object newValue){
        Object handler = Proxy.getInvocationHandler(annotation);
        Field f;
        try {
            f = handler.getClass().getDeclaredField("memberValues");
        } catch (NoSuchFieldException | SecurityException e) {
            throw new IllegalStateException(e);
        }
        f.setAccessible(true);
        Map<String, Object> memberValues;
        try {
            memberValues = (Map<String, Object>) f.get(handler);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        Object oldValue = memberValues.get(key);
        if (oldValue == null || oldValue.getClass() != newValue.getClass()) {
            throw new IllegalArgumentException();
        }
        memberValues.put(key,newValue);
        return oldValue;
    }
}
