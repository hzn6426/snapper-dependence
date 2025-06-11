package com.baomibing.tool.limit;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * GateLimitUser
 *
 * @author zening 2023/8/21 13:59
 * @version 1.0.0
 **/
@Data
@Accessors(chain = true)
public class GateLimitUser {

    private String id;
    private String userId;
    private String userRealCnName;
    private String limitId;
    private String groupId;
}
