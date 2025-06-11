package com.baomibing.tool.limit;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * GateLimitStag
 *
 * @author zening 2023/8/21 13:54
 * @version 1.0.0
 **/
@Data
@Accessors(chain = true)
public class GateLimitStag {

    private String id;
    private String systemName;
    private String systemTag;
    private String limitId;
    private String groupId;
}
