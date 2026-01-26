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

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * SysTenantUsetBusinessPerm
 *
 * @author zening
 * @version 1.0.0
 **/
@Data
@Accessors(chain = true)
public class SysTenantUsetBusinessPermDto {
    private String id;
    private String tenantId;
    private String usetId;
    private String permId;
    private String permScope;
    private Date permStartTime;
    private Date permEndTime;

    //======获取用户数据权限列表连表查询字段========//
    private String menuId;
    private String menuName;
    private String permName;
    private String parentId;
    private String tag;//MENU还是PERM
    private List<UsetBusinessPermDto> children;

    //=========用户权限保存功能传递信息=============//
    //组织委托
    private List<String> groupEntrusts;
    //用户委托
    private List<String> userEntrusts;
    //排除组织委托
    private List<String> exceptGroupEntrusts;
    //排除用户委托
    private List<String> exceptUserEntrusts;
}
