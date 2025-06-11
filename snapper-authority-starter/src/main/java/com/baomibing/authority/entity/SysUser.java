/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.entity;

import com.baomibing.orm.base.MBaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 系统用户
 * 
 * @author zening
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_user")
public class SysUser extends MBaseModel {
	private static final long serialVersionUID = -1029797871531099018L;

	@TableId(type = IdType.ASSIGN_ID)
    private String id;
    @TableField("user_no")
    private String userNo;
    @TableField("work_no")
    private String workNo;

    /**
     * 固话
     */
    @TableField("line_phone")
    private String linePhone;
    @TableField("secret_key")
    private String secretKey;
    @TableField("user_sex")
    private String userSex;
    @TableField("user_passwd")
    private String userPasswd;
    @TableField("be_search")
    private Boolean beSearch;
    @TableField("user_mobile")
    private String userMobile;
    @TableField("user_real_en_name")
    private String userRealEnName;
    @TableField("user_real_cn_name")
    private String userRealCnName;
	@TableField("pin_yin")
    private String pinYin;
	@TableField("qq")
    private String qq;
    /**
     * 微信号
     */
    private String weixin;
	@TableField("user_tag")
    private String userTag;
    @TableField("user_email")
    private String userEmail;
    @TableField("user_email_pwd")
    private String userEmailPwd;
    @TableField("user_email_host")
    private String userEmailHost;
    @TableField("user_email_protocol")
    private String userEmailProtocol;
    @TableField("expire_time")
    private Date expireTime;
    @TableField("avatar")
    private String avatar;
    @TableField("state")
    private String state;
    @TableField("note")
    private String note;
    @TableField("be_multi_login")
    private Boolean beMultiLogin;
    @TableField("expire_policy")
    private String expirePolicy;

    private Boolean beLock;

    /**
     * 工号
     */
    private String jobNo;
    /**
     * 授权码
     */
    private String authPass;
    /**
     * 授权码生效起始时间
     */
    private Date authPassStart;
    /**
     * 授权码生效结束时间
     */
    private Date authPassEnd;

    private transient String ugId;
    private transient String postName;
    private transient String positionId;
    private transient String companyId;
    private transient String userRoles;
    private transient String userRoleIds;
    private transient String userGroups;
    private transient String userGroupIds;
    private transient String userParentGroups;
    private transient String userPosts;
    private transient String groupId;
    private transient String groupName;
    private transient String userSets;
}
