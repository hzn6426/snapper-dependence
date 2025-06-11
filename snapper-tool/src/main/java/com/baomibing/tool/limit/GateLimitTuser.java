package com.baomibing.tool.limit;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * GateLimitTuser
 *
 * @author zening 2024/10/9 14:19
 * @version 1.0.0
 **/
@Data
@Accessors(chain = true)
public class GateLimitTuser {
    private String id;
    private String limitId;
    private String groupId;
    private String limitType;
    private String limitValue;
}
