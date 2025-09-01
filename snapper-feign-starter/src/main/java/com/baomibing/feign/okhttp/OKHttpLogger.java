/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.feign.okhttp;

import okhttp3.internal.platform.Platform;

public interface OKHttpLogger {

	OKHttpLogger DEFAULT = message -> Platform.get().log(Platform.INFO, message, null);

    /**
     * log
     *
     * @param message message
     */
    void log(String message);
}
