/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.feign.convert;


import org.springframework.core.convert.converter.Converter;

import java.time.LocalDateTime;

import static com.baomibing.tool.constant.Formats.DEFAULT_DATE_TIME_FORMAT;

public class LocalDateTime2StringConverter implements Converter<LocalDateTime, String> {
	@Override
	public String convert(LocalDateTime source) {
		return new org.joda.time.LocalDateTime(source).toString(DEFAULT_DATE_TIME_FORMAT);
	}
}