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
package com.baomibing.core.wrap;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * ColumnPermWrap
 *
 * @author zening 2023/6/9 16:13
 * @version 1.0.0
 **/
@Data @Accessors(chain = true)
public class ColumnPermWrap {

    private String tableName;

    private String columnName;

    //列权限-特殊列(各种聚合函数 或者ifNull, switch case...生成的列)
    private Boolean beSpecial;
}
