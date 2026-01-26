/*
 * Copyright (c) 2020-2025, zening (316279828@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.baomibing.security.filter;

import com.alibaba.fastjson.JSONObject;
import com.baomibing.authority.exception.NoBindUserException;
import com.baomibing.security.exception.*;
import com.baomibing.tool.constant.Formats;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.constant.UserHeaderConstant;
import com.baomibing.tool.constant.WebConstant;
import com.baomibing.tool.user.HmacServerUser;
import com.baomibing.tool.user.HmacUser;
import com.baomibing.tool.user.UserKey;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ObjectUtil;
import com.google.common.collect.Lists;
import inet.ipaddr.IPAddressString;
import io.vavr.collection.Stream;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;


/**
 * hamc 认证权限
 *
 *
 **/
public class CommonHmacAuthenticationFilter extends BaseFilter {

    private static final List<String> whites = Lists.newArrayList(WebConstant.JWT_API_PREFIX, WebConstant.THIRD_API_PREFIX, WebConstant.TENANT_API_PREFIX);

    private boolean matchWhiteList(String url) {
        return whites.stream().anyMatch(w -> pathMatch.match(w, url));
    }

    @Override
    public void addWhites(Set<String> urls) {
        if (Checker.beNotEmpty(urls)) {
            whites.addAll(urls);
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String url = request.getRequestURI();
        if (matchWhiteList(url)) {
            filterChain.doFilter(request, response);
            return;
        }


        HmacUser user = buildUser(request);
        HmacServerUser serverUser = getByAppId(user.getAppId());
        validateUser(user, serverUser);
        buildHeader(request, serverUser, user);
        filterChain.doFilter(request, response);
    }

    private void buildHeader(HttpServletRequest request, HmacServerUser serverUser, HmacUser user) {
        addHeader(request, UserHeaderConstant.USER_ID, serverUser.getUserId());
        addHeader(request, UserHeaderConstant.USER_NAME, serverUser.getUserNo());
        addHeader(request, UserHeaderConstant.USER_EN_NAME, serverUser.getUserRealEnName());
        addHeader(request, UserHeaderConstant.USER_CN_NAME, serverUser.getUserRealCnName());
        addHeader(request, UserHeaderConstant.USER_ROLES, serverUser.getRoleIds());
        addHeader(request, UserHeaderConstant.USER_TENANT_ID, serverUser.getTenantId());
        addHeader(request, UserHeaderConstant.USER_TENANT_NAME, serverUser.getTenantName());
        addHeader(request, UserHeaderConstant.USER_FLAG, Strings.TENANT.equalsIgnoreCase(serverUser.getBindType()) ? Strings.TENANT : Strings.USER);

        addHeader(request, UserHeaderConstant.USER_GROUP, serverUser.getOrgId());
        addHeader(request, UserHeaderConstant.USER_GROUP_NAME, serverUser.getOrgName());
        addHeader(request, UserHeaderConstant.USER_POSITION, serverUser.getPositionId());
        addHeader(request, UserHeaderConstant.USER_ROLES, serverUser.getRoleIds());
        addHeader(request, UserHeaderConstant.USER_COMPANY_ID, serverUser.getGroupId());
        addHeader(request, UserHeaderConstant.USER_TAG, serverUser.getUserTag());
        addHeader(request, UserHeaderConstant.USER_COMPANY_NAME, serverUser.getGroupName());


        addHeader(request, UserHeaderConstant.USER_OUTER_SYSTEM, serverUser.getSystemName());
        addHeader(request, UserHeaderConstant.HMAC_USER_NAME, serverUser.getUserNo());
        addHeader(request, UserHeaderConstant.HMAC_USER_CN_NAME, serverUser.getUserRealCnName());
        addHeader(request, UserHeaderConstant.HMAC_USER_GROUP_NAME, serverUser.getOrgName());
        addHeader(request, UserHeaderConstant.HMAC_USER_GROUP, serverUser.getOrgId());
        addHeader(request, UserHeaderConstant.HMAC_USER_BUSINESS_ID, ObjectUtil.defaultIfNull(serverUser.getBusinessId(), serverUser.getTenantId()));
    }

    private boolean matchIps(String whilteIps, String ip) {
        IPAddressString ipString = new IPAddressString(ip);
        return Stream.of(whilteIps.split(",")).exists(ipSegment -> {
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
                .parse(serverUser.getExpireDate(), DateTimeFormat.forPattern(Formats.DEFAULT_DATE_TIME_FORMAT)).isBeforeNow()) {
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

        String userId = serverUser.getUserId();
        if (Checker.beEmpty(userId)) {
            throw new NoBindUserException();
        }

    }

    private HmacServerUser getByAppId(String appId) {
        String cacheUser = cacheService.get(UserKey.userHmacAppIdKey(appId));
        if (Checker.beEmpty(cacheUser)) {
            throw new InvalidAppIdException();
        }
        return JSONObject.parseObject(cacheUser, HmacServerUser.class);
    }

    private HmacUser buildUser(HttpServletRequest request) {
        String appId = request.getHeader(UserHeaderConstant.PARAM_HMAC_APP_ID);
        String timestamp = request.getHeader(UserHeaderConstant.PARAM_HMAC_TIMESTAMP);
        String digest = request.getHeader(UserHeaderConstant.PARAM_HMAC_DIGEST);
        String keyBuffer = appId + timestamp;
        String host = request.getRemoteHost();
        String url = request.getRequestURI();
        return new HmacUser(appId, timestamp, keyBuffer, digest, host, url);
    }
}
