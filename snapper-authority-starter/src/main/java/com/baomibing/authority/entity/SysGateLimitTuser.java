package com.baomibing.authority.entity;

import com.baomibing.orm.base.MBaseModel;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * SysGateLimitUser
 *
 * @author zening
 * @version 1.0.0
 **/
@TableName("sys_gate_limit_tuser")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysGateLimitTuser extends MBaseModel {
    private String id;
    private String limitId;
    private String groupId;
    private String limitType;
    private String limitValue;
    private String limitName;
}
