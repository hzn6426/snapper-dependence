/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.ds;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.baomibing.tool.util.Checker;

/**
 * DataSourceContext
 *
 * @author zening 2022/9/21 09:35
 * @version 1.0.0
 */
public class DataSourceContext {

    private static final TransmittableThreadLocal<DataSource> locale = new TransmittableThreadLocal<>();


    public static void putDataSources(DataSource dataSources) {
        locale.set(dataSources);
    }

    public static DataSource currentDataSources() {
        return locale.get();
    }

    public static boolean exist() {
        return Checker.beNotNull(currentDataSources());
    }

    public static void remove() {
        locale.remove();
    }

}
