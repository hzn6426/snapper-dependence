package com.baomibing.authority.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * GateLimitUserDto
 *
 * @author zening
 * @version 1.0.0
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Accessors(chain = true)
public class GateLimitTuserDto {

    private String id;
    private String limitId;
    private String groupId;
    private String limitType;
    private String limitValue;
    private String limitName;
}
