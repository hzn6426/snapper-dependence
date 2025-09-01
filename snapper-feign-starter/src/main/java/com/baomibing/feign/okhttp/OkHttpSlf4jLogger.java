/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.feign.okhttp;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OkHttpSlf4jLogger implements OKHttpLogger {

	@Override
    public void log(String message) {
        log.info(message);
    }
}
