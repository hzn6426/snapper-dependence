/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.feign.convert;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;
import java.util.Map;

import static com.baomibing.tool.constant.Formats.*;

public class String2DateConverter  extends BaseDateConverter implements Converter<String, Date> {

    private final Map<String, String> formatMap;

    public String2DateConverter() {
        io.vavr.collection.LinkedHashMap<String, String> map = io.vavr.collection.LinkedHashMap.empty();
        map.put(DEFAULT_YEAR_FORMAT, DEFAULT_YEAR_FORMAT_MATCHES)
            .put(DEFAULT_DATE_FORMAT, DEFAULT_DATE_FORMAT_MATCHES)
            .put(DEFAULT_DATE_TIME_FORMAT, DEFAULT_DATE_TIME_FORMAT_MATCHES)
            .put(SLASH_DATE_FORMAT, SLASH_DATE_FORMAT_MATCHES)
            .put(SLASH_DATE_TIME_FORMAT, SLASH_DATE_TIME_FORMAT_MATCHES)
            .put(DEFAULT_DATE_FORMAT_CN, DEFAULT_DATE_FORMAT_CN_MATCHES)
            .put(DEFAULT_DATE_TIME_FORMAT_CN, DEFAULT_DATE_TIME_FORMAT_CN_MATCHES);
        formatMap = map.toJavaMap();
    }

    @Override
    public Date convert( String source) {
        String format = matchKey(source, formatMap);
        return DateTime.parse(source, DateTimeFormat.forPattern(format)).toDate();
    }

}
