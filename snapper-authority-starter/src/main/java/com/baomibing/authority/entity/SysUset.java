/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.entity;


import com.baomibing.orm.base.MBaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 用户组
 * @author zening
 * @version  1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_uset")
public class SysUset extends MBaseModel {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String usetName;
    
    private String note;
    
    private String state;
    
    private String groupId;

    private Boolean beLock;

}
