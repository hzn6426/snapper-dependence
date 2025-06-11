/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.web.convert;


import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.baomibing.tool.constant.Formats.*;

public class String2LocalDateConverter extends BaseDateConverter implements Converter<String, LocalDate> {

    private final Map<String, String> formatMap;

    public String2LocalDateConverter() {
        io.vavr.collection.LinkedHashMap<String, String> map = io.vavr.collection.LinkedHashMap.empty();
        map.put(DEFAULT_DATE_FORMAT, DEFAULT_DATE_FORMAT_MATCHES)
            .put(SLASH_DATE_FORMAT, SLASH_DATE_FORMAT_MATCHES)
            .put(DEFAULT_DATE_FORMAT_CN, DEFAULT_DATE_FORMAT_CN_MATCHES);
        formatMap = map.toJavaMap();
    }



    @Override
    public LocalDate convert(String source) {
        String format = matchKey(source, formatMap);
        return  LocalDate.parse(source, DateTimeFormatter.ofPattern(format));
    }

}
