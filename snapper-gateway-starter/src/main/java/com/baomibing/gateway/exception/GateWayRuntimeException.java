/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.gateway.exception;

import com.alibaba.fastjson.JSON;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.exception.ExceptionEnumable;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Maps;
import lombok.Getter;

import java.text.MessageFormat;
import java.util.Map;

public class GateWayRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 3792290583141022119L;
	@Getter
	protected int code;
	@Getter
	protected String codeMessage;

	public GateWayRuntimeException(ExceptionEnumable gatewayExceptionEnum) {
		super(MessageFormat.format(gatewayExceptionEnum.getExceptionMessage(), new Object[] {"","","",""}));
		codeMessage = super.getMessage();
		this.code = gatewayExceptionEnum.getExceptionCode();
	}
	
	
	public GateWayRuntimeException(ExceptionEnumable gatewayExceptionEnum, Object... arguments) {
		super(MessageFormat.format(gatewayExceptionEnum.getExceptionMessage(), Checker.beEmpty(arguments) ? new Object[] {"","","",""} : arguments));
		codeMessage = super.getMessage();
		this.code = gatewayExceptionEnum.getExceptionCode();
	}
	
	public Map<String, Object> toMap() {
		Map<String, Object> map = Maps.newHashMap();
		map.put(Strings.CODE, code);
		map.put(Strings.MESSAGE, codeMessage);
		return map;
	}
	
	public  String json() {
		return JSON.toJSONString(this.toMap());
	}
}
