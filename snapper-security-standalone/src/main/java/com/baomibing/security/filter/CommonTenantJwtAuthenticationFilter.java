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

import com.baomibing.authority.exception.InvalidTokenException;
import com.baomibing.tool.constant.UserHeaderConstant;
import com.baomibing.tool.constant.WebConstant;
import com.baomibing.tool.user.TenantUserKey;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ObjectUtil;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.baomibing.tool.constant.TenantRedisKeyConstant.*;

/**
 * jwt拦截器，拦截设置当前登录用户信息
 */
@Slf4j
public class CommonTenantJwtAuthenticationFilter extends BaseFilter {

	private static final Set<String> whites = new CopyOnWriteArraySet<>(Sets.newHashSet(
			WebConstant.TENANT_API_WEIXIN_URL,
			WebConstant.TENANT_API_REGISTER_URL,
			WebConstant.TENANT_API_TOKEN_URL,
			WebConstant.TENANT_API_USER_LOG_URL,
			WebConstant.HMAC_API_PREFIX,
			WebConstant.THIRD_API_PREFIX,
			WebConstant.JWT_API_PREFIX
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

	private void wrapHeader(HttpServletRequest request) {

		String userName = getHeader(request, UserHeaderConstant.USER_NAME);
		String systemTag = getHeader(request, UserHeaderConstant.USER_SYSTEM_TAG);
		String contextKey = TenantUserKey.userContextKey(userName, systemTag);
		String groupId = ObjectUtil.toStringIfNotNull(cacheService.hGet(contextKey, KEY_USER_GROUP_ID));
		String groupName = ObjectUtil.toStringIfNotNull(cacheService.hGet(contextKey, KEY_USER_DEPARTMENT));
		String positionId = ObjectUtil.toStringIfNotNull(cacheService.hGet(contextKey, KEY_USER_POSITION_ID));
		String companyId = ObjectUtil.toStringIfNotNull(cacheService.hGet(contextKey, KEY_USER_COMPANY));
		String companyName = ObjectUtil.toStringIfNotNull(cacheService.hGet(contextKey, KEY_USER_COMPANY_NAME));
		String userCacheAuths = ObjectUtil.toStringIfNotNull(cacheService.hGet(contextKey, KEY_USER_ROLE_ID));
		String userTag = ObjectUtil.toStringIfNotNull(cacheService.hGet(contextKey, KEY_USER_TAG));
		addHeader(request, UserHeaderConstant.USER_GROUP, groupId);
		addHeader(request, UserHeaderConstant.USER_GROUP_NAME, groupName);
		addHeader(request, UserHeaderConstant.USER_POSITION, positionId);
		addHeader(request, UserHeaderConstant.USER_ROLES, userCacheAuths);
		addHeader(request, UserHeaderConstant.USER_COMPANY_ID, companyId);
		addHeader(request, UserHeaderConstant.USER_TAG, userTag);
		addHeader(request, UserHeaderConstant.USER_COMPANY_NAME, companyName);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String url = request.getRequestURI();

		String protocol = request.getProtocol();
		if (beSocketProtocol(protocol)) {
			filterChain.doFilter(request, response);
			return;
		}

		if (matchWhiteList(url)) {
			filterChain.doFilter(request, response);
			return;
		}

		if (beTenantUserValidateForDepartments(url)) {
			filterChain.doFilter(request, response);
			return;
		}


		if (beTenantLoginRequest(url)) {
			filterChain.doFilter(request, response);
			return;
		}


		//提取token
		String token = getToken(request);
		if (Checker.beEmpty(token)) {
			throw new InvalidTokenException();
		}

		final String copyToken = token;
		addHeader(request, UserHeaderConstant.USER_TOKEN, copyToken);
		token = token.substring(WebConstant.JWT_BEAR_TYPE.length()).trim();


		verifyTenantToken(request, token);
		wrapHeader(request);
		filterChain.doFilter(request, response);
	}


}
