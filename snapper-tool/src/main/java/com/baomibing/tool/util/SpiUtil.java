/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.util;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.ServiceLoader;

/**
 * SpiUtil
 *
 * @author zening 2022/7/28 15:25
 * @version 1.0.0
 */
public abstract class SpiUtil {

    public static <T> List<T> load(Class<T> clazz) {
        List<T> services = Lists.newArrayList();
        ServiceLoader<T> loader = ServiceLoader.load(clazz);
        loader.forEach(services::add);
        return services;
    }
}
