
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

package com.baomibing.authority.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 机构添加用户
 * 
 * @author zening
 * @since 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data @Accessors(chain = true)
public class GroupUserVo {

    private String positionId;
    private String groupId;
    private List<String> users;
    private String toGroupId;
    private String userId;
    //移动用户时是否同步用户权限
    private boolean beSyncUserPerm = Boolean.TRUE;
    
}
