/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package org.slf4j.impl;

import com.baomibing.log.CustomerMDCAdapter;
import org.slf4j.spi.MDCAdapter;

/**
 * @author : zening
 * @date: 2020-11-19 10:57
 * @version: 1.0.0
 */
public class StaticMDCBinder {

    public static final StaticMDCBinder SINGLETON = new StaticMDCBinder();

    private StaticMDCBinder() {
    }

    public MDCAdapter getMDCA() {
        return new CustomerMDCAdapter();
    }

    public String getMDCAdapterClassStr() {
        return CustomerMDCAdapter.class.getName();
    }
}
