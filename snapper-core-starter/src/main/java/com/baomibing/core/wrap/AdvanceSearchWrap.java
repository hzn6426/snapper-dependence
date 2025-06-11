/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.wrap;

import com.baomibing.core.enums.DataTypeEnum;
import com.baomibing.core.enums.OperatorEnum;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.tool.constant.Formats;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.filter.HTMLFilter;
import com.baomibing.tool.util.Checker;
import com.google.common.base.Splitter;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.EnumUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AdvanceSearchWrap
 *
 * @author zening 2022/4/20 15:30
 * @version 1.0.0
 */
@Data
@Accessors(chain = true)
public class AdvanceSearchWrap {

    private String andOr;

    private String column;

    private String condition;

    //date, datetime
    private String dataType;

    private Object value;


    public Object displayValue(Object displayValue) {
        if (displayValue == null || Checker.beEmpty(displayValue.toString())) {
            return null;
        }


        DataTypeEnum typeEnum = EnumUtils.getEnum(DataTypeEnum.class, dataType);

        Class<?> type = displayValue.getClass();

        if (displayValue instanceof  AdvanceSearchWrap) {
            return Strings.LEFT_BRACKET + ((AdvanceSearchWrap) displayValue).toSQL() + Strings.RIGHT_BRACKET;
        }

        if (type.isPrimitive()) {
            if (displayValue instanceof Boolean) {
                type = Boolean.class;
            } else if (displayValue instanceof Character) {
                type = Character.class;
            } else if (displayValue instanceof Byte) {
                type = Byte.class;
            } else if (displayValue instanceof Short) {
                type = Short.class;
            } else if (displayValue instanceof Integer) {
                type = Integer.class;
            } else if (displayValue instanceof Long) {
                type = Long.class;
            } else if (displayValue instanceof Float) {
                type = Float.class;
            } else if (displayValue instanceof Double) {
                type = Double.class;
            }
        }
        if (String.class.equals(type)) {
            if (Checker.beNotNull(typeEnum)) {
                DateTime date = DateTime.parse(displayValue.toString(), DateTimeFormat.forPattern(Formats.DEFAULT_T_DATE_TIME_FORMAT));
                if (typeEnum == DataTypeEnum.date) {
                    return "'" + date.toString(Formats.DEFAULT_DATE_FORMAT) + "'";
                } else if (typeEnum == DataTypeEnum.datetime) {
                    return "'" + date.toString(Formats.DEFAULT_DATE_TIME_FORMAT) + "'";
                }
            }
            return "'" + displayValue.toString() + "'";
        }
        if (Date.class.isAssignableFrom(type)) {
            Date date = (Date) displayValue;
            if (Checker.beNotNull(typeEnum)) {
                if (typeEnum == DataTypeEnum.date) {
                    return "'" + new DateTime(date).toString(Formats.DEFAULT_DATE_FORMAT) + "'";
                } else if (typeEnum == DataTypeEnum.datetime) {
                    return "'" + new DateTime(date).toString(Formats.DEFAULT_DATE_TIME_FORMAT) + "'";
                }
            }
            return date.toString();
        }
        if (Number.class.isAssignableFrom(type)) {

            Number num = (Number) displayValue;
            if (type.equals(Double.class))
                return num.doubleValue();
            if (type.equals(Float.class))
                return num.floatValue();
            if (type.equals(Long.class))
                return num.longValue();
            if (type.equals(Integer.class))
                return num.intValue();
            if (type.equals(Short.class))
                return num.shortValue();
            try {
                return type.getConstructor(new Class[]{String.class})
                        .newInstance(displayValue.toString());
            } catch (IllegalArgumentException | SecurityException | InstantiationException
                     | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            }

        } else if (Class.class.equals(type)) {
            try {
                return Class.forName(displayValue.toString());
            } catch (ClassNotFoundException e) {
                throw new ClassCastException("Unable to convert displayValue " + displayValue.toString() + " to type Class");
            }
        } else if (Collection.class.isAssignableFrom(type)) {
            Collection<?> collection = (Collection<?>) displayValue;
            return collection.stream().map(c -> displayValue(c).toString())
                    .collect(Collectors.joining(","));
        } else if (displayValue instanceof Object[]) {
            return Arrays.stream((Object[]) displayValue).map(c -> displayValue(c).toString())
                    .collect(Collectors.joining(","));
        }
        throw new ClassCastException(
                "Unable to convert displayValue of type " + displayValue.getClass().getName() + " to type " + type.getName());
    }


    public String toSQL() {
        if ( Checker.beNull(value) || Checker.beEmpty(value.toString())) {
            return Strings.EMPTY;
        }
        if (beSqlInValid(value.toString())) {
            throw new ServerRuntimeException(ExceptionEnum.VALUE_CONTAINS_INVALID_SQL_CHARS);
        }
        if (Checker.beNotEmpty(column)) {
            if (beSqlInValid(column)) {
                throw new ServerRuntimeException(ExceptionEnum.VALUE_CONTAINS_INVALID_SQL_CHARS);
            }
            if (CharUtils.isAsciiNumeric(column.charAt(0))) {
                throw new ServerRuntimeException(ExceptionEnum.COLUMN_NAME_IS_NOT_VALID);
            }
        }
        HTMLFilter filter = new HTMLFilter();
        if (value instanceof String) {
            value = filter.filter(value.toString());
        }
        DataTypeEnum typeEnum = EnumUtils.getEnum(DataTypeEnum.class, dataType);
        boolean beDate = DataTypeEnum.date == typeEnum || DataTypeEnum.datetime == typeEnum;

        String andOrSql = "";
        if (Checker.beNotEmpty(andOr)) {
            OperatorEnum andOrOperator = EnumUtils.getEnum(OperatorEnum.class, andOr);
            if (Checker.beNull(andOrOperator)) {
                throw new ServerRuntimeException(ExceptionEnum.CONDITON_OPERATOR_NOT_VALID);
            }
            andOrSql = andOrOperator.getSql();
        }

        String conditionSql = "";
        Object targetValue = null;
        boolean beHasIn = false;
        if (Checker.beNotEmpty(condition)) {
            OperatorEnum conditionOperator = EnumUtils.getEnum(OperatorEnum.class, condition);
            if (Checker.beNull(conditionOperator)) {
                throw new ServerRuntimeException(ExceptionEnum.CONDITON_OPERATOR_NOT_VALID);
            }
            if (conditionOperator == OperatorEnum.LIKE && !beDate) {
                targetValue = Strings.PERCENT + value + Strings.PERCENT;
            } else if (conditionOperator == OperatorEnum.IN || conditionOperator == OperatorEnum.NIN) {
                beHasIn = true;
                List<String> vs = Splitter.on(Strings.COMMA).splitToList(value.toString());
                targetValue = vs;

            } else {
                targetValue = value;
            }
            conditionSql = conditionOperator.getSql();
        }

        return andOrSql + column + conditionSql + (beHasIn ? Strings.LEFT_BRACKET + displayValue(targetValue) + Strings.RIGHT_BRACKET : displayValue(targetValue));
    }

    private static boolean beSqlInValid(String sql) {
        if (Checker.beEmpty(sql)) {
            return false;
        }
        sql = sql.toLowerCase();
        String badStr = "'|and|exec|execute|insert|select|delete|update|count|drop|*|%|chr|mid|master|truncate|" +
                "char|declare|sitename|net user|xp_cmdshell|;|or|-|+|,|like'|and|exec|execute|insert|create|drop|" +
                "table|from|grant|use|group_concat|column_name|" +
                "information_schema.columns|table_schema|union|where|select|delete|update|order|by|count|*|" +
                "chr|mid|master|truncate|char|declare|or|;|-|--|+|,|like|//|/|%|#";//过滤掉的sql关键字，可以手动添加
        String[] badSqlList = badStr.split("\\|");
        for (String s : badSqlList) {
            if (Checker.beEmpty(s)) {
                continue;
            }
            List<String> sqlSplits = Arrays.asList(sql.split(Strings.SPACE));
            if (sqlSplits.contains(s)) {
                return true;
            }
        }
        return false;
    }
}
