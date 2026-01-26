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

@Slf4j
public class GroovyLog {

    private static final String MSG_PATTERN = "{}|{}|{}";

    public  void info(String message, Object...params) {
    	log.info(message, params);
    }
    
    public  void error(String message, Object...params) {
    	log.info(message, params);
    }

    public  void debug(String message, Object...params) {
        log.debug(message, params);
    }

    public  void debug(String id, String type, String message) {
        log.debug(MSG_PATTERN, id, type, message);
    }

    public  void warn(String message, Object...params) {
        log.warn(message, params);
    }

    public  void trace(String message, Object...params) {
        log.trace(message, params);
    }

}
