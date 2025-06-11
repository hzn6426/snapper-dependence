/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.gateway.filter;

import com.baomibing.gateway.exception.GateWayExceptionEnum;
import com.baomibing.gateway.exception.GateWayRuntimeException;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.baomibing.gateway.constant.GateWayConstant.ORDER_AFTER_ROUTE;
import static com.baomibing.tool.constant.UserHeaderConstant.USER_ROLES;
import static com.baomibing.tool.constant.WebConstant.*;

@Slf4j
public class AuthorizationFilter extends BaseFilter implements GlobalFilter, Ordered {

	private static final CopyOnWriteArraySet<String> whites =  new CopyOnWriteArraySet<>(Sets.newHashSet(
			API_VALIDATE_USER_FOR_DEPARTMENTS,
			API_DEPATMENT_CHANGE,
			API_USER_DEPARTMENTS,
			API_USER_CURRENT,
			API_USER_BUTTONS,
			API_USER_ADMIN_MENUS,
			API_USER_MENUS,
			API_USER_LOG_URL,
			SOCKET_CLIENT,
			THIRD_API_PREFIX,
			HMAC_API_PREFIX,
			TENANT_API_PREFIX

	));

	@Override
	public void addWhites(Set<String> urls) {
		if (Checker.beNotEmpty(urls)) {
			whites.addAll(urls);
		}
	}

	private boolean beDevToken(ServerHttpRequest request) {
		return Strings.DEV.equals(profiles) && HEADER_DEV_TOKEN.equals(getHeader(request, HEADER_DEV_TOKEN));
	}

	@Override
	public int getOrder() {
		return ORDER_AFTER_ROUTE;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = getRequest(exchange);
		String url = getUrl(request);
		ServerHttpRequest.Builder mutate = request.mutate();

		if (beLoginOrOutRequest(url)) {
			return chain.filter(exchange);
		}

		if (hasGatewayAuthed(request)) {
			return chain.filter(exchange);
		}

		//开发环境测试账号
		if (beDevToken(request)) {
			return chain.filter(exchange);
		}

		String userCacheAuths = getHeader(request, USER_ROLES);

		// 白名单需要添加用户信息
		if (matchWhiteList(whites, url)) {
			return chain.filter(exchange);
		}

		// feign调用对应的API不需要权限
		if (beFeignInvoke(request)) {
			return chain.filter(exchange);
		}

		//没有角色认为没有权限
		if (Checker.beBlank(userCacheAuths)) {
			throw new GateWayRuntimeException(GateWayExceptionEnum.NO_PRIVILEGE_EXCEPTION);
		}

		//校验授权
		verifyAuthorization(request, userCacheAuths);
		return chain.filter(exchange);
	}
}
