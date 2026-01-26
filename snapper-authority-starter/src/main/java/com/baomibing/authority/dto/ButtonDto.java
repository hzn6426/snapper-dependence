
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 按钮
 *
 * @author zening
 * @since 1.0.0
 */
@JsonInclude(Include.NON_NULL)
@Data
@Accessors(chain = true)
@EqualsAndHashCode()
public class ButtonDto implements Serializable {
    
    private static final long serialVersionUID = 8416179840325844378L;
    
    private String id;
    @NotBlank
    private String buttonName;
    @NotNull
    private String menuId;
    private String reqMethod;
    private String reqUrl;
    private Boolean beUnauth;
    private Boolean beLoginUnauth;
    private String subMenu;
    
    private String permScope;
    private String permId;
    private Date permStartTime;
    private Date permEndTime;
    private String permAction;
    private String actionRef;
    private Boolean beLock;

    private String keyword;

    private String dataPerm;

    private String columnPerm;
    
}
