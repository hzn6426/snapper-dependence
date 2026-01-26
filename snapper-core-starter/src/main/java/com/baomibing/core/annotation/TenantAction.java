
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


import com.baomibing.core.base.ActionScope;

import java.lang.annotation.*;

/**
 * 业务权限注解，用于根据业务动作获取用户的业务权限信息
 * @author zening
 * @date 2019-06-15 10:09:20
 * @version 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TenantAction {

	String value();
	
	// 是否忽略用户权限范围
	boolean ignoreUserScope() default false;


	// 是否忽略组织权限范围
	boolean ignoreGroupScope() default false;

	//忽略公司权限范围TAG
	String[] ignoreCompanyScopeTags() default "";

	//只过滤分公司的用户TAG
	String[] onlyFilterCompanyTags() default "";

	//是否外部接入
	boolean beWebApi() default false;

	//ignoreUserGroup and ignoreGroupScope will only filter company
	ActionScope defaultScope() default ActionScope.CURRENT_COMPANY;
 }
