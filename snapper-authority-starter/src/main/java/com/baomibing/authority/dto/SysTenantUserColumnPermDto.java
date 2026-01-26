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

import com.baomibing.orm.perm.ActionSelectTable;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * SysTenantUserColumnPerm
 *
 * @author zening
 * @version 1.0.0
 **/
@Data
@Accessors(chain = true)
public class SysTenantUserColumnPermDto {
    private String id;
    private String tenantId;
    private String userId;

    private String permId;

    private String orgId;

    private Date permStartTime;

    private Date permEndTime;

    private String columnExpress;

    private List<ActionSelectTable> tableColumns;
}
