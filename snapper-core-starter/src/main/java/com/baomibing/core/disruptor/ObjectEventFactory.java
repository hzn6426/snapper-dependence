/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * 事件生成工厂，用来分配事件对象
 * @author : zening
 * @date: 2020-08-14 13:44
 * @version: 1.0.0
 */

public class ObjectEventFactory<T> implements EventFactory<ObjectEvent<T>> {

    @Override
    public ObjectEvent<T> newInstance() {
        return new ObjectEvent<>();
    }
}
