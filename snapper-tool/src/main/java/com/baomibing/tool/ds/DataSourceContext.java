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
