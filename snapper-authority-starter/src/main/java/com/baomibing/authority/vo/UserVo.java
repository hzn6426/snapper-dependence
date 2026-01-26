
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

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author : zening
 * @date: 2021-07-08 17:06
 * @version: 1.0.0
 */
@Data
@Accessors(chain = true)
public class UserVo {

    private String id;
    private String userName;
    private String userNo;
    private String userRealCnName;
    private String userRealEnName;
    private String currentGroupId;
    private String currentPositionId;
	private String permScope;
	private String companyId;

	private boolean beIgnoreUserScope = false;
	private boolean beIgnoreGroupScope = false;

    private String permId;

    private String userMobile;
    private String userEmail;
}
