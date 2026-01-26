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
package com.baomibing.core.annotation;

import com.baomibing.tool.constant.PermConstant;

import java.lang.annotation.*;

/**
 * 业务权限关联mapper
 * 
 * @author zening
 * @date Jun 15, 2021 9:53:59 AM
 * @version 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ActionConnect {

	String[] value();

	//总是过滤创建用户列(创建用户列的值系统会默认写入, 用户默认能查自己的数据)
	boolean beAlwaysFilterCreateUserColumn() default true;

	String[] ignoreUserScopeTags() default "";
	//can use for pattern of tag1,tag2,tag3#column1,column2
	String[] userAuthColumn() default PermConstant.CREATE_USER;

	String[] ignoreGroupScopeTags() default "";

	String[] groupAuthColumn() default PermConstant.GROUP_ID;

	//权限条件追加的位置
	String tableNameWithAuthInject() default "";

	//列权限追加的位置(表名对应的层级)
	String tableNameWithColumnInject() default "";

}
