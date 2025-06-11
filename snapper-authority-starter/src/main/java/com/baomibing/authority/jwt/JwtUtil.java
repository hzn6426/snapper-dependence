package com.baomibing.authority.jwt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomibing.authority.exception.CreateRSAKeyException;
import com.baomibing.authority.exception.DecodeRASKeyException;
import com.baomibing.authority.exception.InvalidTokenException;
import com.baomibing.authority.exception.TokenTimeOutException;
import com.baomibing.tool.constant.WebConstant;
import com.baomibing.tool.util.SnowflakeIdWorker;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.Validate;

import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.baomibing.authority.constant.SystemConst.*;

public class JwtUtil {

	private JwtUtil() {}

	public static RSAPublicKey decryptBase64(String key) {
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
	
	/**
	 * 根据用户信息,加密串生成jwt
	 * @param jwtUser jwt用户信息
	 * @param privateKey 加密串
	 * @return
	 */
	public static JwtWrapper createAccessToken(JwtUser jwtUser, PrivateKey privateKey) {
		Validate.notNull(jwtUser, "param jwtUser not be null!");
		Map<String,Object> claimMap = new HashMap<>();
		claimMap.put(WebConstant.JWT_USER_ID, jwtUser.getUserId());
		claimMap.put(WebConstant.JWT_USER_NO, jwtUser.getUserNo());
		claimMap.put(WebConstant.JWT_GROUP_ID, jwtUser.getGroupId());
//		claimMap.put(SystemConsts.JWT_COMPANY_ID, jwtUser.getCompanyId());
//		claimMap.put(SystemConsts.JWT_DEPARTMENT_ID, jwtUser.getDepartmentId());
//		claimMap.put(SystemConsts.JWT_USER_TYPE, jwtUser.getUtype());
		Claims claims = Jwts.claims(claimMap);
		String id = SnowflakeIdWorker.getId();
//		Date expireDate = new DateTime().plusMillis(SystemConsts.JWT_EXPIRE_IN_MILLISECONDS).toDate();
		String token = Jwts.builder().setClaims(claims).setId(id)
			.setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + WebConstant.JWT_EXPIRE_IN_MILLISECONDS))
			.signWith(privateKey, SignatureAlgorithm.RS512)
			.compact();
		JwtWrapper jwt = new JwtWrapper();
		jwt.setClaimMap(claimMap).setId(id)
			.setToken(token);
		return jwt;
	}
	
	/**
	 * 生成refresh token
	 * @return
	 */
	public static String createRefreshToken() {
		return RandomStringUtils.randomAlphabetic(128);
	}
	
	/**
	 * 加密publicKey
	 * @param publicKey
	 * @return
	 */
	public static String encryptBase64(PublicKey publicKey) {
		return Base64.getEncoder().encodeToString(publicKey.getEncoded());
	}

	
	/**
	 * 校验token
	 * @param token jwt token
	 * @param publicKey 公钥
	 * @return
	 */
	public static Claims verifyToken(String token, PublicKey publicKey){
		try {
			return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody();
		} catch(ExpiredJwtException ee) {
			throw new TokenTimeOutException();
		} catch (Exception e) {
			throw new InvalidTokenException();
		}
	}
	/**
	 * 生成RSA加密KEY，包括公钥和私钥
	 * @return
	 */
	public static Map<String, Key> generateRsaKeys() {
		try {
			KeyPairGenerator g = KeyPairGenerator.getInstance(ALGORITHM_RSA);
			g.initialize(2048);
			KeyPair kp = g.generateKeyPair();
			PrivateKey privateKey = kp.getPrivate();
			PublicKey publicKey = kp.getPublic();
			Map<String, Key> keyMap = new HashMap<>();
			keyMap.put(RSA_PRIVATE_KEY, privateKey);
			keyMap.put(RSA_PUBLIC_KEY, publicKey);
			return keyMap;
		} catch (NoSuchAlgorithmException e) {
			throw new CreateRSAKeyException();
		}
		
	}
	
	
	
	/**
	 * 解析jwt的payload
	 * @param jwt
	 * @return
	 */
	public static JwtUser parseJwtPayload(String jwt) {
		Validate.notEmpty(jwt, "jwt params must not be null!");
		Base64.Decoder decoder = Base64.getDecoder();
		String[] parts = jwt.split("\\.");
		String payload = new String(decoder.decode(parts[1]));
		JSONObject json = JSON.parseObject(payload);
		JwtUser user = new JwtUser();
		user.setGroupId(json.getString(WebConstant.JWT_GROUP_ID))
			.setUserId(json.getString(WebConstant.JWT_USER_ID))
			.setUserNo(json.getString(WebConstant.JWT_USER_NO))
//			.setUtype(json.getString(SystemConsts.JWT_USER_TYPE))
			.setCompanyId(json.getString(WebConstant.JWT_COMPANY_ID))
			.setDepartmentId(json.getString(WebConstant.JWT_DEPARTMENT_ID));
		return user;
	}

	public static void main(String[] args) {
		for (int i = 0; i < 1000; i++) {
			System.out.println(	SnowflakeIdWorker.getId());
		}

	}
}
