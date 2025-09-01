/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.feign.convert;


import com.baomibing.tool.util.Checker;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase;
import com.google.common.collect.ImmutableMap;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

import static com.baomibing.tool.constant.Formats.*;

public class CustomerLocalDateTimeDeserializer extends JSR310DateTimeDeserializerBase<LocalDateTime> {
    private static final Map<String, String> formatMap = ImmutableMap
            .of(DEFAULT_DATE_FORMAT, DEFAULT_DATE_FORMAT_MATCHES,
                DEFAULT_DATE_FORMAT_CN, DEFAULT_DATE_FORMAT_CN_MATCHES,
                SLASH_DATE_FORMAT, SLASH_DATE_FORMAT_MATCHES,

                DEFAULT_DATE_TIME_FORMAT, DEFAULT_DATE_TIME_FORMAT_MATCHES,
                DEFAULT_DATE_TIME_FORMAT_CN, DEFAULT_DATE_TIME_FORMAT_CN_MATCHES,
                SLASH_DATE_TIME_FORMAT, SLASH_DATE_TIME_FORMAT_MATCHES
            );
    public CustomerLocalDateTimeDeserializer() {
        this(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public CustomerLocalDateTimeDeserializer(DateTimeFormatter formatter) {
        super(LocalDateTime.class, formatter);
    }

    protected CustomerLocalDateTimeDeserializer(CustomerLocalDateTimeDeserializer base, Boolean leniency) {
        super(base, leniency);
    }

    @Override
    protected CustomerLocalDateTimeDeserializer withLeniency(Boolean leniency) {
        return new CustomerLocalDateTimeDeserializer(this, leniency);
    }

    @Override
    protected JSR310DateTimeDeserializerBase<LocalDateTime> withShape(JsonFormat.Shape shape) {
        return null;
    }

    @Override
    protected JSR310DateTimeDeserializerBase<LocalDateTime> withDateFormat(DateTimeFormatter formatter) {
        return new CustomerLocalDateTimeDeserializer(formatter);
    }

    protected String matchKey(String source, Map<String, String> map) {
        if (Checker.beEmpty(source) || Checker.beEmpty(map)) {
            throw new IllegalArgumentException("Invalid date format:'" + source + "'");
        }
        source = source.trim();
        for (Map.Entry<String, String> entry : formatMap.entrySet()) {
            if (source.matches(entry.getValue())) {
                return entry.getKey();
            }
        }
        throw new IllegalArgumentException("Invalid date format:'" + source + "'");
    }

    private LocalDateTime convert(String source) {
        String format = matchKey(source, formatMap);
        Date dateToConvert = DateTime.parse(source, DateTimeFormat.forPattern(format)).toDate();
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        // 字符串
        if (parser.hasTokenId(JsonTokenId.ID_STRING)) {
            String string = parser.getText().trim();
            if (string.isEmpty()) {
                return null;
            }

            try {
                if (_formatter == null) {
                    return convert(string);
                }
                if (_formatter == DateTimeFormatter.ISO_LOCAL_DATE_TIME) {
                    // JavaScript by default includes time and zone in JSON serialized Dates (UTC/ISO instant format).
                    if (string.length() > 10 && string.charAt(10) == 'T') {
                        if (string.endsWith("Z")) {
                            return LocalDateTime.ofInstant(Instant.parse(string), ZoneOffset.UTC);
                        } else {
                            return LocalDateTime.parse(string, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        }
                    }
                    return convert(string);
                }

                return LocalDateTime.parse(string, this._formatter);
            } catch (DateTimeException e) {
                return _handleDateTimeException(context, e, string);
            }
        }
        // 数组
        if (parser.isExpectedStartArrayToken()) {
            JsonToken t = parser.nextToken();
            if (t == JsonToken.END_ARRAY) {
                return null;
            }
            if ((t == JsonToken.VALUE_STRING || t == JsonToken.VALUE_EMBEDDED_OBJECT) && context.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                final LocalDateTime parsed = deserialize(parser, context);
                if (parser.nextToken() != JsonToken.END_ARRAY) {
                    handleMissingEndArrayForSingle(parser, context);
                }
                return parsed;
            }
            if (t == JsonToken.VALUE_NUMBER_INT) {
                LocalDateTime result;

                int year = parser.getIntValue();
                int month = parser.nextIntValue(-1);
                int day = parser.nextIntValue(-1);
                int hour = parser.nextIntValue(-1);
                int minute = parser.nextIntValue(-1);

                t = parser.nextToken();
                if (t == JsonToken.END_ARRAY) {
                    result = LocalDateTime.of(year, month, day, hour, minute);
                } else {
                    int second = parser.getIntValue();
                    t = parser.nextToken();
                    if (t == JsonToken.END_ARRAY) {
                        result = LocalDateTime.of(year, month, day, hour, minute, second);
                    } else {
                        int partialSecond = parser.getIntValue();
                        if (partialSecond < 1_000 &&
                                !context.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS))
                            partialSecond *= 1_000_000; // value is milliseconds, convert it to nanoseconds
                        if (parser.nextToken() != JsonToken.END_ARRAY) {
                            throw context.wrongTokenException(parser, handledType(), JsonToken.END_ARRAY,
                                    "Expected array to end");
                        }
                        result = LocalDateTime.of(year, month, day, hour, minute, second, partialSecond);
                    }
                }
                return result;
            }
            context.reportInputMismatch(handledType(),
                    "Unexpected token (%s) within Array, expected VALUE_NUMBER_INT",
                    t);
        }
        // 数字
        if (parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            return Instant.ofEpochMilli(parser.getLongValue()).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
        }
        if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
            return (LocalDateTime) parser.getEmbeddedObject();
        }

        return _handleUnexpectedToken(context, parser, "当前参数需要数组、字符串、时间戳。");
    }

}

