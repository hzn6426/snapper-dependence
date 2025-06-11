/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.common;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * HmacPermCache
 *
 * @author zening 2023/6/15 11:32
 * @version 1.0.0
 **/
@Data @Accessors(chain = true)
public class HmacPermCache {

    private String permKey;

    private String user;
}
