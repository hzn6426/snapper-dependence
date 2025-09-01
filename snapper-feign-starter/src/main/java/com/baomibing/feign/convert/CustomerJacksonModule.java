/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.feign.convert;


import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.PackageVersion;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.baomibing.tool.constant.Formats.*;

public class CustomerJacksonModule extends SimpleModule {


	private static final long serialVersionUID = 1L;

	public CustomerJacksonModule() {
        super(PackageVersion.VERSION);
        this.addDeserializer(LocalDateTime.class, new CustomerLocalDateTimeDeserializer());
        this.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        this.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));
        this.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
        this.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        this.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));
        this.addSerializer(Long.class, ToStringSerializer.instance);
        this.addSerializer(Long.TYPE, ToStringSerializer.instance);
        this.addSerializer(BigInteger.class, ToStringSerializer.instance);
        this.addSerializer(BigDecimal.class, ToStringSerializer.instance);
    }
    
}