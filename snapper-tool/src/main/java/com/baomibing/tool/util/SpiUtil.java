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
