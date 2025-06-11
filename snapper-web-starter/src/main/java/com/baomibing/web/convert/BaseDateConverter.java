/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.web.convert;

import com.baomibing.tool.util.Checker;

import java.util.Map;
public abstract class BaseDateConverter {

	protected String matchKey(String source, Map<String, String> formatMap) {
        if (Checker.beEmpty(source) || Checker.beEmpty(formatMap)) {
            return null;
        }
        source = source.trim();
        for (Map.Entry<String, String> entry : formatMap.entrySet()) {
            if (source.matches(entry.getValue())) {
               return entry.getKey();
            }
        }
        throw new IllegalArgumentException("Invalid date format:'" + source + "'");
    }

}
