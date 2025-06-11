package com.baomibing.authority.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * GateLimitRequestDto
 *
 * @author zening
 * @version 1.0.0
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Accessors(chain = true)
public class GateLimitRequestDto {

    private String id;
    private String url;
    private String method;
    private String ip;
    private String limitId;
    private String groupId;
    private String mode;
    private String urlPrefix;
    private String resourceName;
    private String resourceId;
    private String priority;
}
