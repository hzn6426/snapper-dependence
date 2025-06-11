package com.baomibing.authority.entity;

import com.baomibing.orm.base.MBaseModel;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * SysGateLimitRequest
 *
 * @author zening
 * @version 1.0.0
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_gate_limit_request")
public class SysGateLimitRequest extends MBaseModel {

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
