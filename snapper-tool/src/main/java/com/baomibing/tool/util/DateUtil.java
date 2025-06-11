/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.util;
/**
 * 日期工具
 *
 * @author zening
 * @date 2020-01-17 17:43:10
 * @version 1.0.0
 */

import com.baomibing.tool.common.DateDuration;
import com.baomibing.tool.constant.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Date;
import java.util.List;

public abstract class DateUtil {

    public static String toString(Date date, String format) {
        if (Checker.beNull(date)) return Strings.EMPTY;
        return new DateTime(date).toString(format);
    }

    public static Date toDateIgnoreTime(Date date) {
        return new DateTime(date).toLocalDate().toDate();
    }

    public static List<DateDuration> subtract(DateDuration duration, List<DateDuration> removeDurations) {
        if (Checker.beNull(duration) || Checker.beNull(duration.getStart()) || Checker.beNull(duration.getEnd()))
            return Lists.newArrayList();
        if (Checker.beEmpty(removeDurations)) {
            return Lists.newArrayList(duration);
        }
        RangeSet<Date> rs = TreeRangeSet.create();
        rs.add(Range.closed(duration.getStart(), duration.getEnd()));
        for (DateDuration d : removeDurations) {
            rs.remove(Range.closed(d.getStart(), d.getEnd()));
        }
        List<DateDuration> list = Lists.newArrayList();
        for (Range<Date> r : rs.asRanges()) {

            list.add(new DateDuration().setStart(r.lowerEndpoint()).setEnd(r.upperEndpoint()));
        }
        return list;
    }

    public static Date toDate(String date, String format) {
        return DateTime.parse(date, DateTimeFormat.forPattern(format)).toDate();
    }



    public static void main(String[] args) {
        System.out.println(toDateIgnoreTime(new DateTime(System.currentTimeMillis()).toDate()));
//        DateDuration d = new DateDuration();
//        d.setStart(new DateTime(2020, 1, 1, 10, 0).toDate()).setEnd(new DateTime(2020, 1, 31, 12, 0).toDate());
//
//        DateDuration d1 = new DateDuration();
//        d1.setStart(new DateTime(2020, 1, 10, 12, 0).toDate()).setEnd(new DateTime(2020, 1, 15, 12, 0).toDate());
//
//        DateDuration d2 = new DateDuration();
//        d2.setStart(new DateTime(2020, 1, 20, 10, 0).toDate()).setEnd(new DateTime(2020, 1, 22, 12, 0).toDate());
//
//        DateDuration d3 = new DateDuration();
//        d3.setStart(new DateTime(2020, 1, 28, 10, 0).toDate()).setEnd(new DateTime(2020, 1, 29, 12, 0).toDate());
//
//        List<DateDuration> list = Lists.newArrayList(d1, d2, d3);
//
//        List<DateDuration> targets = subtract(d, list);
//
//        targets.forEach(t -> System.out.println(new DateTime(t.getStart()).toString("yyyy-MM-dd HH:mm") + "->" + new DateTime(t.getEnd()).toString("yyyy-MM-dd HH:mm")));

    }
}
