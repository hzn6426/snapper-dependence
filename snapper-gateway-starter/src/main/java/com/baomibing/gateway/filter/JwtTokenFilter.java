/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.gateway.filter;

import com.baomibing.gateway.common.TestUser;
import com.baomibing.gateway.exception.InvalidTokenException;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.constant.UserHeaderConstant;
import com.baomibing.tool.user.UserKey;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ObjectUtil;
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

import static com.baomibing.gateway.constant.GateWayConstant.ORDER_TWO;
import static com.baomibing.tool.constant.PermConstant.RESOURCE_NO_NEED_LOGIN;
import static com.baomibing.tool.constant.RedisKeyConstant.*;
import static com.baomibing.tool.constant.WebConstant.*;
/**
 * 拦截器，用于校验JWT权限
 * @author zening
 * @version 1.0.0
 */
@Slf4j
public class JwtTokenFilter extends BaseFilter implements GlobalFilter, Ordered {

    public JwtTokenFilter withTestUser(TestUser user) {
        this.testUser = user;
        return this;
    }
    //第三方回调是不需要授权的回调情况(为了安全性可以只验证TOKEN的合法性,不验证授权,或者任何不验证,但是生成的回调URL有时效性和随机性), HMAC是需要授权的第三方请求
	private static final Set<String> whites = new CopyOnWriteArraySet<>(Sets.newHashSet(
            API_TOKEN_URL,
            API_TTOKEN_URL,
            API_VALIDATE_USER_FOR_DEPARTMENTS,
            API_USER_LOG_URL,
            HMAC_API_PREFIX,
            THIRD_API_PREFIX,
            TENANT_API_PREFIX
    ));

    @Override
    public void addWhites(Set<String> urls) {
        if (Checker.beNotEmpty(urls)) {
            whites.addAll(urls);
        }
    }

    private void wrapHeader(ServerHttpRequest request) {

        ServerHttpRequest.Builder mutate = getRequestBuilder(request);
        String userName = getHeader(request, UserHeaderConstant.USER_NAME);
        String systemTag = getHeader(request, UserHeaderConstant.USER_SYSTEM_TAG);
        String contextKey = UserKey.userContextKey(userName, systemTag);
        String groupId = ObjectUtil.toStringIfNotNull(redisService.hGet(contextKey, KEY_USER_GROUP_ID));
        String groupName = ObjectUtil.toStringIfNotNull(redisService.hGet(contextKey, KEY_USER_DEPARTMENT));
        String positionId = ObjectUtil.toStringIfNotNull(redisService.hGet(contextKey, KEY_USER_POSITION_ID));
        String companyId = ObjectUtil.toStringIfNotNull(redisService.hGet(contextKey, KEY_USER_COMPANY));
        String companyName = ObjectUtil.toStringIfNotNull(redisService.hGet(contextKey, KEY_USER_COMPANY_NAME));
        String userCacheAuths = ObjectUtil.toStringIfNotNull(redisService.hGet(contextKey, KEY_USER_ROLE_ID));
        String userTag = ObjectUtil.toStringIfNotNull(redisService.hGet(contextKey, KEY_USER_TAG));
        addHeader(mutate, UserHeaderConstant.USER_GROUP, groupId);
        addHeader(mutate, UserHeaderConstant.USER_GROUP_NAME, groupName);
        addHeader(mutate, UserHeaderConstant.USER_POSITION, positionId);
        addHeader(mutate, UserHeaderConstant.USER_ROLES, userCacheAuths);
        addHeader(mutate, UserHeaderConstant.USER_COMPANY_ID, companyId);
        addHeader(mutate, UserHeaderConstant.USER_TAG, userTag);
        addHeader(mutate, UserHeaderConstant.USER_COMPANY_NAME, companyName);
    }

    protected boolean beDevToken(String token) {
        boolean beTestUser = !Checker.beNull(testUser) && token.equalsIgnoreCase(testUser.getUserName());
        return Strings.DEV.equals(profiles) && (Strings.TEST.equalsIgnoreCase(token) || beTestUser);
    }
    private boolean wrapIfDevTokenHeader(ServerHttpRequest request, String token) {

        if (beDevToken(token)) {
            ServerHttpRequest.Builder mutate = getRequestBuilder(request);
            addHeader(mutate, HEADER_DEV_TOKEN, HEADER_DEV_TOKEN);
            if (Checker.beNotNull(testUser)) {
                addHeader(mutate, UserHeaderConstant.USER_ID, testUser.getId());
                addHeader(mutate, UserHeaderConstant.USER_NAME, testUser.getUserName());
                addHeader(mutate, UserHeaderConstant.USER_CN_NAME, testUser.getUserCnName());
                addHeader(mutate, UserHeaderConstant.USER_EN_NAME, testUser.getUserEnName());
                addHeader(mutate, UserHeaderConstant.USER_GROUP, testUser.getGroupId());
                addHeader(mutate, UserHeaderConstant.USER_GROUP_NAME, testUser.getGroupName());
                addHeader(mutate, UserHeaderConstant.USER_POSITION, testUser.getPositionId());
                addHeader(mutate, UserHeaderConstant.USER_ROLES, testUser.getRoles());
                addHeader(mutate, UserHeaderConstant.USER_COMPANY_ID, testUser.getCompanyId());
                addHeader(mutate, UserHeaderConstant.USER_TAG, testUser.getUserTag());
                addHeader(mutate, UserHeaderConstant.USER_COMPANY_NAME, testUser.getCompanyName());

            } else {
                addHeader(mutate, UserHeaderConstant.USER_ID, "1410142488756248577");
                addHeader(mutate, UserHeaderConstant.USER_NAME, "test");
                addHeader(mutate, UserHeaderConstant.USER_CN_NAME, "测试员");
                addHeader(mutate, UserHeaderConstant.USER_EN_NAME, "tester");

            }
            return true;
        }
        return false;
    }


    @Override
    public int getOrder() {
        return ORDER_TWO;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = getRequest(exchange);
        String url = getUrl(request);

        ServerHttpRequest.Builder mutate = getRequestBuilder(request);;

        //白名单
        if (matchWhiteList(whites, url)) {
            return chain.filter(exchange);
        }

        //已授权的
        if (hasGatewayAuthed(request)) {
            return chain.filter(exchange);
        }

        //feign调用
        if (beFeignInvoke(request)) {
            return chain.filter(exchange);
        }

        ///登录或者登出操作
        if (beLoginRequest(url)) {
            return chain.filter(exchange);
        }

        String matchedCacheKey = findCacheKey(request);

        if (Checker.beNotEmpty(matchedCacheKey)) {
            String needRole = redisService.get(matchedCacheKey);

            //URL资源不需要鉴权
            if (needRole.contains(RESOURCE_NO_NEED_LOGIN)) {
                return chain.filter(exchange);
            }
        }

        //提取token
        String token = getToken(request);
        if (Checker.beEmpty(token)) {
            throw new InvalidTokenException();
        }
        final String copyToken = token;
        addHeader(mutate, UserHeaderConstant.USER_TOKEN, copyToken);

        token = token.substring(JWT_BEAR_TYPE.length()).trim();

        //方便调试,设置开发环境固定测试账号
        if (wrapIfDevTokenHeader(request, token)) {
            return chain.filter(exchange);
        }

        //校验TOKEN
        verifyToken(request, token);
        wrapHeader(request);

        return chain.filter(exchange);
    }


}
