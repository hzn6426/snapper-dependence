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

package com.baomibing.authority.entity;

import com.baomibing.orm.base.MBaseModel;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * SysGateLimit
 *
 * @author zening 2023/8/21 11:47
 * @version 1.0.0
 **/
@TableName("sys_gate_limit")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysGateLimit extends MBaseModel {

    private String id;
    private String name;
    private Integer durationInSecond;
    private Integer allowVolume;
    private Integer speedInSecond;
    private String ruleTuser;
    private String ruleUser;
    private String ruleSystemTag;
    private String ruleUserTag;
    private String ruleUrl;
    private String state;
    private String groupId;
    private Boolean beLock;

}
