/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.gateway.exception;

import com.baomibing.tool.exception.ExceptionEnumable;
import lombok.Getter;

@Getter 
public enum GateWayExceptionEnum implements ExceptionEnumable {

	
	UNCLEAR_USER_GROUP_NEET_TO_CHANGE_GROUP(20000, "当前用户的组织未明确，请先切换用户组织!"),
	NO_PRIVILEGE_EXCEPTION(403, "用户没有访问资源的权限!"),
	USER_BE_UNAUTHERIZED(401, "用户权限资源认证失败，没有权限!"),
	DECODE_RSA_EXCEPTION(2001, "解码RSA KEY失败，请重新登录!"),
	INVALID_TOKEN_EXCEPTION(20002, "鉴权失败，请重新登录!"),
	TOKEN_TIME_OUT_EXCEPTION(20003, "登录时间过长，请重新登录!"),
	UNCLEAR_USER_GROUP_EXCEPTION(20004, "未明确组织，无法进行操作，请先选择组织后再进行其他操作!"),
	SYSTEM_IS_BUSY(20005, "系统繁忙，请稍后重试!"),
	USER_LOGIN_IN_ANOTHER_PLACE(20006, "用户在其他地方登陆，请重新登录!"), 
	INVALID_FEIGN_TAG(20007, "非法的FEIGN请求调用!"),
	TENANT_ID_NOT_VALID(20008, "租户ID不合法，无法进行后续操作！"),
	;


	
	GateWayExceptionEnum(final int code, final String message) {
		this.code = code;
		this.message = message;
	}
	
	private int code;
	
	private String message;

	@Override
	public int getExceptionCode() {
		return this.code;
	}


	@Override
	public String getExceptionMessage() {
		return this.message;
	}

	
	

}
