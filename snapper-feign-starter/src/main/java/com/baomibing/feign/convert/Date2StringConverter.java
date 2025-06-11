/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.feign.convert;


import org.joda.time.DateTime;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;

import static com.baomibing.tool.constant.Formats.DEFAULT_DATE_TIME_FORMAT;

public class Date2StringConverter implements Converter<Date, String> {
    @Override
    public String convert(Date source) {
        return new DateTime(source).toString(DEFAULT_DATE_TIME_FORMAT);
    }
}
