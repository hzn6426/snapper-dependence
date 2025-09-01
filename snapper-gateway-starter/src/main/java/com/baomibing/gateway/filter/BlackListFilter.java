/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.gateway.filter;

import com.baomibing.tool.constant.UserHeaderConstant;
import com.baomibing.tool.constant.WebConstant;
import com.baomibing.tool.util.Checker;
import inet.ipaddr.IPAddressString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.baomibing.gateway.constant.GateWayConstant.ORDER_ONE;

@Slf4j
public class BlackListFilter extends BaseFilter implements GlobalFilter, Ordered {
    private static final CopyOnWriteArraySet<String> blacks = new CopyOnWriteArraySet<>();
    private static final CopyOnWriteArraySet<String> whites =  new CopyOnWriteArraySet<>();

    public void addBlacks(Set<String> urls) {
        blacks.addAll(urls);
    }

    @Override
    public void addWhites(Set<String> urls) {
        if (Checker.beNotEmpty(urls)) {
            whites.addAll(urls);
        }
    }

    private boolean matchIps(String ip) {
        IPAddressString ipString = new IPAddressString(ip);
        for (String ipSegment : blacks) {
            IPAddressString segmentString = new IPAddressString(ipSegment);
            if (segmentString.contains(ipString)) return true;
        }
        return false;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        if (hasGatewayAuthed(exchange.getRequest())) {
            return chain.filter(exchange);
        }

        String ip = getIP(exchange.getRequest());
        if (matchIps(ip)) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        ServerHttpRequest request = exchange.getRequest();;
        ServerHttpRequest.Builder mutate = exchange.getRequest().mutate();
        if (Checker.beEmpty(getHeader(request, WebConstant.CONTENT_TYPE))) {
            addHeader(mutate, WebConstant.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        }
        addHeader(mutate, UserHeaderConstant.USER_IP, ip);
        return chain.filter(exchange);

    }


    @Override
    public int getOrder() {
        return ORDER_ONE;
    }



}
