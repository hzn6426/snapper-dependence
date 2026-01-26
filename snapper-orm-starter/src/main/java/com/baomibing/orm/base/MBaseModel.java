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
package com.baomibing.orm.base;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class MBaseModel implements Serializable {
	private static final long serialVersionUID = 3106470533624582061L;
	
	@TableField("create_time")
    protected Date createTime;
    @TableField("update_time")
    protected Date updateTime;
    @TableField("create_user")
    protected String createUser;
    @TableField("update_user")
    protected String updateUser;
    @TableField("create_user_cn_name")
    protected String createUserCnName;
    @TableField("update_user_cn_name")
    protected String updateUserCnName;
    
}
