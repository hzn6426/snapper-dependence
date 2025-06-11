/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
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
public @interface Action {

	String value();
	
	// 是否忽略用户权限范围
	boolean ignoreUserScope() default false;


	// 是否忽略组织权限范围
	boolean ignoreGroupScope() default false;

	//忽略公司权限范围TAG
	String[] ignoreCompanyScopeTags() default "";

	//只过滤分公司的用户TAG
	String[] onlyFilterCompanyTags() default "";


	//ignoreUserGroup and ignoreGroupScope will only filter company
	ActionScope defaultScope() default ActionScope.CURRENT_COMPANY;
 }
