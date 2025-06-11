/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.dto;

import com.baomibing.authority.vo.AdvanceSearchVo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * SysUserDataPermDto
 *
 * @author zening
 * @version 1.0.0
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Accessors(chain = true)
public class UserDataPermDto {

    private String id;

    private String userId;

    private String permId;

    private String orgId;

    private Date permStartTime;

    private Date permEndTime;

    private String permExpress;

    private Boolean beOrCondition;

    private Boolean beForceCondition;

    private List<AdvanceSearchVo> searchExpresses;
}
