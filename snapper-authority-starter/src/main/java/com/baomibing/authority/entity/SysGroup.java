
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
 * 组织 - 无限极分类思想
 * 
 * @author zening
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_group")
public class SysGroup extends MBaseModel {
	private static final long serialVersionUID = -7897896723130542526L;

	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private String id;
	@TableField(value = "group_name")
	private String groupName;
	@TableField(value = "glft")
	private Integer glft;
	@TableField(value = "grht")
	private Integer grht;
	@TableField(value = "parent_id")
	private String parentId;
	@TableField(value = "group_level")
	private Integer groupLevel;
	@TableField(value = "group_id")
	private String groupId;
	@TableField(value = "be_deleted")
	private Boolean beDeleted;
	private Boolean beLock;
    private Short priority;

	// 父级组织名称
	private transient String parentGroupName;
}
