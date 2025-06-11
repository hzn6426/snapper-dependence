package com.baomibing.authority.entity;

import com.baomibing.orm.base.MBaseModel;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * SysGateLimitUtag
 *
 * @author zening
 * @version 1.0.0
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_gate_limit_utag")
public class SysGateLimitUtag extends MBaseModel {

    private String id;
    private String tagCode;
    private String tagName;
    private String limitId;
    private String groupId;

}
