/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.feign.convert;


import org.joda.time.LocalDateTime;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;

import static com.baomibing.tool.constant.Formats.DEFAULT_DATE_FORMAT;

public class LocalDate2StringConverter implements Converter<LocalDate, String> {
    @Override
    public String convert(LocalDate source) {
        return new LocalDateTime(source).toString(DEFAULT_DATE_FORMAT);
    }
}