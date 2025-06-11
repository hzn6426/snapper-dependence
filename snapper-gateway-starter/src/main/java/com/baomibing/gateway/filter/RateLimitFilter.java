/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.baomibing.gateway.rule.RateLimitRule;
import com.baomibing.tool.constant.RedisKeyConstant;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.constant.UserHeaderConstant;
import com.baomibing.tool.constant.WebConstant;
import com.baomibing.tool.limit.*;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.baomibing.gateway.constant.GateWayConstant.ORDER_AFTER_AUTH;

@Slf4j
public class RateLimitFilter extends BaseFilter implements GlobalFilter, Ordered {

    private Map<String, RateLimitRule> ruleMap = Maps.newHashMap();

    private static final CopyOnWriteArraySet<String> whites = new CopyOnWriteArraySet<>(Sets.newHashSet(WebConstant.API_TOKEN_URL, WebConstant.API_USER_LOG_URL,
            WebConstant.HMAC_API_PREFIX, WebConstant.THIRD_API_PREFIX, WebConstant.API_DEPATMENT_CHANGE, WebConstant.API_USER_DEPARTMENTS, WebConstant.API_USER_CURRENT,
            WebConstant.API_USER_BUTTONS, WebConstant.API_USER_ADMIN_MENUS, WebConstant.API_USER_MENUS, WebConstant.SOCKET_CLIENT, WebConstant.API_VALIDATE_USER_FOR_DEPARTMENTS,
            WebConstant.API_LIMIT_REFRESH_CACHE,
            WebConstant.TENANT_API_TOKEN_URL, WebConstant.TENANT_API_USER_LOG_URL,
            WebConstant.TENANT_API_DEPATMENT_CHANGE, WebConstant.TENANT_API_USER_DEPARTMENTS, WebConstant.TENANT_API_USER_CURRENT,
            WebConstant.TENANT_API_USER_BUTTONS, WebConstant.TENANT_API_USER_ADMIN_MENUS, WebConstant.TENANT_API_USER_MENUS
    ));
    private boolean matchWhiteList(String url) {
        return whites.stream().anyMatch(w -> pathMatch.match(w, url));
    }

    private  final String LIMIT_KEY_PREFIX = "_LIMIT_KEY_";

    @Override
    public void addWhites(Set<String> urls) {
        if (Checker.beNotEmpty(urls)) {
            whites.addAll(urls);
        }
    }

    private RateLimitRule createIfNotExist(GateLimit limit) {
        RateLimitRule cacheLimit = ruleMap.get(limit.getId());
        if (Checker.beNull(cacheLimit) || !cacheLimit.getUuid().equals(limit.getUuid())) {
            cacheLimit = new RateLimitRule(limit);
            ruleMap.put(limit.getId(), cacheLimit);
        }
        return cacheLimit;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String url = request.getPath().pathWithinApplication().value();

        if (matchWhiteList(url)) {
            return chain.filter(exchange);
        }

        if (hasGatewayAuthed(exchange.getRequest())) {
            return chain.filter(exchange);
        }

        //文件上传不限流
        String contentType = request.getHeaders().getFirst(WebConstant.CONTENT_TYPE);
        if (contentType != null && contentType.contains("multipart")) {
            return chain.filter(exchange);
        }

        List<GateLimit> limits = Lists.newArrayList();
        Object cache = redisService.get(RedisKeyConstant.KEY_RATE_LIMIT);
        if (Checker.beNotNull(cache) && Checker.beNotEmpty(cache.toString())) {
            limits = JSONObject.parseArray(cache.toString(), GateLimit.class);
        }
        if (Checker.beEmpty(limits)) {
            return chain.filter(exchange);
        }

        for (GateLimit limit : limits) {
            RateLimitRule rule = createIfNotExist(limit);
            String key = matchKey(request, limit);
            if (Checker.beNotEmpty(key)) {
                Bucket bucket = rule.getBucket(key);
                if (bucket.tryConsume(1)) {
                    return chain.filter(exchange);
                } else {
                    return chain.filter(exchange);
                }
            }
        }
        return chain.filter(exchange);
    }

    private String matchKey(ServerHttpRequest request, GateLimit limit) {
        String url = request.getPath().pathWithinApplication().value();
        String method = request.getMethodValue();
        String userId = request.getHeaders().getFirst(UserHeaderConstant.USER_ID);
        String ip = request.getHeaders().getFirst(UserHeaderConstant.USER_IP);
        String utag = request.getHeaders().getFirst(UserHeaderConstant.USER_TAG);
        String stag = request.getHeaders().getFirst(UserHeaderConstant.USER_SYSTEM_TAG);

        String tstate = request.getHeaders().getFirst(UserHeaderConstant.USER_TENANT_STATE);
        String rank = request.getHeaders().getFirst(UserHeaderConstant.USER_TENANT_RANK);

        boolean matchUser = true;
        boolean matchUtags = true;
        boolean matchSTags = true;
        boolean matchRequest = true;
        boolean matchTuser = true;


        String limitKey = LIMIT_KEY_PREFIX;
        String ALL = "ALL";
        if (GateLimitRuleEnum.ALL.name().equals(limit.getRuleUser())) {
            limitKey += ALL;
        } else if (GateLimitRuleEnum.CUSTOMER.name().equals(limit.getRuleUser())) {
            List<GateLimitUser> users = limit.getUsers();
            users = Checker.beNotEmpty(users) ? users : Lists.newArrayList();
            matchUser = users.stream().anyMatch(u -> u.getUserId().equals(userId));
            if (matchUser) {
                limitKey += userId;
            }
        } else if (GateLimitRuleEnum.SPECIAL.name().equals(limit.getRuleUser())) {
            limitKey += userId;
        }

        if (GateLimitRuleEnum.ALL.name().equals(limit.getRuleTuser())) {
            limitKey += ALL;
        } else if (GateLimitRuleEnum.STATE.name().equals(limit.getRuleTuser())) {
            List<GateLimitTuser> users = limit.getTusers();
            users = Checker.beNotEmpty(users) ? users : Lists.newArrayList();
            matchTuser = users.stream().anyMatch(u -> u.getLimitValue().equals(tstate));
            if (matchTuser) {
                limitKey += tstate;
            }
        } else if (GateLimitRuleEnum.RANK.name().equals(limit.getRuleTuser())) {
            List<GateLimitTuser> users = limit.getTusers();
            users = Checker.beNotEmpty(users) ? users : Lists.newArrayList();
            matchTuser = users.stream().anyMatch(u -> u.getLimitValue().equals(rank));
            if (matchTuser) {
                limitKey += tstate;
            }
        }

        if (GateLimitRuleEnum.ALL.name().equals(limit.getRuleUserTag())) {
            limitKey += Strings.UNDERSCORE + ALL;
        } else if (GateLimitRuleEnum.CUSTOMER.name().equals(limit.getRuleUserTag())) {
            List<GateLimitUtag> utags = limit.getUtags();
            utags = Checker.beNotEmpty(utags) ? utags : Lists.newArrayList();
            matchUtags = utags.stream().anyMatch(ut -> ut.getTagCode().equals(utag));
            if (matchUtags) {
                limitKey += Strings.UNDERSCORE + utag;
            }
        }

        if (GateLimitRuleEnum.ALL.name().equals(limit.getRuleSystemTag())) {
            limitKey += Strings.UNDERSCORE + ALL;
        } else if (GateLimitRuleEnum.CUSTOMER.name().equals(limit.getRuleSystemTag())) {
            List<GateLimitStag> stags = limit.getStags();
            stags = Checker.beNotEmpty(stags) ? stags : Lists.newArrayList();
            matchSTags = stags.stream().anyMatch(st -> stag.contains(st.getSystemTag()));
            if (matchSTags) {
                limitKey += Strings.UNDERSCORE + stag;
            }
        }

        if (GateLimitRuleEnum.ALL.name().equals(limit.getRuleUrl())) {
            limitKey += Strings.UNDERSCORE + ALL;
        } else if (GateLimitRuleEnum.CUSTOMER.name().equals(limit.getRuleUrl())) {
            List<GateLimitRequest> requests = limit.getRequests();
            GateLimitRequest limitRequest = requests.stream().filter(r -> {
                boolean urlMatch = true;
                if (Checker.beNotEmpty(r.getUrl())) {
                    urlMatch = pathMatch.match(r.getUrl(), url);
                }
                boolean methodMatch = true;
                if (Checker.beNotEmpty(r.getMethod())) {
                    methodMatch = method.equalsIgnoreCase(r.getMethod());
                }
                boolean ipMatch = true;
                if (Checker.beNotEmpty(r.getIp())) {
                    ipMatch = Arrays.asList(r.getIp().split(Strings.COMMA)).contains(ip);
                }
                return urlMatch && methodMatch && ipMatch;
            }).findFirst().orElse(null);
            if (Checker.beNotNull(limitRequest)) {
                String murl = Checker.beNotEmpty(limitRequest.getUrl()) ? url + Strings.UNDERSCORE  : "";
                String mmethod = Checker.beNotEmpty(limitRequest.getMethod()) ? method + Strings.UNDERSCORE : "";
                String mip = Checker.beNotEmpty(limitRequest.getIp()) ? ip : "";
                String mprefix = Checker.beNotEmpty(murl) && Checker.beNotEmpty(mmethod) && Checker.beNotEmpty(mip) ? Strings.UNDERSCORE : "";
                limitKey += mprefix + murl + method + mip;
            } else {
                matchRequest = false;
            }
        }
        if (LIMIT_KEY_PREFIX.equals(limitKey)) {
//            return false;
            return Strings.EMPTY;
        }
        return matchUser && matchUtags && matchSTags && matchRequest && matchTuser ? limitKey : Strings.EMPTY;
    }


    @Override
    public int getOrder() {
        return ORDER_AFTER_AUTH;
    }
}
