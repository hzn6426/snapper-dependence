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

import java.io.Serializable;

/**
 * 用户用户组关系
 * @author : zening
 * @version : 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Accessors(chain = true)
public class UserUsetDto implements Serializable {
    

	private String id;
    
    private String usetId;
    
    private String userId;
    
    private String orgId;
}
