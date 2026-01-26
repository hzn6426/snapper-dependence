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
