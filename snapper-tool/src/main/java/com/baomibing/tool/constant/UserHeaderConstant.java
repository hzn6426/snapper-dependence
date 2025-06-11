/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.constant;
/**
 * 用户封装常量
 * @author zening
 * @version 1.0.0
 */
public abstract class UserHeaderConstant {

	//=======================================================================//
	//                             USER AUTH HEADER                          //
	//=======================================================================//
	public static final String USER_URL = "HEADER_USER_URL";

	public static final String USER_IP = "HEADER_USER_IP";

	public static final String USER_ID = "HEADER_USER_ID";

	public static final String USER_NAME = "HEADER_USER_NAME";

	public static final String USER_GROUP = "HEADER_USER_GROUP";

	public static final String USER_GROUP_NAME = "HEADER_USER_GROUP_NAME";

	public static final String USER_COMPANY_ID = "HEADER_USER_COMPANY_ID";

	public static final String USER_COMPANY_NAME = "HEADER_USER_COMPANY_NAME";

	public static final String USER_POSITION = "HEADER_USER_POSITION";

	public static final String USER_ROLES = "HEADER_USER_ROLES";

	public static final String USER_CN_NAME = "HEADER_USER_CN_NAME";

	public static final String USER_EN_NAME = "HEADER_USER_EN_NAME";

	public static final String CONTENT_TYPE = "Content-Type";

	public static final String FEIGN_TAG = "HEADER_FEIGN_TAG";

	public static final String USER_OUTER_SYSTEM = "HEADER_USER_OUTER_SYSTEM";

	public static final String USER_TAG = "HEADER_USER_TAG";

	public static final String USER_PARAM = "HEADER_USER_PARAM";

	public static final String USER_TOKEN = "HEADER_USER_TOKEN";

	public static final String USER_SYSTEM_TAG = "HEADER_USER_SYSTEM_TAG";

	public static final String USER_TENANT_ID = "HEADER_USER_TENANT_ID";

	public static final String USER_TENANT_RANK = "HEADER_USER_TENANT_RANK";

	public static final String USER_TENANT_SCORE = "HEADER_USER_TENANT_SCORE";

	public static final String USER_TENANT_STATE = "HEADER_USER_TENANT_STATE";

	public static final String USER_FLAG = "HEADER_USER_TENANT_FLAG";

	//user datasource
	public static final String USER_DS = "HEADER_USER_DS";

	//需要调用哪个数据源的头
	public static final String X_DS = "X-DS";

	public static final String USER_AUTHORIZATION = "Authorization";

	public static final String LANG = "LANG";
	//网关转发标记
	public static final String GATE_WAY_REDIRECT_TAG = "_GATE_WAY_REDIRECT_KEY";

	public static final String HMAC_USER_NAME = "HMAC_HEADER_USER_NAME";

	public static final String HMAC_USER_GROUP = "HMAC_HEADER_USER_GROUP";

	public static final String HMAC_USER_GROUP_NAME = "HMAC_HEADER_USER_GROUP_NAME";

	public static final String HMAC_USER_CN_NAME = "HMAC_HEADER_USER_CN_NAME";

	public static final String HMAC_USER_BUSINESS_ID = "HMAC_USER_BUSINESS_ID";
	// User-Agent
	public final static String USER_AGENT = "User-Agent";

	//=======================================================================//
	//                             HMAC AUTH HEADER                          //
	//=======================================================================//

	public static final String PARAM_HMAC_APP_ID = "appId";

	public static final String PARAM_HMAC_TIMESTAMP = "timestamp";

	public static final String PARAM_HMAC_DIGEST = "digest";
}
