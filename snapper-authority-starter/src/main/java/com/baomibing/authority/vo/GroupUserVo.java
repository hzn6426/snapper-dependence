/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 机构添加用户
 * 
 * @author zening
 * @since 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data @Accessors(chain = true)
public class GroupUserVo {

    private String positionId;
    private String groupId;
    private List<String> users;
    private String toGroupId;
    private String userId;
    
}
