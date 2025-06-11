/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TimeUtils {


    public static String cstTimeFormat(String cst){
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
                    new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US)
                            .parse(cst)
            );
        } catch (Exception e){
            return null;
        }

    }
}
