/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
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
