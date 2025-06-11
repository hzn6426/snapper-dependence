package com.baomibing.tool.limit;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * GateLimit
 *
 * @author zening 2023/8/21 13:52
 * @version 1.0.0
 **/
@Data
@Accessors(chain = true)
public class GateLimit {

    private String id;
    private String name;
    private Integer durationInSecond;
    private Integer allowVolume;
    private Integer speedInSecond;
    private String ruleTuser;
    private String ruleUser;
    private String ruleSystemTag;
    private String ruleUserTag;
    private String ruleUrl;
    private String state;
    private String groupId;

    //when object changed(refresh cache), uuid will change!
    private String uuid;

    private List<GateLimitStag> stags;
    private List<GateLimitUtag> utags;
    private List<GateLimitUser> users;
    private List<GateLimitRequest> requests;
    private List<GateLimitTuser> tusers;

}
