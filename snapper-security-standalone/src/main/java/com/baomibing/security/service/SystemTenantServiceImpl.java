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
package com.baomibing.security.service;

import com.alibaba.fastjson.JSON;
import com.baomibing.authority.constant.SystemConst;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.exception.CreateRSAKeyException;
import com.baomibing.authority.jwt.TenantJwtTokenResponse;
import com.baomibing.authority.service.SysUserService;
import com.baomibing.authority.service.SystemTenantService;
import com.baomibing.cache.CacheService;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.security.jwt.TenantSecurityUser;
import com.baomibing.tool.constant.NumberConstant;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.constant.TenantRedisKeyConstant;
import com.baomibing.tool.constant.WebConstant;
import com.baomibing.tool.user.EmailServer;
import com.baomibing.tool.user.TenantUserKey;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ObjectUtil;
import com.baomibing.tool.util.SnowflakeIdWorker;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.baomibing.tool.constant.WebConstant.*;

/**
 * 系统服务 包含登录信息
 *
 * @author zening
 * @version 1.0.0
 **/
@Service @Slf4j
public class SystemTenantServiceImpl implements SystemTenantService {

    @Autowired private CacheService cacheService;
    @Autowired private SysUserService userService;
    @Autowired private AuthenticationManager authenticationManager;
    @Value("${spring.profiles.active}") protected String profiles;


    @Override
    public TenantJwtTokenResponse login(String userName, String password, String tag, String orgId) {

        if (Checker.beEmpty(tag)) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.SYSTEM_TAG_NOT_CLARITY);
        }
        userName = userName.trim();
        String cacheHashKey = (userName + Strings.HASH + tag + Strings.HASH + Strings.TENANT).toLowerCase();
//        String cacheHashKey = UserKey.userTagHashKey(userName, tag);
//        if (tag.contains(SystemTagEnum.business.name())) {
//            if (Checker.beEmpty(orgId)) {
//                throw new ServerRuntimeException(AuthorizationExceptionEnum.USER_GROUP_NOT_CLARITY);
//            }
//            //缓存登录用户选择的组织ID
//            cacheService.set(UserKey.userSelectGroupKey(userName, tag), orgId, 30);
//        }

        UsernamePasswordAuthenticationToken usernameAuthentication = new UsernamePasswordAuthenticationToken(cacheHashKey, password);
        Authentication authentication = this.authenticationManager.authenticate(usernameAuthentication);
        // 认证
        SecurityContextHolder.getContext().setAuthentication(authentication);
        TenantSecurityUser securityUser = (TenantSecurityUser) authentication.getPrincipal();

        try {
            // 生成RSA KEY
            KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
            keyGenerator.initialize(2048);
            KeyPair kp = keyGenerator.genKeyPair();
            RSAPublicKey publicKey = (RSAPublicKey) kp.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) kp.getPrivate();
            // 存储public key
            String secretKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            // 更新秘钥
            userService.updateUserSecret(securityUser.getUserId(), secretKey);
            // 封装claim
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .claim(JWT_USER_ID, securityUser.getUserId())
                    .claim(JWT_USER_NO, securityUser.getUsername())
                    .claim(JWT_TENANT_ID, securityUser.getTenantId())
                    .claim(JWT_SYSTEM_TAG, tag)
                    .claim(JWT_MULTI_LOGIN, securityUser.isBeMultiLogin())
                    .claim(JWT_EXPIRE_POLICY, securityUser.getExpirePolicy())
                    .expirationTime(new DateTime().plus(SystemConst.JWT_EXPIRE_IN_MILLISECONDS).toDate())
                    .jwtID(SnowflakeIdWorker.getId()).issuer("Snapper").issueTime(new Date()).build();
            // 用私钥加密
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claimsSet);
            JWSSigner signer = new RSASSASigner(privateKey);
            signedJWT.sign(signer);
            String accessToken = signedJWT.serialize();
            String refreshToken = RandomStringUtils.randomAlphabetic(128);

           //单机版不支持多用户登录
            if (Boolean.TRUE.equals(securityUser.isBeMultiLogin())) {
                if (cacheService.hasKey(TenantUserKey.userTokenKey(userName))) {
                    cacheService.lPush(TenantUserKey.userTokenKey(userName), accessToken);
                } else {
                    cacheService.lPush(TenantUserKey.userTokenKey(userName), accessToken, NumberConstant.ONE_DAY_SECONDS);
                }
            } else {
                cacheService.del(TenantUserKey.userTokenKey(userName));
            }

            TenantJwtTokenResponse jwtTokenResponse = new TenantJwtTokenResponse();
            jwtTokenResponse.setAccess_token(accessToken).setRefresh_token(refreshToken).setJti(claimsSet.getJWTID())
                    .setBe2Change(securityUser.isBeNeed2ChangeDepartment());
            List<String> authorizations = Lists.newArrayList();
            securityUser.getAuthorities().forEach(a -> authorizations.add(a.getAuthority()));
            EmailServer email = new EmailServer();
            email.setAddress(securityUser.getUserEmail()).setPasswd(securityUser.getUserEmailPwd()).setHost(securityUser.getUserEmailHost()).setProtocol(securityUser.getUserEmailProtocol());
            // 缓存信息 pk 及 权限列表
            Map<String, String> cacheMap = Maps.newHashMap();
            cacheMap.put(TenantUserKey.userJwtRsaKey(tag), secretKey);
            cacheMap.put(TenantRedisKeyConstant.KEY_USER_EMAIL, JSON.toJSONString(email));
            cacheMap.put(TenantRedisKeyConstant.KEY_USER_ID, securityUser.getUserId());
            cacheMap.put(TenantRedisKeyConstant.KEY_USER_NO, securityUser.getUsername());
            cacheMap.put(TenantRedisKeyConstant.KEY_USER_COMPANY, securityUser.getCompanyId());
            cacheMap.put(TenantRedisKeyConstant.KEY_USER_COMPANY_NAME, securityUser.getCompanyName());
            cacheMap.put(TenantRedisKeyConstant.KEY_USER_GROUP_ID, ObjectUtil.defaultIfNull(securityUser.getGroupId(), ""));//防止另一个用户登录时用当前用户的信息
            cacheMap.put(TenantRedisKeyConstant.KEY_USER_POSITION_ID, ObjectUtil.defaultIfNull(securityUser.getPositionId(), ""));
            cacheMap.put(TenantRedisKeyConstant.KEY_USER_REAL_CN_NAME, ObjectUtil.defaultIfNull(securityUser.getUserCnName(), ""));
            cacheMap.put(TenantRedisKeyConstant.KEY_USER_REAL_EN_NAME, ObjectUtil.defaultIfNull(securityUser.getUserEnName(), ""));
            cacheMap.put(TenantRedisKeyConstant.KEY_USER_DEPARTMENT, ObjectUtil.defaultIfNull(securityUser.getGroupName(), ""));
            cacheMap.put(TenantRedisKeyConstant.KEY_USER_RANK, ObjectUtil.defaultIfNull(securityUser.getRank(), ""));
            cacheMap.put(TenantRedisKeyConstant.KEY_USER_STATE, ObjectUtil.defaultIfNull(securityUser.getState(), ""));
            cacheMap.put(TenantRedisKeyConstant.KEY_USER_SCORE, ObjectUtil.defaultIfNull(ObjectUtil.toStringIfNotNull(securityUser.getScore()), ""));
            cacheMap.put(TenantRedisKeyConstant.KEY_USER_ROLE_ID, Joiner.on(Strings.COMMA).join(authorizations));
            cacheMap.put(TenantRedisKeyConstant.KEY_USER_TAG, ObjectUtil.defaultIfNull(securityUser.getUserTag(), ""));
            //cacheMap.put(RedisKeyConstant.KEY_USER_EXPIRE_TIME, new DateTime(expireTime).toString(Formats.DEFAULT_DATE_TIME_FORMAT));
            cacheService.hSetAll(TenantUserKey.userContextKey(userName, tag), cacheMap, WebConstant.JWT_EXPIRE_IN_MILLISECONDS / 1000);
            if (Checker.beEmpty(securityUser.getGroupId())) {
                jwtTokenResponse.setBe2Change(Boolean.TRUE);
            }

            return jwtTokenResponse;
        } catch (NoSuchAlgorithmException | JOSEException e) {
            log.error(e.getMessage());
            throw new CreateRSAKeyException();
        }
    }

    @Override
    public void logout() {
        String userName = ObjectUtil.toStringIfNotNull(SecurityContextHolder.getContext().getAuthentication().getDetails());
        cacheService.del(MessageFormat.format(TenantRedisKeyConstant.KEY_USER_CONTEXT, userName));
        SecurityContextHolder.clearContext();
    }
}
