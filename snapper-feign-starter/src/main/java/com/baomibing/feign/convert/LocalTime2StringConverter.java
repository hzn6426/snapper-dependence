/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.feign.convert;


import org.springframework.core.convert.converter.Converter;

import java.time.LocalTime;

import static com.baomibing.tool.constant.Formats.DEFAULT_TIME_FORMAT;

public class LocalTime2StringConverter implements Converter<LocalTime, String> {
    @Override
    public String convert(LocalTime source) {
        return new org.joda.time.LocalTime(source).toString(DEFAULT_TIME_FORMAT);
    }
}
