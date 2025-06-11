package com.baomibing.security.filter;

import com.baomibing.authority.exception.InvalidTokenException;
import com.baomibing.tool.constant.UserHeaderConstant;
import com.baomibing.tool.constant.WebConstant;
import com.baomibing.tool.user.UserKey;
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

import static com.baomibing.tool.constant.RedisKeyConstant.*;

/**
 * jwt拦截器，拦截设置当前登录用户信息
 *
 */
@Slf4j
public class CommonJwtAuthenticationFilter extends BaseFilter {

	private static final Set<String> whites = new CopyOnWriteArraySet<>(Sets.newHashSet(
			WebConstant.API_WEIXIN_URL,
			WebConstant.API_TTOKEN_URL,
			WebConstant.API_TOKEN_URL,
			WebConstant.API_USER_LOG_URL,
			WebConstant.HMAC_API_PREFIX,
			WebConstant.THIRD_API_PREFIX,
			WebConstant.TENANT_API_PREFIX));

	private boolean matchWhiteList(String url) {
		return whites.stream().anyMatch(w -> pathMatch.match(w, url));
	}

	@Override
	public void addWhites(Set<String> urls) {
		if (Checker.beNotEmpty(urls)) {
			whites.addAll(urls);
		}
	}

	public CommonJwtAuthenticationFilter withTestUser(TestUser user) {
		this.testUser = user;
		return this;
	}


	private void wrapHeader(HttpServletRequest request) {

		String userName = getHeader(request, UserHeaderConstant.USER_NAME);
		String systemTag = getHeader(request, UserHeaderConstant.USER_SYSTEM_TAG);
		String contextKey = UserKey.userContextKey(userName, systemTag);
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

	private boolean wrapIfDevTokenHeader(HttpServletRequest request, String token) {

		if (beDevToken(token)) {
			addHeader(request, HEADER_DEV_TOKEN, HEADER_DEV_TOKEN);
			if (Checker.beNotNull(testUser)) {
				addHeader(request, UserHeaderConstant.USER_ID, testUser.getId());
				addHeader(request, UserHeaderConstant.USER_NAME, testUser.getUserName());
				addHeader(request, UserHeaderConstant.USER_CN_NAME, testUser.getUserCnName());
				addHeader(request, UserHeaderConstant.USER_EN_NAME, testUser.getUserEnName());
				addHeader(request, UserHeaderConstant.USER_GROUP, testUser.getGroupId());
				addHeader(request, UserHeaderConstant.USER_GROUP_NAME, testUser.getGroupName());
				addHeader(request, UserHeaderConstant.USER_POSITION, testUser.getPositionId());
				addHeader(request, UserHeaderConstant.USER_ROLES, testUser.getRoles());
				addHeader(request, UserHeaderConstant.USER_COMPANY_ID, testUser.getCompanyId());
				addHeader(request, UserHeaderConstant.USER_TAG, testUser.getUserTag());
				addHeader(request, UserHeaderConstant.USER_COMPANY_NAME, testUser.getCompanyName());

			} else {
				addHeader(request, UserHeaderConstant.USER_ID, "1410142488756248577");
				addHeader(request, UserHeaderConstant.USER_NAME, "test");
				addHeader(request, UserHeaderConstant.USER_CN_NAME, "测试员");
				addHeader(request, UserHeaderConstant.USER_EN_NAME, "tester");

			}
			return true;
		}
		return false;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String url = request.getRequestURI();


		if (matchWhiteList(url)) {
			filterChain.doFilter(request, response);
			return;
		}


		if (beUserValidateForDepartments(url)) {
			filterChain.doFilter(request, response);
			return;
		}

		if (beLoginRequest(url)) {
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

		//方便调试,设置开发环境固定测试账号
		if (wrapIfDevTokenHeader(request, token)) {
			filterChain.doFilter(request, response);
			return;
		}

		//校验TOKEN
		verifyToken(request, token);
		wrapHeader(request);
		filterChain.doFilter(request, response);
	}
	

}
