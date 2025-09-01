/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.baomibing.gateway.exception.InvalidTokenException;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.URLUtil;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.baomibing.gateway.constant.GateWayConstant.ORDER_TWO;
import static com.baomibing.tool.constant.WebConstant.*;

@Slf4j
public class ThirdPartFilter extends BaseFilter implements GlobalFilter, Ordered {

    @Value("${baomibing.thirdPartParamName:extendedParams}")
    private String thirdPartParam = "extendedParams";

    private static final Set<String> whites = new CopyOnWriteArraySet<>(Sets.newHashSet(
            HMAC_API_PREFIX,
            JWT_API_PREFIX,
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

        ServerHttpRequest request = exchange.getRequest();

        if (matchWhiteList(whites, request.getPath().pathWithinApplication().value())) {
            return chain.filter(exchange);
        }

        if (hasGatewayAuthed(request)) {
            return chain.filter(exchange);
        }

        if (beFeignInvoke(request)) {
            return chain.filter(exchange);
        }

        String url = request.getPath().pathWithinApplication().value();
        if (beLoginRequest(url)) {
            return chain.filter(exchange);
        }

        String params = exchange.getAttribute(ServerWebExchangeUtils.CACHED_REQUEST_BODY_ATTR);
        if (Checker.beEmpty(params)) {
            throw new InvalidTokenException();
        }
        String token;
        //提取token - 第三方回调根据具体情况来进行TOKEN提取
        try {
            String extendedParams = JSON.parseObject(params).getString(thirdPartParam);
            token = JSON.parseObject(extendedParams).getString("token");
        } catch (JSONException joe) {
            throw new InvalidTokenException();
        }

        if (Checker.beEmpty(token)) {
            throw new InvalidTokenException();
        }
        token = URLUtil.decode(token).substring(JWT_BEAR_TYPE.length()).trim();

        verifyToken(request, token);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return ORDER_TWO;
    }


}
