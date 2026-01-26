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

package com.baomibing.tool.log;

import lombok.extern.slf4j.Slf4j;

/**
 * SQLLog
 *
 * @author zening 2023/8/8 18:15
 * @version 1.0.0
 **/
@Slf4j
public class SQLLog {

    public static void info(String message, Object...params) {
        log.info(message, params);
    }

    public static void error(String message, Object...params) {
        log.error(message, params);
    }

    public static void debug(String message, Object...params) {
        log.debug(message, params);
    }

    public static void warn(String message, Object...params) {
        log.warn(message, params);
    }

    public static void trace(String message, Object...params) {
        log.trace(message, params);
    }
}
