/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.security.service;

import com.alibaba.fastjson.JSON;
import com.baomibing.authority.constant.enums.SystemTagEnum;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.exception.CreateRSAKeyException;
import com.baomibing.authority.jwt.JwtTokenResponse;
import com.baomibing.authority.service.SysUserService;
import com.baomibing.authority.service.SystemService;
import com.baomibing.cache.redis.RedisService;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.security.jwt.SecurityUser;
import com.baomibing.tool.constant.NumberConstant;
import com.baomibing.tool.constant.RedisKeyConstant;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.constant.WebConstant;
import com.baomibing.tool.user.EmailServer;
import com.baomibing.tool.user.UserKey;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
 * @author zening 2023/8/2 17:30
 * @version 1.0.0
 **/
@Slf4j
public class SystemServiceImpl implements SystemService {

    @Autowired private RedisService redisService;
    @Autowired private SysUserService userService;
    @Autowired private AuthenticationManager authenticationManager;
    @Value("${spring.profiles.active}") protected String profiles;
    private final long SIX_MONTH = 60 * 60 *24* 30 * 6;
    private final String TAG_EXAMPLE = "example";

    @Override
    public JwtTokenResponse login(String userName, String password, String tag, String orgId) {

        if (Checker.beEmpty(tag)) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.SYSTEM_TAG_NOT_CLARITY);
        }
        userName = userName.trim();
        String cacheHashKey = UserKey.userTagHashKey(userName, tag);
        if (tag.contains(SystemTagEnum.business.name())) {
            if (Checker.beEmpty(orgId)) {
                throw new ServerRuntimeException(AuthorizationExceptionEnum.USER_GROUP_NOT_CLARITY);
            }
            //缓存登录用户选择的组织ID
            redisService.set(UserKey.userSelectGroupKey(userName, tag), orgId, 30);
        }

        UsernamePasswordAuthenticationToken usernameAuthentication = new UsernamePasswordAuthenticationToken(cacheHashKey, password);
        Authentication authentication = this.authenticationManager.authenticate(usernameAuthentication);
        // 认证
        SecurityContextHolder.getContext().setAuthentication(authentication);
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        // 如果是测试用户
        if (Strings.TEST.equals(userName) && Strings.DEV.equals(profiles)) {
            return new JwtTokenResponse().setAccess_token(Strings.TEST).setRefresh_token("");
        }
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
                    .claim(JWT_SYSTEM_TAG, tag)
                    .claim(JWT_MULTI_LOGIN, securityUser.isBeMultiLogin())
                    .claim(JWT_EXPIRE_POLICY, securityUser.getExpirePolicy())
                    .jwtID(SnowflakeIdWorker.getId()).issuer("Snapper").issueTime(new Date()).build();
            // 用私钥加密
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claimsSet);
            JWSSigner signer = new RSASSASigner(privateKey);
            signedJWT.sign(signer);
            String accessToken = signedJWT.serialize();
            String refreshToken = RandomStringUtils.randomAlphabetic(128);

            //是否支持多用户登录
            if (securityUser.isBeMultiLogin()) {
                if (redisService.hasKey(UserKey.userTokenKey(userName))) {
                    redisService.lPush(UserKey.userTokenKey(userName), accessToken);
                } else {
                    redisService.lPush(UserKey.userTokenKey(userName), accessToken, NumberConstant.ONE_DAY_SECONDS);
                }
            } else {
                redisService.del(UserKey.userTokenKey(userName));
            }

            JwtTokenResponse jwtTokenResponse = new JwtTokenResponse();
            jwtTokenResponse.setAccess_token(accessToken).setRefresh_token(refreshToken).setJti(claimsSet.getJWTID())
                    .setBe2Change(securityUser.isBeNeed2ChangeDepartment());
            List<String> authorizations = Lists.newArrayList();
            securityUser.getAuthorities().forEach(a -> authorizations.add(a.getAuthority()));
            EmailServer email = new EmailServer();
            email.setAddress(securityUser.getUserEmail()).setPasswd(securityUser.getUserEmailPwd()).setHost(securityUser.getUserEmailHost()).setProtocol(securityUser.getUserEmailProtocol());
            // 缓存信息 pk 及 权限列表
            Map<String, String> cacheMap = Maps.newHashMap();
            cacheMap.put(UserKey.userJwtRsaKey(tag), secretKey);
            cacheMap.put(RedisKeyConstant.KEY_USER_EMAIL, JSON.toJSONString(email));
            cacheMap.put(RedisKeyConstant.KEY_USER_ID, securityUser.getUserId());
            cacheMap.put(RedisKeyConstant.KEY_USER_NO, securityUser.getUsername());
            cacheMap.put(RedisKeyConstant.KEY_USER_COMPANY, securityUser.getCompanyId());
            cacheMap.put(RedisKeyConstant.KEY_USER_COMPANY_NAME, securityUser.getCompanyName());
            cacheMap.put(RedisKeyConstant.KEY_USER_GROUP_ID, ObjectUtil.defaultIfNull(securityUser.getGroupId(), ""));//防止另一个用户登录时用当前用户的信息
            cacheMap.put(RedisKeyConstant.KEY_USER_POSITION_ID, ObjectUtil.defaultIfNull(securityUser.getPositionId(), ""));
            cacheMap.put(RedisKeyConstant.KEY_USER_REAL_CN_NAME, ObjectUtil.defaultIfNull(securityUser.getUserCnName(), ""));
            cacheMap.put(RedisKeyConstant.KEY_USER_REAL_EN_NAME, ObjectUtil.defaultIfNull(securityUser.getUserEnName(), ""));
            cacheMap.put(RedisKeyConstant.KEY_USER_DEPARTMENT, ObjectUtil.defaultIfNull(securityUser.getGroupName(), ""));
            cacheMap.put(RedisKeyConstant.KEY_USER_ROLE_ID, Joiner.on(Strings.COMMA).join(authorizations));
            cacheMap.put(RedisKeyConstant.KEY_USER_TAG, ObjectUtil.defaultIfNull(securityUser.getUserTag(), ""));
            //cacheMap.put(RedisKeyConstant.KEY_USER_EXPIRE_TIME, new DateTime(expireTime).toString(Formats.DEFAULT_DATE_TIME_FORMAT));
            redisService.hSetAll(UserKey.userContextKey(userName, tag), cacheMap, TAG_EXAMPLE.equals(tag) ? SIX_MONTH :WebConstant.JWT_EXPIRE_IN_MILLISECONDS / 1000);
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
        redisService.del(MessageFormat.format(RedisKeyConstant.KEY_USER_CONTEXT, userName));
        SecurityContextHolder.clearContext();
    }
}
