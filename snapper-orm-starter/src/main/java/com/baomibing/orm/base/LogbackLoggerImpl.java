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
package com.baomibing.orm.base;

import com.baomibing.tool.log.SQLLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.logging.Log;

/**
 * LogbackLoggerImpl
 *
 * @author zening 2023/5/11 10:03
 * @version 1.0.0
 **/
@Slf4j
public class LogbackLoggerImpl implements Log {

    public LogbackLoggerImpl(String clazz) {
        // Do Nothing
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public void error(String s, Throwable e) {
        e.printStackTrace(System.err);
        SQLLog.error(s);
    }

    @Override
    public void error(String s) {
        SQLLog.error(s);
    }

    @Override
    public void debug(String s) {
        SQLLog.debug(s);
    }

    @Override
    public void trace(String s) {
        SQLLog.trace(s);
    }

    @Override
    public void warn(String s) {
        SQLLog.warn(s);
    }



}
