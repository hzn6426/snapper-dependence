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

import java.util.List;

/**
 * DataPermSearchWrap
 *
 * @author zening 2023/11/7 14:52
 * @version 1.0.0
 **/
@Data
@Accessors(chain = true)
public class DataPermSearchWrap {

    //表名
    private String tableName;
    //表别名
    private String aliasName;
//    //SQL追加模式（如果有已有相同列的查询条件）
//    private String conditionAppendMode;
    //SQL条件中对应的表重命名
    private String conditionTableAlias;
//    //SQL条件追加到表格名称对应的层测
//    private String conditionAppendWithTable;

    private List<AdvanceSearchWrap> permExpresses;
}
