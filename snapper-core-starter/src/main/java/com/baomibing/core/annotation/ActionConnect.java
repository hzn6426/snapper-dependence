/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
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
