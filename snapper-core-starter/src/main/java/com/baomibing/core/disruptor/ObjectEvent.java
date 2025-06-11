/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.disruptor;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 需要发布的事件对象
 * @author : zening
 * @date: 2020-08-14 13:42
 * @version: 1.0.0
 */
@Data
@Accessors(chain = true) 
public class ObjectEvent<T> {

    T data;
}
