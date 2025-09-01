/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.feign.convert;


import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.format.FormatterRegistry;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;


public class DateFormatterRegistrar implements FeignFormatterRegistrar{

	public DateFormatterRegistrar() {
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {
        registry.addConverter(Date.class, String.class, new Date2StringConverter());
        registry.addConverter(LocalDateTime.class, String.class, new LocalDateTime2StringConverter());
        registry.addConverter(LocalDate.class, String.class, new LocalDate2StringConverter());
        registry.addConverter(LocalTime.class, String.class, new LocalTime2StringConverter());
        registry.addConverter(String.class, Date.class, new String2DateConverter());
        registry.addConverter(String.class, LocalDate.class, new String2LocalDateConverter());
        registry.addConverter(String.class, LocalDateTime.class, new String2LocalDateTimeConverter());
        registry.addConverter(String.class, LocalTime.class, new String2LocalTimeConverter());
    }

}
