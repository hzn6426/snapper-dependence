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
package com.baomibing.core.common;


import com.baomibing.core.enums.OperatorEnum;
import com.baomibing.core.wrap.AdvanceSearchWrap;
import com.baomibing.tool.util.Checker;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AdvanceSearchParser
 *
 * @author zening 2023/4/25 11:47
 * @version 1.0.0
 **/
public class AdvanceSearchParser {

    public static String toSQL(List<AdvanceSearchWrap> searchs) {
        if (Checker.beEmpty(searchs)) {
            return "";
        } else {
            String sql = (String)searchs.stream().map(AdvanceSearchWrap::toSQL).collect(Collectors.joining());
            sql = sql.trim();
            if (sql.startsWith(OperatorEnum.AND.name())) {
                sql = sql.substring(3);
            } else if (sql.startsWith(OperatorEnum.OR.name())) {
                sql = sql.substring(2);
            }

            return sql;
        }
    }
}
