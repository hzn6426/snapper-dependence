/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.baomibing.gateway.exception.*;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.constant.UserHeaderConstant;
import com.baomibing.tool.user.HmacServerUser;
import com.baomibing.tool.user.HmacUser;
import com.baomibing.tool.user.UserKey;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ObjectUtil;
import com.google.common.collect.Sets;
import inet.ipaddr.IPAddressString;
import io.vavr.collection.Stream;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.baomibing.gateway.constant.GateWayConstant.ORDER_TWO;
import static com.baomibing.tool.constant.Formats.DEFAULT_DATE_TIME_FORMAT;
import static com.baomibing.tool.constant.UserHeaderConstant.*;
import static com.baomibing.tool.constant.WebConstant.JWT_API_PREFIX;
import static com.baomibing.tool.constant.WebConstant.THIRD_API_PREFIX;

/**
 * HmacFilter
 *
 * @author zening 2022/5/9 11:15
 * @version 1.0.0
 */
public class HmacFilter extends BaseFilter implements GlobalFilter, Ordered {


    private static final CopyOnWriteArraySet<String> whites = new CopyOnWriteArraySet<>(Sets.newHashSet(
            JWT_API_PREFIX,
            THIRD_API_PREFIX
    ));

    @Override
    public void addWhites(Set<String> urls) {
        if (Checker.beNotEmpty(urls)) {
            whites.addAll(urls);
        }
    }

    private boolean matchWhiteList(String url) {
        return whites.stream().anyMatch(w -> pathMatch.match(w, url));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = getRequest(exchange);

        if (matchWhiteList(getUrl(request))) {
            return chain.filter(exchange);
        }

        if (hasGatewayAuthed(request)) {
            return chain.filter(exchange);
        }

        if (beFeignInvoke(request)) {
            return chain.filter(exchange);
        }

        HmacUser user = buildUser(request);
        HmacServerUser serverUser = getByAppId(user.getAppId());
        validateUser(user, serverUser);
        buildHeader(request, serverUser, user);
        return chain.filter(exchange);
    }

    private void buildHeader(ServerHttpRequest request, HmacServerUser serverUser, HmacUser user) {
        ServerHttpRequest.Builder mutate = request.mutate();
        addHeader(mutate, USER_ID, serverUser.getUserId());
        addHeader(mutate, USER_NAME, serverUser.getUserNo());
        addHeader(mutate, USER_EN_NAME, serverUser.getUserRealEnName());
        addHeader(mutate, USER_CN_NAME, serverUser.getUserRealCnName());
        addHeader(mutate, USER_ROLES, serverUser.getRoleIds());
        addHeader(mutate, UserHeaderConstant.USER_TENANT_ID, serverUser.getTenantId());
        addHeader(mutate, UserHeaderConstant.USER_FLAG, Strings.TENANT.equals(serverUser.getBindType()) ? Strings.TENANT : Strings.USER);

        addHeader(mutate, UserHeaderConstant.USER_GROUP, serverUser.getOrgId());
        addHeader(mutate, UserHeaderConstant.USER_GROUP_NAME, serverUser.getOrgName());
        addHeader(mutate, UserHeaderConstant.USER_POSITION, serverUser.getPositionId());
        addHeader(mutate, UserHeaderConstant.USER_ROLES, serverUser.getRoleIds());
        addHeader(mutate, UserHeaderConstant.USER_COMPANY_ID, serverUser.getGroupId());
        addHeader(mutate, UserHeaderConstant.USER_TAG, serverUser.getUserTag());
        addHeader(mutate, UserHeaderConstant.USER_COMPANY_NAME, serverUser.getGroupName());

        addHeader(mutate, USER_OUTER_SYSTEM, serverUser.getSystemName());
        addHeader(mutate, HMAC_USER_NAME, serverUser.getUserNo());
        addHeader(mutate, HMAC_USER_CN_NAME, serverUser.getUserRealCnName());
        addHeader(mutate, HMAC_USER_GROUP_NAME, serverUser.getOrgName());
        addHeader(mutate, HMAC_USER_GROUP, serverUser.getOrgId());
        addHeader(mutate, UserHeaderConstant.HMAC_USER_BUSINESS_ID, ObjectUtil.defaultIfNull(serverUser.getBusinessId(), serverUser.getTenantId()));
    }

    private boolean matchIps(String whilteIps, String ip) {
        IPAddressString ipString = new IPAddressString(ip);
        return Stream.of(whilteIps.split(Strings.COMMA)).exists(ipSegment -> {
            IPAddressString segmentString = new IPAddressString(ipSegment);
            return segmentString.contains(ipString);
        });
    }

    private void validateUser(HmacUser user, HmacServerUser serverUser) {
        String digest = user.getDigest();

        if (Checker.beNull(serverUser)) {
            throw new InvalidAppIdException();
        }

        if (Checker.beNotEmpty(serverUser.getExpireDate()) && DateTime
                .parse(serverUser.getExpireDate(), DateTimeFormat.forPattern(DEFAULT_DATE_TIME_FORMAT)).isBeforeNow()) {
            throw new AppIdExpireException();
        }
        if (Checker.beNotEmpty(serverUser.getWhiteIps())
                && !matchIps(serverUser.getWhiteIps(), user.getHost())) {
            throw new IPNotAllowedException();
        }
        String appKey = serverUser.getAppKey();
        if (Checker.beEmpty(appKey)) {
            throw new InvalidAppKeyException();
        }
        String serverDigest = new HmacUtils(HmacAlgorithms.HMAC_MD5, appKey).hmacHex(user.getBaseString());
        if (Checker.beEmpty(serverDigest) || Checker.beNotEqual(serverDigest, digest)) {
            throw new IncorrectCredentialsException();
        }

//		Long currentTimeMillis = System.currentTimeMillis();
//		Long tokenTimestamp = Long.valueOf(user.getTimestamp());
//		// 数字签名超时失效 - 设置防止回放攻击
//		if ((currentTimeMillis - tokenTimestamp) / 1000 > DIGEST_EXPIRE_SECONDS) {
//			throw new ExpiredCredentialsException();
//		}

        String userId = serverUser.getUserId();
        if (Checker.beEmpty(userId)) {
            throw new NoBindUserException();
        }

    }

    private HmacServerUser getByAppId(String appId) {
        String cacheUser = redisService.get(UserKey.userHmacAppIdKey(appId));
        if (Checker.beEmpty(cacheUser)) {
            throw new InvalidAppIdException();
        }
        return JSONObject.parseObject(cacheUser, HmacServerUser.class);
    }

    private HmacUser buildUser(ServerHttpRequest request) {
        String appId = getHeader(request, PARAM_HMAC_APP_ID);
        String timestamp = getHeader(request, PARAM_HMAC_TIMESTAMP);
        String digest = getHeader(request, PARAM_HMAC_DIGEST);
        String keyBuffer = appId + timestamp;
        String host = getIP(request);
        String url = getUrl(request);
        return new HmacUser(appId, timestamp, keyBuffer, digest, host, url);
    }

    @Override
    public int getOrder() {
        return ORDER_TWO;
    }
}
