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

package com.baomibing.tool.tenant;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.Optional;

/**
 * TenantFunctionContext
 *
 * @author zening 2024/10/18 11:11
 * @version 1.0.0
 **/
public abstract class TenantFunctionContext {

    private static final TransmittableThreadLocal<TenantFunctionConsume> locale = new TransmittableThreadLocal<>();

    public static void putFunction(TenantFunctionConsume function) {
        if (!exist()) {
            locale.set(function);
        }
    }

    public static Optional<TenantFunctionConsume> currentFunction() {
        if (locale.get() == null) {
            return Optional.empty();
        }
        return Optional.of(locale.get());
    }

    public static boolean exist() {
        return currentFunction().isPresent();
    }

    public static void remove() {
        locale.remove();
    }
}
