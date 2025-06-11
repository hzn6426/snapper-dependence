/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
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
