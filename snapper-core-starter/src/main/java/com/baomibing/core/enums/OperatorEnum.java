/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.enums;

import com.baomibing.tool.constant.Strings;
import lombok.Getter;

/**
 * OperatorEnum
 *
 * @author zening 2022/4/20 15:40
 * @version 1.0.0
 */
public enum OperatorEnum {

    AND("AND"), OR("OR"), EQ("="), GTE(">="), GT(">"), LTE("<="), LT("<"), LIKE("LIKE"), NEQ("!="), IN("IN"), NIN("NOT IN");

    @Getter
    private final String sql;

    OperatorEnum(String sql) {
        this.sql = Strings.SPACE + sql + Strings.SPACE;
    }
}
