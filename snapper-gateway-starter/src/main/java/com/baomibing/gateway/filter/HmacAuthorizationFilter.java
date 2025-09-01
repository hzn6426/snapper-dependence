/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.gateway.filter;

import com.baomibing.tool.util.Checker;
import com.google.common.collect.Sets;
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


/**
 * HmacAuthorizationFilter
 *
 * @author zening 2022/5/10 11:42
 * @version 1.0.0
 */
public class HmacAuthorizationFilter extends BaseFilter implements GlobalFilter, Ordered {

    private static final CopyOnWriteArraySet<String> whites = new CopyOnWriteArraySet<>(Sets.newHashSet(
            JWT_API_PREFIX,
            THIRD_API_PREFIX,
            TENANT_API_PREFIX
    ));

    @Override
    public void addWhites(Set<String> urls) {
        if (Checker.beNotEmpty(urls)) {
            whites.addAll(urls);
        }
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = getRequest(exchange);
        String url = getUrl(request);
        if (matchWhiteList(whites, url)) {
            return chain.filter(exchange);
        }

        if (beFeignInvoke(request)) {
            return chain.filter(exchange);
        }

        //USER_ROLES 已在HmacFilter中添加到头部
        String cacheHashKey = getHeader(request, USER_ROLES);
        //校验授权
        verifyAuthorization(request, cacheHashKey);

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return ORDER_AFTER_ROUTE;
    }
}
