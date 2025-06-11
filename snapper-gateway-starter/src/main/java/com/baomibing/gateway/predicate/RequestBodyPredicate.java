/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.gateway.predicate;

import java.util.function.Predicate;

/**
 * RequestBodyPredicate
 *
 * @author zening 2022/9/19 11:21
 * @version 1.0.0
 */

public class RequestBodyPredicate implements Predicate {
    @Override
    public boolean test(Object o) {
        return true;
    }
}
