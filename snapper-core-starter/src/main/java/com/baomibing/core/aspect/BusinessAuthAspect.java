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


import com.alibaba.fastjson.JSONObject;
import com.baomibing.cache.CacheService;
import com.baomibing.core.annotation.Action;
import com.baomibing.core.base.ActionScope;
import com.baomibing.core.context.BusinessAuthContext;
import com.baomibing.core.context.PermContext;
import com.baomibing.core.spi.BusinessAuthService;
import com.baomibing.core.wrap.EntrustWarpper;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.perm.ActionAnnotation;
import com.baomibing.tool.user.RequestContext;
import com.baomibing.tool.user.User;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ClassUtil;
import com.baomibing.tool.util.SpiUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import java.util.List;

import static com.baomibing.tool.constant.RedisKeyConstant.KEY_ACTION_CONNECT_PREFIX;
import static com.baomibing.tool.user.UserContext.currentSystemTag;
import static com.baomibing.tool.user.UserContext.currentUser;


/**
 * 业务权限切面，根据服务注解自动将用户的委托和组织的委托放入线程变量中
 * 
 * @author zening
 * @version 1.0.0
 */
@Aspect
@Order(10000)
@Slf4j
public class BusinessAuthAspect {

	@Autowired  private CacheService cacheService;

	public BusinessAuthAspect() {
		List<BusinessAuthService> providers = SpiUtil.load(BusinessAuthService.class);
		if (Checker.beNotEmpty(providers)) {
			authService = providers.get(0);
		}
	}
	private BusinessAuthService authService;

	@Around("@annotation(com.baomibing.core.annotation.Action)")
	public Object roundBusinessAuth(ProceedingJoinPoint point) throws Throwable {
		Action ba = ((MethodSignature) point.getSignature()).getMethod().getAnnotation(Action.class);
		ActionAnnotation aa = new ActionAnnotation();
		aa.setActionValue(ba.value())
			.setActionIgnoreUserScope(ba.ignoreUserScope())
			.setActionIgnoreGroupScope(ba.ignoreGroupScope())
			.setActionIgnoreCompanyScopeTags(Checker.beNotEmpty(ba.ignoreCompanyScopeTags()) ? String.join(Strings.COMMA,ba.ignoreCompanyScopeTags()): null)
			.setActionOnlyFilterCompanyTags(Checker.beNotEmpty(ba.onlyFilterCompanyTags()) ? String.join(Strings.COMMA,ba.onlyFilterCompanyTags()): null);
		String caches = cacheService.get(KEY_ACTION_CONNECT_PREFIX + ba.value());
		if (Checker.beNotEmpty(caches)) {
			aa = JSONObject.parseObject(caches, ActionAnnotation.class);
		}

		User currentUser = currentUser().orElse(null);
		String bae = aa.getActionValue();
		String methodName = point.getSignature().getName();
		String className =  ((MethodSignature)point.getSignature()).getMethod().getDeclaringClass().getName();
		String key = className + Strings.DOT + methodName;
		try {
			//如果忽略当前Action直接跳过
			if (!PermContext.hasIgnore(bae)) {
				buildContext(key, currentUser, aa);
			}
			return point.proceed();
		} finally {
			cleanContext(key);
			PermContext.removeOnlyFilterCompany();
			PermContext.remove(bae);
			PermContext.removeUserNos();
			PermContext.removeIgnoreCreateUserColumn();
			PermContext.removeIgnoreCompanyScope();
			PermContext.removeIgnoreUserScope();
			PermContext.removeIgnoreGroupScope();
			PermContext.removeCommaInCreateUserColumn();
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
	
	public void buildContext(String key, User currentUser, ActionAnnotation ba) {

		//模块不需要权限直接跳过
		if (Checker.beNull(authService)) return;

		if (Checker.beNull(currentUser) || ClassUtil.beNotClassOnly(User.class, currentUser)) return;

//		String[] companyUserTags = ba.onlyFilterCompanyTags();
		String[] companyUserTags = Checker.beNotEmpty(ba.getActionOnlyFilterCompanyTags())  ? ba.getActionOnlyFilterCompanyTags().split(Strings.COMMA) : new String[0];
//		String[] ignoreCompanyUserTags = ba.ignoreCompanyScopeTags();
		String[] ignoreCompanyUserTags = Checker.beNotEmpty(ba.getActionIgnoreCompanyScopeTags()) ? ba.getActionIgnoreCompanyScopeTags().split(Strings.COMMA) : new String[0];
//		String scope = ba.defaultScope().name();
		String scope = ActionScope.CURRENT_COMPANY.name();
//		boolean beIgnoreUserScope = ba.ignoreUserScope();
//		boolean beIgnoreGroupScope = ba.ignoreGroupScope();
		boolean beIgnoreUserScope = ba.getActionIgnoreUserScope();
		boolean beIgnoreGroupScope = ba.getActionIgnoreGroupScope();
		String userTag = currentUser.getUserTag();
//		String bae = ba.value();
		String bae = ba.getActionValue();

		String permId;
		if (Checker.beNull(bae)) {
			permId = authService.getPermIdByUrlAndMethod(RequestContext.reqUrl(), RequestContext.reqMethod());
		} else {
			permId = authService.getPermIdByAction(bae);
		}
		EntrustWarpper ew = authService.getEntrustBusinessPerm(currentUser, permId, scope, beIgnoreUserScope, beIgnoreGroupScope);
		ew.setBeOnlyFilterCompany((beIgnoreGroupScope && beIgnoreUserScope) || parseTag(companyUserTags, userTag)).setBeLoginWithAuthCode(Strings.TEMP.equals(currentSystemTag()))
				.setBeIgnoreCompanyScope(parseTag(ignoreCompanyUserTags, userTag)).setAction(ba.getActionValue());
		BusinessAuthContext.set(key, ew);
	}
	
	public void cleanContext(String key) {
		BusinessAuthContext.clear(key);
		
	}
}
