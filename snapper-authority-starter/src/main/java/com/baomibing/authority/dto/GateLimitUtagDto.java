package com.baomibing.authority.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * GateLimitUtagDto
 *
 * @author zening
 * @version 1.0.0
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Accessors(chain = true)
public class GateLimitUtagDto {

    private String id;
    private String tagCode;
    private String tagName;
    private String limitId;
    private String groupId;
}
