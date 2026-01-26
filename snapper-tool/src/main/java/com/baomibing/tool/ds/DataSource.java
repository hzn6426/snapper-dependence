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
package com.baomibing.tool.ds;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 数据源 - 用来进行辅助切换为对应的数据源
 *
 * @author zening 2022/9/21 09:36
 * @version 1.0.0
 */
@Data
@Accessors(chain = true)
public class DataSource {
    //数据源名称
    private String name;

//    private String method;
//
//    private String url;
//
//    private String token;
//
//    private String params;
}
