/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.user;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 个人邮箱配置
 *
 * @author zening 2022/4/12 09:11
 * @version 1.0.0
 */
@Data
@Accessors(chain = true)
public class EmailServer {

    private String host;

    private String address;

    private String passwd;

    private String protocol;
}
