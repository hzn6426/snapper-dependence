/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.gateway.filter;

import com.baomibing.tool.constant.UserHeaderConstant;
import com.baomibing.tool.log.LogConstant;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.baomibing.gateway.constant.GateWayConstant.ORDER_AFTER_AUTH;

@Slf4j
public class LogTraceFilter extends BaseFilter implements GlobalFilter, Ordered {

    private final CopyOnWriteArraySet<String> whites = new CopyOnWriteArraySet<>();

    @Override
    public void addWhites(Set<String> urls) {
        if (Checker.beNotEmpty(urls)) {
            whites.addAll(urls);
        }
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String url = getUrl(request);
        if (matchWhiteList(whites, url)) {
            return chain.filter(exchange);
        }
    	if (hasGatewayAuthed(exchange.getRequest())) {
        	return chain.filter(exchange);
        }
        if (beFeignInvoke(request)) {
            return chain.filter(exchange);
        }
        //链路追踪id
        String traceId = SnowflakeIdWorker.getId();
        String gatewayId = SnowflakeIdWorker.getId();
        ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate()
        .headers(h -> {
            h.add(LogConstant.MDC_TRACE_HEADER, traceId);
            h.add(UserHeaderConstant.GATE_WAY_REDIRECT_TAG, gatewayId);
        })
        .build();

        MDC.put(LogConstant.MDC_TRACE, traceId);
        MDC.put(LogConstant.MDC_USER_ID, getHeader(request, UserHeaderConstant.USER_NAME));
        ServerWebExchange build = exchange.mutate().request(serverHttpRequest).build();
        //设置gatewayId,feign间调用判断是否存在ID
        long min = RandomUtils.nextInt(5, 10);
        redisService.set(gatewayId, gatewayId, min * 60);
        return chain.filter(build).doFinally((onFinally) -> {
            MDC.remove(LogConstant.MDC_USER_ID);
            MDC.remove(LogConstant.MDC_TRACE);
        });
    }

	@Override
	public int getOrder() {
		return ORDER_AFTER_AUTH;
	}
    
    

}
