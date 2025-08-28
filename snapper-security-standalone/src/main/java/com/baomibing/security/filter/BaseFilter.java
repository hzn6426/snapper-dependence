package com.baomibing.security.filter;

import cn.hutool.core.util.URLUtil;
import com.baomibing.authority.exception.InvalidTokenException;
import com.baomibing.authority.exception.TokenBeKickedException;
import com.baomibing.authority.exception.TokenTimeOutException;
import com.baomibing.cache.CacheService;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.tool.constant.*;
import com.baomibing.tool.user.TenantUserKey;
import com.baomibing.tool.user.UserKey;
import com.baomibing.tool.util.CharacterUtil;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ObjectUtil;
import com.baomibing.web.common.MultiHeaderHttpServletRequest;
import com.google.common.base.Splitter;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.interfaces.RSAPublicKey;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.baomibing.authority.jwt.JwtUtil.decryptBase64;
import static com.baomibing.tool.constant.PermConstant.RESOURCE_NO_NEED_ROLE;
import static com.baomibing.tool.constant.RedisKeyConstant.CACHE_API_PREFIX;
import static com.baomibing.tool.constant.RedisKeyConstant.KEY_USER_TOKEN;
import static com.baomibing.tool.constant.WebConstant.*;

/**
 * BaseFilter
 *
 * @author zening 2023/9/5 09:38
 * @version 1.0.0
 **/
@Slf4j
public abstract class BaseFilter extends OncePerRequestFilter {

    @Value("${spring.profiles.active}") protected String profiles;
    @Autowired protected CacheService cacheService;
    protected static final AntPathMatcher pathMatch = new AntPathMatcher();
    protected TestUser testUser;

    protected final static String HEADER_DEV_TOKEN = "HEADER_DEV_TOKEN";

    protected abstract void addWhites(Set<String> urls);

    protected boolean beDevToken(String token) {
        boolean beTestUser = !Checker.beNull(testUser) && token.equalsIgnoreCase(testUser.getUserName());
        return Strings.DEV.equals(profiles) && (Strings.TEST.equalsIgnoreCase(token) || beTestUser);
    }

    protected boolean beDevUser(String userName) {
        return Strings.DEV.equals(profiles) && Strings.TEST.equalsIgnoreCase(userName);
    }

    protected boolean beLoginOrOutRequest(String url) {
        return pathMatch.match(WebConstant.API_TOKEN_URL, url);
    }

    protected boolean beUserValidateForDepartments(String url) {
        return pathMatch.match(WebConstant.API_VALIDATE_USER_FOR_DEPARTMENTS, url);
    }

    protected boolean beSocketProtocol(String protocol) {
        final String ws = "ws";
        final String wss = "wss";
        if (Checker.beEmpty(protocol)) {
            return false;
        }
        if (protocol.toLowerCase().contains(ws) || protocol.toLowerCase().contains(wss)) {
            return true;
        }
        return false;
    }

    protected boolean beLoginRequest(String url) {
        return pathMatch.match(WebConstant.API_LOGIN_URL, url);
    }

    protected boolean beTenantLoginRequest(String url) {
        return pathMatch.match(WebConstant.TENANT_API_TOKEN_LOGIN_URL, url);
    }

    protected boolean beTenantLoginOrOutRequest(String url) {
        return pathMatch.match(WebConstant.TENANT_API_TOKEN_URL, url);
    }

    protected boolean beTenantUserValidateForDepartments(String url) {
        return pathMatch.match(WebConstant.TENANT_API_VALIDATE_USER_FOR_DEPARTMENTS, url);
    }


    protected void addHeader(HttpServletRequest request, String name, Object value) {
        if (Checker.beNull(value) || Checker.beBlank(value.toString())) {
            return;
        }
        if (!(request instanceof MultiHeaderHttpServletRequest)) {
            return;
        }
        MultiHeaderHttpServletRequest mrequest = (MultiHeaderHttpServletRequest) request;
        String valueStr = value.toString();
        String valueEncode = URLUtil.encode(valueStr);
        mrequest.putHeader(name, valueEncode);
    }

    protected String getHeader(HttpServletRequest request, String name) {
        if (request instanceof MultiHeaderHttpServletRequest) {
            MultiHeaderHttpServletRequest mrequest = (MultiHeaderHttpServletRequest) request;
            return mrequest.getHeader(name);
        }
        return request.getHeader(name);
    }
    protected String getToken(HttpServletRequest request) {
        return request.getHeader(HttpHeaders.AUTHORIZATION);
    }

    protected void verifyTenantToken(HttpServletRequest request, String token) {
        try {
            //根据token获取claim对应的userNo,获取缓存公钥，验证token有效性
            SignedJWT signedJWT = SignedJWT.parse(token);
            Object ouserNo = signedJWT.getJWTClaimsSet().getClaim(JWT_USER_NO);
            Object otag = signedJWT.getJWTClaimsSet().getClaim(JWT_SYSTEM_TAG);
            if (Checker.beNull(ouserNo)) {
                throw new TokenTimeOutException();
            }
            String userNo = ouserNo.toString();
            String tag = ObjectUtil.toStringIfNotNull(otag);
            String tenantId = signedJWT.getJWTClaimsSet().getClaim(WebConstant.JWT_TENANT_ID).toString();
            String contextKey = TenantUserKey.userContextKey(userNo, tag);

            String pk = ObjectUtil.toStringIfNotNull(
                    cacheService.hGet(contextKey,TenantUserKey.userJwtRsaKey(tag))
            );
            if (Checker.beEmpty(pk)) {
                throw new InvalidTokenException();
            }
            RSAPublicKey k = decryptBase64(pk);
            JWSVerifier verifier = new RSASSAVerifier(k);
            boolean beValid = signedJWT.verify(verifier);

            if (!beValid) {
                throw new TokenBeKickedException();
            }
//			Date expireTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            boolean beNotExpire = cacheService.hasKey(contextKey);
            //Date expireTime = DateUtil.toDate(expires, Formats.DEFAULT_DATE_TIME_FORMAT);
            if (!beNotExpire) {
                throw new TokenTimeOutException();
            }
            //校验成功后，获取封装的jwt信息
            String userId = ObjectUtil.toStringIfNotNull(signedJWT.getJWTClaimsSet().getClaim(JWT_USER_ID));
            String userCnName = ObjectUtil.toStringIfNotNull(cacheService.hGet(contextKey, TenantRedisKeyConstant.KEY_USER_REAL_CN_NAME));
            String userEnName = ObjectUtil.toStringIfNotNull(cacheService.hGet(contextKey, TenantRedisKeyConstant.KEY_USER_REAL_EN_NAME));
            String rank = ObjectUtil.toStringIfNotNull(cacheService.hGet(contextKey, TenantRedisKeyConstant.KEY_USER_RANK));
            String score = ObjectUtil.toStringIfNotNull(cacheService.hGet(contextKey, TenantRedisKeyConstant.KEY_USER_SCORE));
            String state = ObjectUtil.toStringIfNotNull(cacheService.hGet(contextKey, TenantRedisKeyConstant.KEY_USER_STATE));
            addHeader(request, UserHeaderConstant.USER_ID, userId);
            addHeader(request, UserHeaderConstant.USER_NAME, userNo);
            addHeader(request, UserHeaderConstant.USER_CN_NAME, userCnName);
            addHeader(request, UserHeaderConstant.USER_EN_NAME, userEnName);
            addHeader(request, UserHeaderConstant.USER_SYSTEM_TAG, otag);
            addHeader(request, UserHeaderConstant.USER_TENANT_ID, tenantId);
            addHeader(request, UserHeaderConstant.USER_FLAG, Strings.TENANT);
            addHeader(request, UserHeaderConstant.USER_TENANT_RANK, rank);
            addHeader(request, UserHeaderConstant.USER_TENANT_SCORE, score);
            addHeader(request, UserHeaderConstant.USER_TENANT_STATE, state);
        } catch (ParseException | JOSEException e) {
            log.error(e.getMessage());
            throw new InvalidTokenException();
        }
    }

    protected void verifyToken(HttpServletRequest request, String token) {
        try {
            //根据token获取claim对应的userNo,获取缓存公钥，验证token有效性
            SignedJWT signedJWT = SignedJWT.parse(token);
            Object ouserNo = signedJWT.getJWTClaimsSet().getClaim(JWT_USER_NO);
            Object otag = signedJWT.getJWTClaimsSet().getClaim(JWT_SYSTEM_TAG);
            if (Checker.beNull(ouserNo)) {
                throw new TokenTimeOutException();
            }
            String userNo = ouserNo.toString();
            String tag = ObjectUtil.toStringIfNotNull(otag);
            String contextKey = UserKey.userContextKey(userNo, tag);

            Boolean beMultiLogin = (Boolean) signedJWT.getJWTClaimsSet().getClaim(JWT_MULTI_LOGIN);
            boolean beValid = false;
            if (Boolean.TRUE.equals(beMultiLogin)) {
                String key = MessageFormat.format(KEY_USER_TOKEN, userNo);
                beValid = cacheService.lContains(key, token);
            } else {
                String pk = ObjectUtil.toStringIfNotNull(
                        cacheService.hGet(contextKey, UserKey.userJwtRsaKey(tag))
                );
                if (Checker.beEmpty(pk)) {
                    throw new InvalidTokenException();
                }
                RSAPublicKey k = decryptBase64(pk);
                JWSVerifier verifier = new RSASSAVerifier(k);
                beValid = signedJWT.verify(verifier);

            }

            if (!beValid) {
                if (Boolean.TRUE.equals(beMultiLogin)) {
                    throw new InvalidTokenException();
                }
                throw new TokenBeKickedException();
            }
//			Date expireTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            boolean beNotExpire = cacheService.hasKey(contextKey);
            //Date expireTime = DateUtil.toDate(expires, Formats.DEFAULT_DATE_TIME_FORMAT);
            if (!beNotExpire) {
                throw new TokenTimeOutException();
            }
            //校验成功后，获取封装的jwt信息
            String userId = ObjectUtil.toStringIfNotNull(signedJWT.getJWTClaimsSet().getClaim(JWT_USER_ID));
            String userCnName = ObjectUtil.toStringIfNotNull(cacheService.hGet(contextKey, RedisKeyConstant.KEY_USER_REAL_CN_NAME));
            String userEnName = ObjectUtil.toStringIfNotNull(cacheService.hGet(contextKey, RedisKeyConstant.KEY_USER_REAL_EN_NAME));
            addHeader(request, UserHeaderConstant.USER_ID, userId);
            addHeader(request, UserHeaderConstant.USER_NAME, userNo);
            addHeader(request, UserHeaderConstant.USER_CN_NAME, userCnName);
            addHeader(request, UserHeaderConstant.USER_EN_NAME, userEnName);
            addHeader(request, UserHeaderConstant.USER_SYSTEM_TAG, otag);
            String expirePolicy = ObjectUtil.toStringIfNotNull(signedJWT.getJWTClaimsSet().getClaim(JWT_EXPIRE_POLICY));
            if (Checker.beNotEmpty(expirePolicy) && "LAST_ACTIVE".equals(expirePolicy)) {
                cacheService.expire(contextKey, JWT_EXPIRE_IN_MILLISECONDS/1000);
            }
        } catch (ParseException | JOSEException e) {
            log.error(e.getMessage());
            throw new InvalidTokenException();
        }
    }

    protected String handleGetUri(String uri) {
        String url = uri;

        int index = url.lastIndexOf("/");
        String charsAfterSlash = url.substring(index + 1);
        if (Checker.beNotEmpty(charsAfterSlash) && charsAfterSlash.equals(CharacterUtil.justLeftNumbers(charsAfterSlash))) {
            url = url.replace(charsAfterSlash, Strings.BRACE);
        }
        return url;
    }


    protected String findCacheKey(HttpServletRequest request) {
        String url = request.getRequestURI();
        url = url.replaceAll("/wapi/","/api/");
        String method = request.getMethod();
        String authorizationCacheKey = UserKey.buttonPermKey(method,url) + "*";
        Set<String> keys = ObjectUtil.nullIfEmptySet(cacheService.keys(authorizationCacheKey));
        String matchedCacheKey = "";
        for (String k : keys) {
            List<String> splits = Splitter.on(Strings.DOUBLE_AT).splitToList(k);
            boolean match = new AntPathMatcher().match(splits.get(1), url) && Checker.beNotNull(method)
                    && method.matches(ObjectUtil.toStringIfNotNull(splits.get(0)).replace(CACHE_API_PREFIX, ""));
            if (match) {
                matchedCacheKey = k;
                break;
            }
        }
        if (Checker.beEmpty(matchedCacheKey)) {
            url = handleGetUri(url);
            //注意匹配/xxx/** 和 /xxx/{123} 模式
//            int index = url.lastIndexOf("/");
//            String charsAfterSlash = url.substring(index + 1);
//            if (Checker.beNotEmpty(charsAfterSlash) && charsAfterSlash.equals(CharacterUtil.justLeftNumbers(charsAfterSlash))) {
//                url = url.replace(charsAfterSlash, Strings.BRACE);
                String refKey = CACHE_API_PREFIX + method + Strings.DOUBLE_AT + url;
                if (Boolean.TRUE.equals(cacheService.hasKey(refKey))) {
                    matchedCacheKey = refKey;
                }
//            }
        }
        return matchedCacheKey;
    }
    protected void verifyAuthorization(HttpServletRequest request, String userCacheAuths) {
        String matchedCacheKey = findCacheKey(request);
        //没有匹配上，说明当前的请求没有权限
        if (Checker.beEmpty(matchedCacheKey)) {
            throw new ServerRuntimeException(ExceptionEnum.NO_PRIVILEGE_EXCEPTION);
        }

        String needRole = cacheService.get(matchedCacheKey);

        //URL资源不需要权限
        if (needRole.contains(RESOURCE_NO_NEED_ROLE)) {
            return;
        }
        //表明此API没有分配给任何角色
        if (Checker.beBlank(needRole) && Boolean.TRUE.equals(cacheService.hasKey(matchedCacheKey))) {
            throw new ServerRuntimeException(ExceptionEnum.NO_PRIVILEGE_EXCEPTION);
        }
        Set<String> needRoles = new HashSet<>(Splitter.on(Strings.COMMA).splitToList(needRole));
        Set<String> userRoles = new HashSet<>(Splitter.on(Strings.COMMA).splitToList(userCacheAuths));
        boolean match = needRoles.stream().anyMatch(userRoles::contains);
        if (!match) {
            throw new ServerRuntimeException(ExceptionEnum.NO_PRIVILEGE_EXCEPTION);
        }
    }

    protected String getIP(HttpServletRequest request){

        // 未知IP
        final String UNKNOWN = "unknown";
        // 本地 IP
        final String LOCALHOST_IP_Virtual = "0:0:0:0:0:0:0:1";
        final String LOCALHOST_IP = "127.0.0.1";
        // 根据 HttpHeaders 获取 请求 IP地址
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("x-forwarded-for");
            if (ip != null && !ip.isEmpty() && !UNKNOWN.equalsIgnoreCase(ip)) {
                // 多次反向代理后会有多个ip值，第一个ip才是真实ip
                if (ip.contains(Strings.COMMA)) {
                    ip = ip.split(Strings.COMMA)[0];
                }
            }
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        //兼容k8s集群获取ip
        if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteHost();
            if (LOCALHOST_IP_Virtual.equalsIgnoreCase(ip) || LOCALHOST_IP.equalsIgnoreCase(ip)) {
                //根据网卡取本机配置的IP
                InetAddress iNet = null;
                try {
                    iNet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    log.error("getClientIp error: ", e);
                }
                if (iNet != null) {
                    ip = iNet.getHostAddress();
                }
            }
        }
        return ip;
    }

}


