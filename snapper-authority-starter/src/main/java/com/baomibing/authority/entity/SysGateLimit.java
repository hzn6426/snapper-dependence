package com.baomibing.authority.entity;

import com.baomibing.orm.base.MBaseModel;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * SysGateLimit
 *
 * @author zening 2023/8/21 11:47
 * @version 1.0.0
 **/
@TableName("sys_gate_limit")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysGateLimit extends MBaseModel {

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
    private Boolean beLock;

}
