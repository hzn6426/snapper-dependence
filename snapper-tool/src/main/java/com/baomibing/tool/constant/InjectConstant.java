/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.constant;

/**
 * InjectConstant
 *
 * @author zening 2023/3/16 10:51
 * @version 1.0.0
 **/
public abstract class InjectConstant {

    //是否支持SQL注入 -- KEY
    public static final String TAG_FOR_SQL_INJECT_KEY = "_TAG_FOR_SQL_INJECT_KEY";
    //是否支持SQL注入 -- VALUE
    public static final String TAG_FOR_SQL_INJECT_VALUE = "_SUPPORTED_SQL_INJECT";
    //mapper前缀
    public static final String MAPPER_PREFIX_FOR_SQL_INJECT = "_MAPPER_PREFIX_FOR_SQL_INJECT";
}
