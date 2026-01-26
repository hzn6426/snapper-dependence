/*
 * Copyright (c) 2020-2025, zening (316279828@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.baomibing.authority.entity;

import com.baomibing.orm.base.MBaseModel;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;


/**
 * 租户-用户
 *
 * @author zening
 * @version 1.0.0
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_tenant_user")
public class SysTenantUser extends MBaseModel {

    private String id;
    private String tenantId;
    private String userNo;

    private String jobNo;

    private String userSex;
    private String userPasswd;
    private String secretKey;

    private Boolean beSearch;
    private Boolean beSuper;

    private String userMobile;

    private String userRealEnName;
    private String userRealCnName;

    private String userTag;
    private String pointTag;
    private String userEmail;
    private Date expireTime;

    private String avatar;
    private String state;
    private String note;

    private  String openId;

    /**
     * 简称拼音
     */
    private String pinYin;

    private String fullPinYin;

    /**
     * 用户组织关联ID
     */
    private transient String ugId;
    /**
     * 用户职位（某个组织下）
     */
    private transient String postName;
    /**
     * 职位ID
     */
    private transient String positionId;
    /**
     * 公司ID
     */
    private transient String companyId;
    /**
     * 用户角色 ，不存入数据库
     */
    private transient String userRoles;
    /**
     * 用户角色ID，不存入数据库
     */
    private transient String userRoleIds;
    /**
     * 用户所在的组织
     */
    private transient String groupId;

    private transient String groupName;
}
