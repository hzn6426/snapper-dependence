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
package com.baomibing.tool.constant;

/**
 * Formats
 *
 * @author zening 2022/2/11 14:47
 * @version 1.0.0
 */
public abstract class Formats {

    public final static String DEFAULT_YEAR_FORMAT = "yyyy";
    public final static String DEFAULT_MONTH_FORMAT = "yyyy-MM";
    public final static String DEFAULT_MONTH_FORMAT_SLASH = "yyyy/MM";
    public final static String DEFAULT_MONTH_FORMAT_CN = "yyyy年MM月";
    public final static String DEFAULT_WEEK_FORMAT = "yyyy-ww";
    public final static String DEFAULT_WEEK_FORMAT_CN = "yyyy年ww周";
    public final static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public final static String DEFAULT_DATE_FORMAT_CN = "yyyy年MM月dd日";
    public final static String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public final static String DEFAULT_T_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public final static String DEFAULT_DATE_TIME_FORMAT_CN = "yyyy年MM月dd日HH时mm分ss秒";
    public final static String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    public final static String DEFAULT_TIME_FORMAT_CN = "HH时mm分ss秒";
    public final static String DAY = "DAY";
    public final static String MONTH = "MONTH";
    public final static String WEEK = "WEEK";
    public final static String yearMonth = "yyyyMM";
    public final static String YEAR_MONTH_DAY_HOUR_FORMAT = "yyyy-MM-dd HH";
    public final static String YEAR_MONTH_DAY_HOUR_MINUTE_FORMAT = "yyyy-MM-dd HH:mm";
    public final static String SLASH_YEAR_MONTH_DAY_HOUR_FORMAT = "yyyy/MM/dd HH";
    public final static String SLASH_YEAR_MONTH_DAY_HOUR_MINUTE_FORMAT = "yyyy/MM/dd HH:mm";
    public static final String SLASH_DATE_FORMAT = "yyyy/MM/dd";
    public static final String SLASH_DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";

    public static final String DEFAULT_YEAR_FORMAT_MATCHES = "^\\d{4}";
    public static final String DEFAULT_YEAR_MONTH_FORMAT_MATCHES = "^\\d{4}-\\d{1,2}$";
    public static final String DEFAULT_YEAR_MONTH_DAY_HOUR_FORMAT_MATCHES = "^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}";
    public static final String DEFAULT_YEAR_MONTH_DAY_HOUR_MINUTE_FORMAT_MATCHES = "^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$";

    public static final String DEFAULT_DATE_FORMAT_MATCHES = "^\\d{4}-\\d{1,2}-\\d{1,2}$";
    public static final String DEFAULT_DATE_TIME_FORMAT_MATCHES = "^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$";
    public static final String DEFAULT_DATE_FORMAT_CN_MATCHES = "^\\d{4}年\\d{1,2}月\\d{1,2}日$";
    public static final String DEFAULT_DATE_TIME_FORMAT_CN_MATCHES = "^\\d{4}年\\d{1,2}月\\d{1,2}日\\d{1,2}时\\d{1,2}分\\d{1,2}秒$";
    public static final String DEFAULT_TIME_FORMAT_CN_MATCHES = "^\\d{1,2}时\\d{1,2}分\\d{1,2}秒$";

    public static final String SLASH_YEAR_MONTH_FORMAT_MATCHES = "\\d{4}/\\d{1,2}$";
    public static final String SLASH_YEAR_MONTH_DAY_HOUR_MINUTE_FORMAT_MATCHES = "^\\d{4}/\\d{1,2}/\\d{1,2} {1}\\d{1,2}:\\d{1,2}$";
    public static final String SLASH_DATE_FORMAT_MATCHES = "^\\d{4}/\\d{1,2}/\\d{1,2}$";
    public static final String SLASH_DATE_TIME_FORMAT_MATCHES = "^\\d{4}/\\d{1,2}/\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$";

    public static final String TIME_STAMP_FORMAT = "yyyyMMddHHmmssSSS";

}
