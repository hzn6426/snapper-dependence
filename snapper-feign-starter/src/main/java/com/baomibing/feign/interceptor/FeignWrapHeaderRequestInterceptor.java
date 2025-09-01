/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.feign.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.baomibing.cache.redis.RedisService;
import com.baomibing.tool.constant.RedisKeyConstant;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.constant.UserHeaderConstant;
import com.baomibing.tool.user.TenantUser;
import com.baomibing.tool.user.UserContext;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ObjectUtil;
import com.baomibing.tool.util.SnowflakeIdWorker;
import com.baomibing.tool.util.URLUtil;
import com.google.common.base.Joiner;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;

@Slf4j
public class FeignWrapHeaderRequestInterceptor implements RequestInterceptor {

	@Autowired private RedisService redisService;
	//封装当前用户信息-如果feign间的调用仍然被gateway拦截，则此处需要进一步优化

	private void wrapUserHeader(RequestTemplate template) {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		boolean beNullRequest = Checker.beNull(requestAttributes);
		HttpServletRequest request = beNullRequest ? null : ((ServletRequestAttributes) requestAttributes).getRequest();

		boolean beFetchInCache = false;

		String userId = "", ucnName = "", uenName = "", userName = "", userGroup = "", userPosition = "", userRoles = "",
				gatewayTag = "", userTag = "", companyId = "", systemTag = "", tenantId = "",
				outerSystemName="", hmacUserName ="", hmacUserCnName="", hmacGroupId="", hmacGroupName="";

		if (Checker.beNotNull(request)) {
			gatewayTag = request.getHeader(UserHeaderConstant.GATE_WAY_REDIRECT_TAG);
			if (Checker.beNotEmpty(gatewayTag)) {
				String v = redisService.get(MessageFormat.format(RedisKeyConstant.KEY_USER_GATE_WAY_ID, gatewayTag));
				if (Checker.beNotEmpty(v)) {
					TenantUser user = JSONObject.parseObject(v, TenantUser.class);
					userId = user.getId();
					ucnName = user.getUserCnName();
					uenName = user.getUserEnName();
					userName = user.getUserName();
					userGroup = user.getCurrentGroupId();
					userPosition = user.getCurrentPositionId();
					userRoles = Checker.beNotEmpty(user.getRoles()) ? Joiner.on(",").join(user.getRoles()) : "";
					userTag = user.getUserTag();
					companyId = user.getCompanyId();
					systemTag = user.getSystemTag();
					tenantId = user.getTenantId();
					outerSystemName = user.getOuterSystemName();
					hmacUserName = user.getHmacUserName();
					hmacUserCnName = user.getHmacUserCnName();
					hmacGroupId = user.getHmacGroupId();
					hmacGroupName = user.getHmacGroupName();
					beFetchInCache = true;
				}
			}
		}
		if (!beFetchInCache) {

			userId = ObjectUtil.defaultIfNull(UserContext.currentUserId(), beNullRequest ? null : request.getHeader(UserHeaderConstant.USER_ID));
			ucnName = ObjectUtil.defaultIfNull(UserContext.currentUserCnName(), beNullRequest ? null : request.getHeader(UserHeaderConstant.USER_CN_NAME));
			uenName = ObjectUtil.defaultIfNull(UserContext.currentUserEnName(), beNullRequest ? null : request.getHeader(UserHeaderConstant.USER_EN_NAME));
			userName = ObjectUtil.defaultIfNull(UserContext.currentUserName(), beNullRequest ? null : request.getHeader(UserHeaderConstant.USER_NAME));
			userGroup = ObjectUtil.defaultIfNull(UserContext.currentUserGroupId(), beNullRequest ? null : request.getHeader(UserHeaderConstant.USER_GROUP));
			userPosition = ObjectUtil.defaultIfNull(UserContext.currentUserPositionId(), beNullRequest ? null : request.getHeader(UserHeaderConstant.USER_POSITION));
			gatewayTag = ObjectUtil.defaultIfNull(UserContext.currentGateWayTag(), beNullRequest ? null : request.getHeader(UserHeaderConstant.GATE_WAY_REDIRECT_TAG));
			userRoles = ObjectUtil.defaultIfNull(UserContext.currentUserRoles(), beNullRequest ? null : request.getHeader(UserHeaderConstant.USER_ROLES));
			userTag = ObjectUtil.defaultIfNull(UserContext.currentUserTag(), beNullRequest ? null : request.getHeader(UserHeaderConstant.USER_TAG));
			companyId = ObjectUtil.defaultIfNull(UserContext.currentCompanyId(), beNullRequest ? null : request.getHeader(UserHeaderConstant.USER_COMPANY_ID));
			systemTag = ObjectUtil.defaultIfNull(UserContext.currentSystemTag(), beNullRequest ? null : request.getHeader(UserHeaderConstant.USER_SYSTEM_TAG));
			tenantId = ObjectUtil.defaultIfNull(UserContext.currentTenantId(), beNullRequest ? null : request.getHeader(UserHeaderConstant.USER_TENANT_ID));
			outerSystemName = ObjectUtil.defaultIfNull(UserContext.currentOuterSystemName(), beNullRequest ? null : request.getHeader(UserHeaderConstant.USER_OUTER_SYSTEM));
			hmacUserName = ObjectUtil.defaultIfNull(UserContext.hmacUserName(), beNullRequest ? null : request.getHeader(UserHeaderConstant.HMAC_USER_NAME));
			hmacUserCnName = ObjectUtil.defaultIfNull(UserContext.hmacUserCnName(), beNullRequest ? null : request.getHeader(UserHeaderConstant.HMAC_USER_CN_NAME));
			hmacGroupId = ObjectUtil.defaultIfNull(UserContext.hmacUserGroupId(), beNullRequest ? null : request.getHeader(UserHeaderConstant.HMAC_USER_GROUP));
			hmacGroupName = ObjectUtil.defaultIfNull(UserContext.hmacUserGroupName(), beNullRequest ? null : request.getHeader(UserHeaderConstant.HMAC_USER_GROUP_NAME));

		}

		template.header(UserHeaderConstant.USER_ID, userId)
				.header(UserHeaderConstant.USER_CN_NAME, Checker.beNotEmpty(ucnName) ? URLUtil.encode(ucnName) : ucnName)
				.header(UserHeaderConstant.USER_EN_NAME, uenName)
				.header(UserHeaderConstant.USER_NAME, userName)
				.header(UserHeaderConstant.USER_GROUP, userGroup)
				.header(UserHeaderConstant.USER_POSITION, userPosition)
				.header(UserHeaderConstant.GATE_WAY_REDIRECT_TAG, gatewayTag)
				.header(UserHeaderConstant.USER_ROLES, userRoles)
				.header(UserHeaderConstant.USER_COMPANY_ID, companyId)
				.header(UserHeaderConstant.USER_SYSTEM_TAG, systemTag)
				.header(UserHeaderConstant.USER_TAG, userTag)
				.header(UserHeaderConstant.USER_TENANT_ID, tenantId)
				.header(UserHeaderConstant.HMAC_USER_NAME, hmacUserName)
				.header(UserHeaderConstant.HMAC_USER_CN_NAME, hmacUserCnName)
				.header(UserHeaderConstant.HMAC_USER_GROUP, hmacGroupId)
				.header(UserHeaderConstant.HMAC_USER_GROUP_NAME, hmacGroupName)
				.header(UserHeaderConstant.USER_OUTER_SYSTEM, outerSystemName);
	}

    @Override
    public void apply(RequestTemplate template) {
		String feignId = SnowflakeIdWorker.getId();
		long timeout = RandomUtils.nextInt(2, 8);
		// 设置2分钟超时
		redisService.set(feignId, feignId, timeout * 60);
		template.header(UserHeaderConstant.CONTENT_TYPE, Strings.APPLICATION_JSON).header(UserHeaderConstant.FEIGN_TAG, feignId);
		wrapUserHeader(template);




	}
    
}
