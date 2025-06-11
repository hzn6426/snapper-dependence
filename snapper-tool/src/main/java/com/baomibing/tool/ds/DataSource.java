/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.ds;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 数据源 - 用来进行辅助切换为对应的数据源
 *
 * @author zening 2022/9/21 09:36
 * @version 1.0.0
 */
@Data
@Accessors(chain = true)
public class DataSource {
    //数据源名称
    private String name;

//    private String method;
//
//    private String url;
//
//    private String token;
//
//    private String params;
}
