/*
 * Copyright (c) 2020-2025, zening (316279828@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomibing.web.convert;

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
