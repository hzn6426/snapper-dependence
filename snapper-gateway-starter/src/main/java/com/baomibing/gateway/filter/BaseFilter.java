/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.gateway.filter;

import cn.hutool.core.util.URLUtil;
import com.baomibing.cache.CacheService;
import com.baomibing.gateway.common.TestUser;
import com.baomibing.gateway.exception.*;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.constant.TenantRedisKeyConstant;
import com.baomibing.tool.constant.UserHeaderConstant;
import com.baomibing.tool.constant.WebConstant;
import com.baomibing.tool.user.TenantUserKey;
import com.baomibing.tool.user.UserKey;
import com.baomibing.tool.util.AuthUtil;
import com.baomibing.tool.util.CharacterUtil;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ObjectUtil;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.baomibing.tool.constant.PermConstant.RESOURCE_NO_NEED_ROLE;
import static com.baomibing.tool.constant.RedisKeyConstant.*;
import static com.baomibing.tool.constant.WebConstant.*;

@Slf4j
public abstract class BaseFilter {
	
	@Value("${spring.profiles.active}") protected String profiles;
	
	@Autowired protected CacheService redisService;

	protected final static String HEADER_DEV_TOKEN = "HEADER_DEV_TOKEN";

	protected TestUser testUser;
	public abstract void addWhites(Set<String> urls);
	
	 protected static final AntPathMatcher pathMatch = new AntPathMatcher();

	 protected ServerHttpRequest.Builder getRequestBuilder(ServerHttpRequest request) {
		 return request.mutate();
	 }
	
//	protected boolean beDevToken(String token) {
//		return Strings.DEV.equals(profiles) && Strings.TEST.equalsIgnoreCase(token);
//	}
//
//	protected boolean beDevUser(String userName) {
//		return Strings.DEV.equals(profiles) && Strings.TEST.equalsIgnoreCase(userName);
//	}
	

	protected boolean matchWhiteList(Set<String> whites, String url) {
		return whites.stream().anyMatch(w -> pathMatch.match(w, url));
	}

	protected boolean hasGatewayAuthed(ServerHttpRequest request) {
		String tag = request.getHeaders().getFirst(UserHeaderConstant.GATE_WAY_REDIRECT_TAG);
		String userId = request.getHeaders().getFirst(UserHeaderConstant.USER_ID);
		return Checker.beNotEmpty(tag) && AuthUtil.verifyTag(userId, tag);
	}


	protected boolean beLoginOrOutRequest(String url) {
		return pathMatch.match(API_TOKEN_URL, url) || pathMatch.match(API_TTOKEN_URL, url);
	}

	protected boolean beTenantLoginOrOutRequest(String url) {
		return pathMatch.match(TENANT_API_TOKEN_URL, url) || pathMatch.match(TENANT_API_TTOKEN_URL, url);
	}

	protected boolean beLoginRequest(String url) {
		return pathMatch.match(API_LOGIN_URL, url);
	}

	protected boolean beTenantLoginRequest(String url) {
		return pathMatch.match(API_LOGIN_URL, url);
	}
	
	protected void addHeader(ServerHttpRequest.Builder mutate, String name, Object value) {
        if (Checker.beNull(value) || Checker.beBlank(value.toString())) {
            return;
        }
        String valueStr = value.toString();
        String valueEncode = URLUtil.encode(valueStr);
        mutate.header(name, valueEncode);
    }

	protected boolean beFeignInvoke(ServerHttpRequest request) {
		String feignTag = request.getHeaders().getFirst(UserHeaderConstant.FEIGN_TAG);
		if (Checker.beNotEmpty(feignTag)) {
			if (Boolean.FALSE.equals(redisService.hasKey(feignTag))) {
				throw new GateWayRuntimeException(GateWayExceptionEnum.INVALID_FEIGN_TAG);
			}
			String url = request.getPath().pathWithinApplication().value();
			return url.startsWith(FEIGN_API_PREFIX);

		}
		return false;
	}

	protected ServerHttpRequest getRequest(ServerWebExchange exchange) {
		return exchange.getRequest();
	}

	protected String getHeader(ServerHttpRequest request, String headerName) {
		return request.getHeaders().getFirst(headerName);
	}

	protected String getUrl(ServerHttpRequest request) {
		return request.getPath().pathWithinApplication().value();
	}

	protected String getToken(ServerHttpRequest request) {
		return request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
	}

	private RSAPublicKey decryptBase64(String key) {
		byte[] bytes = Base64.getDecoder().decode(key);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
		KeyFactory kf;
		try {
			kf = KeyFactory.getInstance("RSA");
			PublicKey pk = kf.generatePublic(keySpec);
			return (RSAPublicKey)pk;
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new DecodeRASKeyException();
		}

	}

	protected void verifyTenantToken(ServerHttpRequest request, String token) {
		ServerHttpRequest.Builder mutate = request.mutate();
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
					redisService.hGet(contextKey,TenantUserKey.userJwtRsaKey(tag))
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
			boolean beNotExpire = redisService.hasKey(contextKey);
			//Date expireTime = DateUtil.toDate(expires, Formats.DEFAULT_DATE_TIME_FORMAT);
			if (!beNotExpire) {
				throw new TokenTimeOutException();
			}
			//校验成功后，获取封装的jwt信息
			String userId = ObjectUtil.toStringIfNotNull(signedJWT.getJWTClaimsSet().getClaim(JWT_USER_ID));
			String userCnName = ObjectUtil.toStringIfNotNull(redisService.hGet(contextKey, TenantRedisKeyConstant.KEY_USER_REAL_CN_NAME));
			String userEnName = ObjectUtil.toStringIfNotNull(redisService.hGet(contextKey, TenantRedisKeyConstant.KEY_USER_REAL_EN_NAME));
			String rank = ObjectUtil.toStringIfNotNull(redisService.hGet(contextKey, TenantRedisKeyConstant.KEY_USER_RANK));
			String score = ObjectUtil.toStringIfNotNull(redisService.hGet(contextKey, TenantRedisKeyConstant.KEY_USER_SCORE));
			String state = ObjectUtil.toStringIfNotNull(redisService.hGet(contextKey, TenantRedisKeyConstant.KEY_USER_STATE));
			addHeader(mutate, UserHeaderConstant.USER_ID, userId);
			addHeader(mutate, UserHeaderConstant.USER_NAME, userNo);
			addHeader(mutate, UserHeaderConstant.USER_CN_NAME, userCnName);
			addHeader(mutate, UserHeaderConstant.USER_EN_NAME, userEnName);
			addHeader(mutate, UserHeaderConstant.USER_SYSTEM_TAG, otag);
			addHeader(mutate, UserHeaderConstant.USER_TENANT_ID, tenantId);
			addHeader(mutate, UserHeaderConstant.USER_FLAG, Strings.TENANT);
			addHeader(mutate, UserHeaderConstant.USER_TENANT_RANK, rank);
			addHeader(mutate, UserHeaderConstant.USER_TENANT_SCORE, score);
			addHeader(mutate, UserHeaderConstant.USER_TENANT_STATE, state);
		} catch (ParseException | JOSEException e) {
			log.error(e.getMessage());
			throw new InvalidTokenException();
		}
	}

	protected void verifyToken(ServerHttpRequest request, String token) {
		ServerHttpRequest.Builder mutate = request.mutate();
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
				beValid = redisService.lContains(key, token);
			} else {
				String pk = ObjectUtil.toStringIfNotNull(
					redisService.hGet(contextKey,UserKey.userJwtRsaKey(tag))
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
			boolean beNotExpire = redisService.hasKey(contextKey);
			//Date expireTime = DateUtil.toDate(expires, Formats.DEFAULT_DATE_TIME_FORMAT);
			if (!beNotExpire) {
				throw new TokenTimeOutException();
			}
			//超级用户直接跳过
	//				if (beSuper(userNo)) {
	//					addHeader(mutate, UserHeaderConstant.USER_ID, userNo);
	//					addHeader(mutate, UserHeaderConstant.USER_NAME, userNo);
	//					addHeader(mutate, UserHeaderConstant.USER_CN_NAME, userNo);
	//					addHeader(mutate, UserHeaderConstant.USER_EN_NAME, userNo);
	//				}
			//校验成功后，获取封装的jwt信息
			String userId = ObjectUtil.toStringIfNotNull(signedJWT.getJWTClaimsSet().getClaim(JWT_USER_ID));
			String userCnName = ObjectUtil.toStringIfNotNull(redisService.hGet(contextKey, KEY_USER_REAL_CN_NAME));
			String userEnName = ObjectUtil.toStringIfNotNull(redisService.hGet(contextKey, KEY_USER_REAL_EN_NAME));
			addHeader(mutate, UserHeaderConstant.USER_ID, userId);
			addHeader(mutate, UserHeaderConstant.USER_NAME, userNo);
			addHeader(mutate, UserHeaderConstant.USER_CN_NAME, userCnName);
			addHeader(mutate, UserHeaderConstant.USER_EN_NAME, userEnName);
			addHeader(mutate, UserHeaderConstant.USER_SYSTEM_TAG, otag);
			String expirePolicy = ObjectUtil.toStringIfNotNull(signedJWT.getJWTClaimsSet().getClaim(JWT_EXPIRE_POLICY));
			if (Checker.beNotEmpty(expirePolicy) && "LAST_ACTIVE".equals(expirePolicy)) {
				redisService.expire(contextKey, JWT_EXPIRE_IN_MILLISECONDS/1000);
			}
		} catch (ParseException | JOSEException e) {
			log.error(e.getMessage());
			throw new InvalidTokenException();
		}
	}


	protected void verifyAuthorization(ServerHttpRequest request, String userCacheAuths) {
		String url = getUrl(request);
		HttpMethod method = request.getMethod();
		String authorizationCacheKey = UserKey.buttonPermKey(method.name(),url) + "*";
		Set<String> keys = ObjectUtil.nullIfEmptySet(redisService.keys(authorizationCacheKey));
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
			//注意匹配/xxx/** 和 /xxx/{123} 模式
			int index = url.lastIndexOf("/");
			String charsAfterSlash = url.substring(index + 1);
			if (Checker.beNotEmpty(charsAfterSlash) && charsAfterSlash.equals(CharacterUtil.justLeftNumbers(charsAfterSlash))) {
				url = url.replace(charsAfterSlash, Strings.BRACE);
				String refKey = CACHE_API_PREFIX + method + Strings.DOUBLE_AT + url;
				if (Boolean.TRUE.equals(redisService.hasKey(refKey))) {
					matchedCacheKey = refKey;
				}
			}
		}
		//没有匹配上，说明当前的请求没有权限
		if (Checker.beEmpty(matchedCacheKey)) {
			throw new GateWayRuntimeException(GateWayExceptionEnum.NO_PRIVILEGE_EXCEPTION);
		}

		String needRole = redisService.get(matchedCacheKey);

		//URL资源不需要权限
		if (RESOURCE_NO_NEED_ROLE.equals(needRole)) {
			return;
		}
		//表明此API没有分配给任何角色
		if (Checker.beBlank(needRole) && Boolean.TRUE.equals(redisService.hasKey(matchedCacheKey))) {
			throw new GateWayRuntimeException(GateWayExceptionEnum.NO_PRIVILEGE_EXCEPTION);
		}
		Set<String> needRoles = new HashSet<>(Splitter.on(Strings.COMMA).splitToList(needRole));
		Set<String> userRoles = new HashSet<>(Splitter.on(Strings.COMMA).splitToList(userCacheAuths));
		boolean match = needRoles.stream().anyMatch(userRoles::contains);
		if (!match) {
			throw new GateWayRuntimeException(GateWayExceptionEnum.NO_PRIVILEGE_EXCEPTION);
		}
	}


	// 多次反向代理后会有多个ip值 的分割符
	protected String getIP(ServerHttpRequest request){

		// 未知IP
		final String UNKNOWN = "unknown";
		// 本地 IP
		final String LOCALHOST_IP_Virtual = "0:0:0:0:0:0:0:1";
		final String LOCALHOST_IP = "127.0.0.1";
		// 根据 HttpHeaders 获取 请求 IP地址
		String ip = request.getHeaders().getFirst("X-Forwarded-For");
		if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeaders().getFirst("x-forwarded-for");
			if (ip != null && !ip.isEmpty() && !UNKNOWN.equalsIgnoreCase(ip)) {
				// 多次反向代理后会有多个ip值，第一个ip才是真实ip
				if (ip.contains(Strings.COMMA)) {
					ip = ip.split(Strings.COMMA)[0];
				}
			}
		}
		if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeaders().getFirst("Proxy-Client-IP");
		}
		if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeaders().getFirst("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeaders().getFirst("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeaders().getFirst("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeaders().getFirst("X-Real-IP");
		}
		//兼容k8s集群获取ip
		if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddress().getAddress().getHostAddress();
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
