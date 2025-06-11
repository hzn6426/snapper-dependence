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
