
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

package com.baomibing.authority.wrap;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * MenuWrap
 *
 * @author zening 2022/3/7 14:38
 * @version 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Accessors(chain = true)
public class MenuWrap {
    private boolean disabled;
    private String key;
    private String value;
    private String title;
    private String url;
    private String parentId;
    private String parentGroupName;
    private String parentMenuType;
    private String tag;
    private String iconCls;
    private String state;
    private Boolean isLeaf;
    private Boolean selectable;
    private Boolean disableCheckbox;
    private String reqMethod;
    private String reqUrl;
    private Short priority;
    private Boolean beHidden;
    private String menuType;
    private Boolean beUnauth;
    private List<MenuWrap> children;

}
