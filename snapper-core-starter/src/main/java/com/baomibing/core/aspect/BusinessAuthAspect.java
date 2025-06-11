/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.aspect;


import com.baomibing.core.annotation.Action;
import com.baomibing.core.context.BusinessAuthContext;
import com.baomibing.core.context.PermContext;
import com.baomibing.core.spi.BusinessAuthService;
import com.baomibing.core.wrap.EntrustWarpper;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.user.RequestContext;
import com.baomibing.tool.user.User;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.SpiUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;

import java.util.List;

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
		User currentUser = currentUser().orElse(null);
		String bae = Checker.beNull(ba) ? null : ba.value();
		String methodName = point.getSignature().getName();
		String className =  ((MethodSignature)point.getSignature()).getMethod().getDeclaringClass().getName();
		String key = className + Strings.DOT + methodName;
		try {
			//如果忽略当前Action直接跳过
			if (!PermContext.hasIgnore(bae)) {
				buildContext(key, currentUser, ba);
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
	
	public void buildContext(String key, User currentUser, Action ba) {

		//模块不需要权限直接跳过
		if (Checker.beNull(authService)) return;

		if (Checker.beNull(currentUser)) return;

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
		EntrustWarpper ew = authService.getEntrustBusinessPerm(currentUser, permId, scope, beIgnoreUserScope, beIgnoreGroupScope);
		ew.setBeOnlyFilterCompany((beIgnoreGroupScope && beIgnoreUserScope) || parseTag(companyUserTags, userTag)).setBeLoginWithAuthCode(Strings.TEMP.equals(currentSystemTag()))
				.setBeIgnoreCompanyScope(parseTag(ignoreCompanyUserTags, userTag));
		BusinessAuthContext.set(key, ew);
	}
	
	public void cleanContext(String key) {
		BusinessAuthContext.clear(key);
		
	}
}
