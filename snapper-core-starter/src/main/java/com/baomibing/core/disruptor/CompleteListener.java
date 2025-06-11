/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.disruptor;

/**
 * Consumer执行的回调
 * @author : zening
 * @date: 2020-08-14 16:32
 * @version: 1.0.0
 */
public interface CompleteListener {

    public void afterComplete();
}
