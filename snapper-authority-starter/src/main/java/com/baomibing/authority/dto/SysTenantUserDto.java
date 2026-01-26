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

package com.baomibing.authority.dto;

import com.baomibing.authority.action.TenantUserAction;
import com.baomibing.authority.state.TenantUserState;
import com.baomibing.core.process.SProcess;
import com.baomibing.core.process.StateProcess;
import com.github.dozermapper.core.Mapping;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.EnumSet;
import java.util.List;


/**
 * 租户-用户
 *
 * @author fzening
 * @version 1.0.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysTenantUserDto extends StateProcess<TenantUserState, TenantUserAction> {

    private String id;
    private String tenantId;
    @Mapping("userNo")
    private String userName;

    private String jobNo;

    private String userSex;
    private String userPasswd;
    private String secretKey;

    private Boolean beSearch;
    private Boolean beSuper;

    private String userMobile;

    private String userRealEnName;
    private String userRealCnName;

    private Date expireTime;
    private String userTag;
    private String pointTag;
    private String userEmail;

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
     * 组织ID - 组织列表中用于根据组织查询用户
     */
    private String groupId;

    /**
     * 新密码，用于修改密码
     */
    private String newPassword;
    /**
     * 未加密的密码，用于发送密码邮件
     */
    private String unencryptPassword;

    private String titlePrefix;

    private String websiteUrl;
    /**
     * 用户角色名称，以逗号间隔 ，不存入数据库
     */
    private String userRoles;
    /**
     * 用户角色ID列表 ，不存入数据库
     */
    private List<String> roles;

    /**
     * 前端界面用来展示中文名的
     * @ignore
     */
    private String name;
    /**
     * 用户组织关联ID
     */
    private String ugId;
    /**
     * 用户职位(某个组织下)
     */
    private String postName;
    /**
     * 职位ID
     */
    private String positionId;
    /**
     * 组织
     */
    private String groupName;

    /**
     * 系统的tag 用来区分是哪个系统登录
     */
    private String systemTag;

    private String createUserCnName;

    private Date createTime;


    @Override
    public SProcess<TenantUserState, TenantUserAction> initProcess() {
        return getBuilder().initState(TenantUserState.ACTIVE).deleteable(EnumSet.of(TenantUserState.STOPPED))
                .process().source(TenantUserState.UNACTIVE).target(TenantUserState.ACTIVE).action(TenantUserAction.ACTIVE)
                .and()
                .process().source(TenantUserState.ACTIVE).target(TenantUserState.STOPPED).action(TenantUserAction.STOP)
                .and()
                .process().source(TenantUserState.STOPPED).target(TenantUserState.ACTIVE).action(TenantUserAction.UNSTOP)
                .and()
                .process().source(TenantUserState.ACTIVE).target(TenantUserState.LOCKED).action(TenantUserAction.LOCK)
                .and()
                .process().source(TenantUserState.LOCKED).target(TenantUserState.ACTIVE).action(TenantUserAction.UNLOCK)
                .build();
    }
}
