package com.baomibing.tool.limit;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * GateLimitUtag
 *
 * @author zening 2023/8/21 13:53
 * @version 1.0.0
 **/
@Data
@Accessors(chain = true)
public class GateLimitUtag {

    private String id;
    private String tagCode;
    private String tagName;
    private String limitId;
    private String groupId;
}
