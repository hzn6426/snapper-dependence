/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.wrap;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: zhenyang 2021/5/18 10:04
 * @version: 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Accessors(chain = true)
@AllArgsConstructor
public class DictionaryChildWrapDto {

    private String dictCode;

    private String dictName;
}
