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

package com.baomibing.security.filter;

import com.baomibing.authority.service.SysTenantRoleResourceService;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.tool.constant.UserHeaderConstant;
import com.baomibing.tool.constant.WebConstant;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.baomibing.tool.constant.UserHeaderConstant.USER_ROLES;


/**
 * CommonJwtAuthorizationFilter
 *
 * @author zening 2023/9/5 10:33
 * @version 1.0.0
 **/
public class CommonTenantJwtAuthorizationFilter extends BaseFilter {

	@Autowired private SysTenantRoleResourceService tenantRoleResourceService;

	private static final CopyOnWriteArraySet<String> whites =  new CopyOnWriteArraySet<>(Sets.newHashSet(
			WebConstant.TENANT_API_WEIXIN_URL,
			WebConstant.TENANT_API_REGISTER_URL,
			WebConstant.TENANT_API_DEPATMENT_CHANGE,
			WebConstant.TENANT_API_USER_DEPARTMENTS,
			WebConstant.TENANT_API_USER_CURRENT,
			WebConstant.TENANT_API_USER_BUTTONS,
			WebConstant.TENANT_API_USER_ADMIN_MENUS,
			WebConstant.TENANT_API_USER_MENUS,
			WebConstant.TENANT_API_USER_LOG_URL,
			WebConstant.SOCKET_CLIENT,
			WebConstant.THIRD_API_PREFIX,
			WebConstant.JWT_API_PREFIX,
			WebConstant.HMAC_API_PREFIX
	));

	private boolean matchWhiteList(String url) {
		return whites.stream().anyMatch(w -> pathMatch.match(w, url));
	}

	@Override
	public void addWhites(Set<String> urls) {
		if (Checker.beNotEmpty(urls)) {
			whites.addAll(urls);
		}
	}



	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String method = request.getMethod();
		String url = request.getRequestURI();

		String protocol = request.getProtocol();
		if (beSocketProtocol(protocol)) {
			filterChain.doFilter(request, response);
			return;
		}

		if (beTenantLoginOrOutRequest(url)) {
			filterChain.doFilter(request, response);
			return;
		}

		if (beTenantUserValidateForDepartments(url)) {
			filterChain.doFilter(request, response);
			return;
		}

		// 白名单需要添加用户信息
		if (matchWhiteList(url)) {
			filterChain.doFilter(request, response);
			return;
		}


		//获取当前用户名
		String userName = getHeader(request, UserHeaderConstant.USER_NAME);
		String tenantId = getHeader(request, UserHeaderConstant.USER_TENANT_ID);

		if (Checker.beEmpty(tenantId)) {
			throw new ServerRuntimeException(ExceptionEnum.TENANT_ID_NOT_VALID);
		}

		if (beDevUser(userName)) {
			filterChain.doFilter(request, response);
			return;
		}

		String userCacheAuths = getHeader(request, USER_ROLES);
		url = handleGetUri(url);
		String needRole = tenantRoleResourceService.getRolesByUrl(tenantId, url, method);

		verifyTenantAuthorization(needRole, userCacheAuths);
		filterChain.doFilter(request, response);
	}
}
