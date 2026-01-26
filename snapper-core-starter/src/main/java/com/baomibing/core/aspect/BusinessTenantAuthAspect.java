
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


import com.baomibing.core.annotation.TenantAction;
import com.baomibing.core.context.BusinessAuthContext;
import com.baomibing.core.context.PermContext;
import com.baomibing.core.context.PermTenantContext;
import com.baomibing.core.spi.BusinessTenantAuthService;
import com.baomibing.core.wrap.TenantEntrustWarpper;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.user.RequestContext;
import com.baomibing.tool.user.TenantUser;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ClassUtil;
import com.baomibing.tool.util.SpiUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;

import java.util.List;

import static com.baomibing.tool.user.UserContext.currentSystemTag;
import static com.baomibing.tool.user.UserContext.currentTenantUser;


/**
 * 业务权限切面，根据服务注解自动将用户的委托和组织的委托放入线程变量中
 * 
 * @author zening
 * @version 1.0.0
 */
@Aspect
@Order(10000)
@Slf4j
public class BusinessTenantAuthAspect {

	public BusinessTenantAuthAspect() {
		List<BusinessTenantAuthService> providers = SpiUtil.load(BusinessTenantAuthService.class);
		if (Checker.beNotEmpty(providers)) {
			authService = providers.get(0);
		}
	}
	private BusinessTenantAuthService authService;

	@Around("@annotation(com.baomibing.core.annotation.TenantAction)")
	public Object roundBusinessAuth(ProceedingJoinPoint point) throws Throwable {
		TenantAction ba = ((MethodSignature) point.getSignature()).getMethod().getAnnotation(TenantAction.class);
		String bae = Checker.beNull(ba) ? null : ba.value();
		String methodName = point.getSignature().getName();
		String className =  ((MethodSignature)point.getSignature()).getMethod().getDeclaringClass().getName();
		String key = className + Strings.DOT + methodName;
		try {TenantUser currentUser = currentTenantUser().orElse(null);
			//如果忽略当前Action直接跳过
			if (!PermContext.hasIgnore(bae) && Checker.beNotNull(currentUser)) {
				buildContext(key, currentUser, ba);
			}
			return point.proceed();
		} finally {
			cleanContext(key);
			PermTenantContext.removeOnlyFilterCompany();
			PermTenantContext.remove(bae);
			PermTenantContext.removeUserNos();
			PermTenantContext.removeIgnoreCreateUserColumn();
			PermTenantContext.removeIgnoreCompanyScope();
			PermTenantContext.removeIgnoreUserScope();
			PermTenantContext.removeIgnoreGroupScope();
		}
	}

	private boolean parseTag(String[] companyUserTags, String userTag) {
		if (Checker.beEmpty(companyUserTags) || Checker.beEmpty(userTag)) {
			return false;
		}
		for (String ut : companyUserTags) {
			if (Checker.beNotEmpty(ut) && userTag.contains(ut)) {
				return true;
			}
		}
		return false;
	}
	
	public void buildContext(String key, TenantUser currentUser, TenantAction ba) {

		//模块不需要权限直接跳过
		if (Checker.beNull(authService)) return;

		if (Checker.beNull(currentUser) || ClassUtil.beNotClassOnly(TenantUser.class, currentUser)) return;

		String[] companyUserTags = ba.onlyFilterCompanyTags();
		String[] ignoreCompanyUserTags = ba.ignoreCompanyScopeTags();
		String scope = ba.defaultScope().name();
		boolean beIgnoreUserScope = ba.ignoreUserScope();
		boolean beIgnoreGroupScope = ba.ignoreGroupScope();
		String userTag = currentUser.getUserTag();
		String bae = ba.value();

		String permId;
		if (Checker.beNull(bae)) {
			permId = authService.getPermIdByUrlAndMethod(RequestContext.reqUrl(), RequestContext.reqMethod());
		} else {
			permId = authService.getPermIdByAction(bae);
		}
		TenantEntrustWarpper ew = authService.getEntrustBusinessPerm(currentUser, permId, scope, beIgnoreUserScope, beIgnoreGroupScope);

		if (Boolean.TRUE.equals(ba.beWebApi())) {
			ew.setBeOnlyFilterTenant(true);
		} else {
			ew.setBeOnlyFilterCompany((beIgnoreGroupScope && beIgnoreUserScope) || parseTag(companyUserTags, userTag)).setBeLoginWithAuthCode(Strings.TEMP.equals(currentSystemTag()))
					.setBeIgnoreCompanyScope(parseTag(ignoreCompanyUserTags, userTag));
		}

		BusinessAuthContext.set(key, ew);
	}
	
	public void cleanContext(String key) {
		BusinessAuthContext.clear(key);
		
	}
}
