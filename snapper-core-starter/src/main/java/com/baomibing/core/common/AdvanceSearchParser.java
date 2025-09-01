/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
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
