/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.feign.convert;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.baomibing.tool.constant.Formats.*;

public class String2LocalTimeConverter extends BaseDateConverter implements Converter<String, LocalTime> {

    private final Map<String, String> formatMap;

    public String2LocalTimeConverter() {
        io.vavr.collection.LinkedHashMap<String, String> map = io.vavr.collection.LinkedHashMap.empty();
        map.put(DEFAULT_TIME_FORMAT, DEFAULT_DATE_TIME_FORMAT_MATCHES)
            .put(DEFAULT_TIME_FORMAT_CN, DEFAULT_TIME_FORMAT_CN_MATCHES);
        formatMap = map.toJavaMap();
    }


    @Override
    public LocalTime convert(String source) {
        String format = matchKey(source, formatMap);
        return LocalTime.parse(source, DateTimeFormatter.ofPattern(format));
    }
}
