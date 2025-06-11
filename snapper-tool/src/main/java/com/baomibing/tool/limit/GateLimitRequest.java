package com.baomibing.tool.limit;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * GateLimitRequest
 *
 * @author zening 2023/8/21 13:52
 * @version 1.0.0
 **/
@Data
@Accessors(chain = true)
public class GateLimitRequest {

    private String id;
    private String url;
    private String method;
    private String ip;
    private String limitId;
    private String groupId;
    private String limitType;
    private String resourceName;
    private String resourceId;
    private String priority;
}
