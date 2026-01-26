
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


import com.baomibing.authority.action.UserAction;
import com.baomibing.authority.constant.enums.TokenExpirePolicyEnum;
import com.baomibing.authority.state.UserState;
import com.baomibing.core.process.SProcess;
import com.baomibing.core.process.StateProcess;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.github.dozermapper.core.Mapping;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import static com.baomibing.authority.state.UserState.*;

/**
 * 用户对象
 *
 * @author zening
 * @since 1.0.0
 */
@JsonInclude(Include.NON_NULL)
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class UserDto extends StateProcess<UserState, UserAction> {

    private String id;
    @Mapping("userNo")
    private String userName;
    private String workNo;
    private String linePhone;
    private String secretKey;
    private String userSex;
    private String userPasswd;
    private String userMobile;
    private String userRealEnName;
    private String userRealCnName;
    private String name;
    private String qq;
    private String createUserCnName;
    /**
     * 微信号
     */
    private String weixin;
    private String userTag;
    private String pointTag;
    private String userEmail;
    private String userEmailPwd;
    private String userEmailHost;
    private String userEmailProtocol;
    @JsonInclude(value = Include.ALWAYS)
    private Date expireTime;
    private Date createTime;
    private Boolean beSearch;
    private String avatar;
    private String state;
    private String pinYin;
    private String fullPinYin;
    @Length(max = 300)
    private String note;
    private Boolean beMultiLogin;
    private String expirePolicy = TokenExpirePolicyEnum.FIXED.name();
    private Boolean beLock;

    private String ugId;
    private String postName;
    private String positionId;
    private String groupName;
    private String groupId;
    //是否分配部门
    private String groupState;
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
    private List<String> roles;
    private String userRoles;
    private String userGroups;
    private List<String> groups;
    private String userPosts;
    private String userParentGroups;
    private String newPassword;
    private String type;
    private String unencryptPassword;
    private String systemTag;

    private String roleName;
    private String departName;
    private String companyName;

    private String titilePrefix;

    private String websiteUrl;

    private String branchCompany;
    private String userSets;

    private String captcha;

    @Override
    public SProcess<UserState, UserAction> initProcess() {
        return builder
            .initState(UNACTIVE).deleteable(EnumSet.of(STOPPED,UNACTIVE))
            .process().source(UNACTIVE).target(ACTIVE).action(UserAction.ACTIVE)
            .and()
            .process().source(ACTIVE).target(STOPPED).action(UserAction.STOP)
            .and()
            .process().source(STOPPED).target(ACTIVE).action(UserAction.UNSTOP)
            .and()
            .process().source(ACTIVE).target(LOCKED).action(UserAction.LOCK)
            .and()
            .process().source(LOCKED).target(ACTIVE).action(UserAction.UNLOCK)
            .build();
    }
    
    
}
