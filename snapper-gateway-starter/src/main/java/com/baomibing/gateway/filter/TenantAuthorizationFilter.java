/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.gateway.filter;

import com.baomibing.gateway.exception.GateWayExceptionEnum;
import com.baomibing.gateway.exception.GateWayRuntimeException;
import com.baomibing.gateway.spi.BusinessTenantRoleService;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.constant.UserHeaderConstant;
import com.baomibing.tool.util.CharacterUtil;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.SpiUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.baomibing.gateway.constant.GateWayConstant.ORDER_AFTER_ROUTE;
import static com.baomibing.tool.constant.PermConstant.RESOURCE_NO_NEED_ROLE;
import static com.baomibing.tool.constant.UserHeaderConstant.USER_ROLES;
import static com.baomibing.tool.constant.WebConstant.*;

@Slf4j
public class TenantAuthorizationFilter extends BaseFilter implements GlobalFilter, Ordered {

	private BusinessTenantRoleService businessTenantRoleService;

	public TenantAuthorizationFilter() {
		List<BusinessTenantRoleService> services = SpiUtil.load(BusinessTenantRoleService.class);
		if (Checker.beNotEmpty(services)) {
			businessTenantRoleService = services.get(0);
		}
	}

	private static final CopyOnWriteArraySet<String> whites =  new CopyOnWriteArraySet<>(Sets.newHashSet(
			TENANT_API_WEIXIN_URL,
			TENANT_API_REGISTER_URL,
			TENANT_API_VALIDATE_USER_FOR_DEPARTMENTS,
			TENANT_API_DEPATMENT_CHANGE,
			TENANT_API_USER_DEPARTMENTS,
			TENANT_API_USER_CURRENT,
			TENANT_API_USER_BUTTONS,
			TENANT_API_USER_ADMIN_MENUS,
			TENANT_API_USER_MENUS,
			TENANT_API_USER_LOG_URL,
			SOCKET_CLIENT,
			THIRD_API_PREFIX,
			HMAC_API_PREFIX,
			JWT_API_PREFIX

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
		String tenantId = getHeader(request, UserHeaderConstant.USER_TENANT_ID);
		String method = request.getMethodValue();
		ServerHttpRequest.Builder mutate = request.mutate();

		if (beTenantLoginOrOutRequest(url)) {
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

		if (Checker.beEmpty(tenantId)) {
			throw new GateWayRuntimeException(GateWayExceptionEnum.TENANT_ID_NOT_VALID);
		}

		//没有角色认为没有权限
		if (Checker.beBlank(userCacheAuths)) {
			throw new GateWayRuntimeException(GateWayExceptionEnum.NO_PRIVILEGE_EXCEPTION);
		}

		int index = url.lastIndexOf("/");
		String charsAfterSlash = url.substring(index + 1);
		if (charsAfterSlash.equals(CharacterUtil.justLeftNumbers(charsAfterSlash))) {
			url = url.replace(charsAfterSlash, Strings.BRACE);
		}

		String needRole = businessTenantRoleService.getRolesByUrl(tenantId, url, method);

		//URL资源不需要权限
		if (RESOURCE_NO_NEED_ROLE.equals(needRole)) {
			return chain.filter(exchange);
		}

		//校验授权
		Set<String> needRoles = new HashSet<>(Splitter.on(Strings.COMMA).splitToList(needRole));
		Set<String> userRoles = new HashSet<>(Splitter.on(Strings.COMMA).splitToList(userCacheAuths));
		boolean match = needRoles.stream().anyMatch(userRoles::contains);
		if (!match) {
			throw new GateWayRuntimeException(GateWayExceptionEnum.NO_PRIVILEGE_EXCEPTION);
		}
		return chain.filter(exchange);
	}
}
