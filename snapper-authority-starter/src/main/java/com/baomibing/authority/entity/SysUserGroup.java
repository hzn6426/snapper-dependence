
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
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 用户组织关联（一个用户对应多个组织）
 * 
 * @author zening
 * @version  1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_user_group")
public class SysUserGroup extends MBaseModel {
	private static final long serialVersionUID = 7815495185453562247L;

	@TableId(type = IdType.ASSIGN_ID)
	private  String id;
	@TableField(value = "user_id")
	private String userId;
	@TableField(value = "group_id")
	private String groupId;
	

	//根据用户查询冗余字段
	private transient String groupName;
	// 公司名
	private transient String companyName;
	// 公司ID
	private transient String companyId;
	private transient String userNo;
}
