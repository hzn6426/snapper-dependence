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

package com.baomibing.tool.perm;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * ActionAnnotation
 *
 * @author zening (316279828@qq.com) 2025/6/26 16:16
 * @version 1.0.0
 **/
@Data @Accessors(chain = true)
public class ActionAnnotation {

    private String actionValue;

    private Boolean actionIgnoreUserScope;

    private Boolean actionIgnoreGroupScope;

    private String actionIgnoreCompanyScopeTags;

    private String actionOnlyFilterCompanyTags;

    private String connectValue;

    private String connectIgnoreUserScopeTags;

    private String connectIgnoreGroupScopeTags;

    private Boolean connectBeAlwaysFilterCreateUserColumn;

    private String connectUserAuthColumn;

    private String connectGroupAuthColumn;

    private String connectTableNameWithAuthInject;

    private String connectTableNameWithColumnInject;
}
