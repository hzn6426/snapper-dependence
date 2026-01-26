
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

package com.baomibing.core.aspect;


import com.baomibing.core.annotation.TenantActionConnect;
import com.baomibing.core.context.BusinessAuthContext;
import com.baomibing.core.context.MapperAuthContext;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.core.wrap.EntrustWarpper;
import com.baomibing.core.wrap.TenantEntrustWarpper;
import com.baomibing.tool.constant.PermConstant;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.user.TenantUser;
import com.baomibing.tool.util.*;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.vavr.collection.Array;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.baomibing.tool.user.UserContext.currentTenantUser;

/**
 * 业务权限连接切面，用于拦截ActionConnect注解，将Action信息 与 Mapper层关联
 *
 * @author zening
 * @version 1.0.0
 */
@Aspect
@Order(12000)
public class BusinessTenantConnectAspect {

	@Around("@annotation(com.baomibing.core.annotation.TenantActionConnect)")
	public Object roundBusinessConnect(ProceedingJoinPoint point) throws Throwable {
		TenantActionConnect bac = ((MethodSignature) point.getSignature()).getMethod().getAnnotation(TenantActionConnect.class);
		TenantUser currentUser = currentTenantUser().orElse(null);
		String[] bacvs = Checker.beNull(bac) ? new String[0] : bac.value();
		String methodName = point.getSignature().getName();
		String className = ((MethodSignature) point.getSignature()).getMethod().getDeclaringClass().getName();
		String key = className + "." + methodName;

		List<String> values = Lists.newArrayList();
		try {
			EntrustWarpper wrapper = BusinessAuthContext.get(key);
			//如果当前没有绑定权限，直接跳过
			TenantEntrustWarpper ew = wrapper instanceof TenantEntrustWarpper ? (TenantEntrustWarpper)wrapper : null;
			if (Checker.beNotNull(ew)) {
				Class<?> clazz = ((MethodSignature) point.getSignature()).getMethod().getDeclaringClass();
				String clazzName = getMapperClassName(clazz);
				if (Checker.beNotEmpty(clazzName)) {
					// 如果不为空，进行mapper全称前缀
					values = Array.of(bacvs).filter(Checker::beNotEmpty).map(b -> clazzName + Strings.DOT + b).asJava();
				}
				buildContext(key, values, currentUser, bac);
			}
			return point.proceed();
		} finally {
			if (Checker.beNotEmpty(values)) {
				values.forEach(MapperAuthContext::clear);
			}
		}
	}

	// 反射获取mapperClass
	public String getMapperClassName(Class<?> methodClass) {
		String result = Strings.EMPTY;
		if (Checker.beNull(methodClass) || Checker.beNull(methodClass.getSuperclass())) {
			return result;
		}
		Class<?> superClass = methodClass.getSuperclass();
		if (!superClass.getName().contains("MBaseServiceImpl")) {
			return result;
		}
		try {
			Method method = superClass.getDeclaredMethod("reflectForMapperClass");
			method.setAccessible(true);
			Object classAInstance = methodClass.newInstance();
			result = method.invoke(classAInstance).toString();
		} catch (Exception e) {
			throw new ServerRuntimeException(ExceptionEnum.BUSINESS_CONNECT_EXECUTE_ERROR);
		}
		return result;
	}

	private String[] unpack(String[] customerColumns, String userTag) {
		if (Checker.beEmpty(customerColumns) || Checker.beEmpty(userTag)) {
			return customerColumns;
		}
		Set<String> columns = Sets.newHashSet();
		for (String uc : customerColumns) {
			if (uc.contains(Strings.HASH)) {
				List<String> hashSplits = Splitter.on(Strings.HASH).splitToList(uc);
				String tag = hashSplits.get(0);
				if (Checker.beNotEmpty(tag) && Splitter.on(Strings.COMMA).splitToList(tag).contains(userTag)) {
					columns.addAll(Splitter.on(Strings.COMMA).splitToList(hashSplits.get(1)));
				}
			} else {
				columns.add(uc);
			}
		}
		return ConvertUtil.toArray(columns, String.class);

	}



	private boolean beHaveCreateUserColumn(String [] userColumns) {
		if (Checker.beEmpty(userColumns)) {
			return false;
		}
		return Arrays.asList(userColumns).contains(PermConstant.CREATE_USER);
	}

	public void buildContext(String detailMethodName, List<String> detailMapperIds, TenantUser currentUser, TenantActionConnect bac) {

		if (Checker.beNull(currentUser) || ClassUtil.beNotClassOnly(TenantUser.class, currentUser)) {
			return;
		}

		if (Checker.beEmpty(detailMapperIds) || Checker.beEmpty(detailMethodName)) {
			return;
		}
		TenantEntrustWarpper ew = (TenantEntrustWarpper)BusinessAuthContext.get(detailMethodName);
		if (Checker.beNull(ew)) {
			return;
		}

		String userTag = currentUser.getUserTag();
		String[] ignoreUserScopeTags = bac.ignoreUserScopeTags();
		String[] ignoreGroupScopeTags = bac.ignoreGroupScopeTags();

		String[] userColumns = bac.userAuthColumn();
		String[] groupColumns = bac.groupAuthColumn();
		String[] tenantColumns = bac.tenantAuthColumn();

		String tableNameAuthInject = bac.tableNameWithAuthInject();
		String tableNameColumnInject = bac.tableNameWithColumnInject();

		if (Checker.beNotEmpty(ignoreUserScopeTags) && ArrayUtil.containsIgnoreCase(ignoreUserScopeTags, userTag)) {
			ew.setBeIgnoreUserScope(Boolean.TRUE);
		}

		if (Checker.beNotEmpty(userColumns)) {
			ew.setUserColumn(unpack(userColumns, userTag));
			if (Checker.beEmpty(ew.getUserColumn())) {
				ew.setUserColumn(new String[]{PermConstant.CREATE_USER});
			}
		}

		if (Checker.beNotEmpty(ignoreGroupScopeTags) && ArrayUtil.containsIgnoreCase(ignoreGroupScopeTags, userTag)) {
			ew.setBeIgnoreGroupScope(Boolean.TRUE);
		}

		if (Checker.beNotEmpty(groupColumns)) {
			ew.setGroupColumn(unpack(groupColumns, userTag));
			if (Checker.beEmpty(ew.getGroupColumn())) {
				ew.setGroupColumn(new String[]{PermConstant.GROUP_ID});
			}
		}

		if (Checker.beNotEmpty(tenantColumns)) {
			ew.setTenantColumn(unpack(tenantColumns, userTag));
			if (Checker.beEmpty(ew.getTenantColumn())) {
				ew.setTenantColumn(new String[] {PermConstant.TENANT_ID});
			}
		}


		//总是添加CREATE_USER列作为过滤条件
		if (Boolean.TRUE.equals(bac.beAlwaysFilterCreateUserColumn()) && !beHaveCreateUserColumn(ew.getUserColumn())) {
			List<String> userColumnList = Checker.beEmpty(ew.getUserColumn()) ? Lists.newArrayList() : Lists.newArrayList(ew.getUserColumn());
			userColumnList.add(PermConstant.CREATE_USER);
			ew.setUserColumn(userColumnList.toArray(new String[0]));
		}

		ew.setTenantId(currentUser.getTenantId());
		if (Checker.beNotEmpty(currentUser.getHmacBusinessId())) {
			ew.setTenantId(currentUser.getHmacBusinessId());
		}

		ew.setTableNameWithAuthAppend(tableNameAuthInject);
		ew.setTableNameWithColumnAppend(ObjectUtil.defaultIfNull(tableNameColumnInject, tableNameAuthInject));

		detailMapperIds.stream().filter(Checker::beNotEmpty).forEach(b -> MapperAuthContext.set(b, ew));
	}


}
