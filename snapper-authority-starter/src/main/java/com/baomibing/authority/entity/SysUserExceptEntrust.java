
package com.baomibing.authority.entity;

import com.baomibing.orm.base.MBaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * SysUserExceptEntrust
 *
 * @author zening
 * @version 1.0.0
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_user_except_entrust")
public class SysUserExceptEntrust extends MBaseModel {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    @TableField(value = "user_id")
    private String userId;
    @TableField(value = "user_except_entrust_id")
    private String userExceptEntrustId;
    //	@TableField(value = "perm_action")
//	private String permAction;
    @TableField(value = "perm_id")
    private String permId;
    @TableField(value = "org_id")
    private String orgId;
}
