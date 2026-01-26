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
package com.baomibing.security.filter;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * TestUser
 *
 * @author zening 2023/8/2 21:24
 * @version 1.0.0
 **/
@Data @Accessors(chain = true)
public class TestUser {

    private String id;
    private String userName;
    private String userCnName;
    private String userEnName;

    private String companyId;
    private String companyName;
    private String roles; //多个以逗号间隔
    private String positionId;
    private String groupId;
    private String groupName;
    private String userTag;//多个以逗号间隔

}
