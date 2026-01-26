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
package com.baomibing.core.wrap;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * DataPermWrap
 *
 * @author zening 2023/5/18 09:15
 * @version 1.0.0
 **/
@Data
@Accessors(chain = true)
public class DataPermWrap {

    //原始条件 有相同列时，是否强制追加相同列查询
//    private Boolean beForceCondition = Boolean.FALSE;

    private Boolean beOrCondition = Boolean.FALSE;

    private List<DataPermSearchWrap> searchWraps;

//    private List<AdvanceSearchWrap> permExpresses;
}
