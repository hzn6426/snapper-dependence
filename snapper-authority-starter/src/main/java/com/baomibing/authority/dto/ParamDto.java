/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 参数
 *
 * @author : zening
 * @version : 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Accessors(chain = true)
public class ParamDto {

    private String id;

    /**
     * 参数名称
     */
    private String paramName;

    /**
     * 参数编码
     */
    private String paramCode;

    /**
     * 参数值
     */
    private String paramValue;

    /**
     * 是否删除
     */
    private Boolean beDelete;

    /**
     * 组织ID
     */
    private String groupId;

    /**
     * 备注
     */
    private String note;

    private Boolean beLock;

}
