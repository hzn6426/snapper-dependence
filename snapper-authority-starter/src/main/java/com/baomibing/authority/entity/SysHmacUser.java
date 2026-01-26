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

/**
 * SysHmacUser
 *
 * @author zening
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_hmac_user")
public class SysHmacUser extends MBaseModel {

    /**
     * ID
     */
    private String id;
    /**
     * 外部系统名称
     */
    private String systemName;
    /**
     * 加密KEY
     */
    private String appKey;
    /**
     * 过期时间
     */
    private String expireDate;
    /**
     * 颁发的系统ID
     */
    private String appId;
    /**
     * 对应的用户id
     */
    private String userId;
    /**
     * 白名单ip列表
     */
    private String whiteIps;
    /**
     * 状态
     */
    private String state;

    private String orgId;
    /**
     * 业务ID
     */
    private String businessId;

    private String bindType;

    private String note;

    private String tenantId;

    private String tenantName;

    /**
     * 角色ID列表以逗号间隔
     */
    private transient String roleIds;

    /**
     * 用户编码
     */
    private transient String userNo;

    /**
     * 组织ID
     */
    private transient String groupId;

    /**
     * 用户真实姓名
     */
    private transient String userRealCnName;

    /**
     * 用户真实英文名
     */
    private transient String userRealEnName;
    /**
     * 用户TAG
     */
    private transient String userTag;

    /**
     * 部门名称
     */
    private transient String orgName;


    /**
     * 分公司名称
     */
    private transient String groupName;

    /**
     * 职位ID
     */
    private transient String positionId;

    private transient String bindUser;

}
