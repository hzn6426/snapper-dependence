/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.log;

import lombok.extern.slf4j.Slf4j;

/**
 * 日志埋点工具，生成对应的埋点文件
 * 
 * @author zening
 * @since 1.0.0
 */
@Slf4j
public abstract class PointLog {

    private static final String MSG_PATTERN = "{}|{}|{}";

    public static void info(String message, Object...params) {
    	log.info(message, params);
    }
    
    public static void error(String message, Object...params) {
    	log.info(message, params);
    }

    public static void debug(String message, Object...params) {
        log.debug(message, params);
    }

    public static void debug(String id, String type, String message) {
        log.debug(MSG_PATTERN, id, type, message);
    }

}
