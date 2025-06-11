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
@TableName("sys_gate_limit_user")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysGateLimitUser extends MBaseModel {
    private String id;
    private String userId;
    private String userRealCnName;
    private String limitId;
    private String groupId;
}
