/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.exception;

import com.alibaba.fastjson.JSON;
import com.baomibing.tool.exception.ExceptionEnumable;
import com.baomibing.tool.util.Checker;
import lombok.Getter;

import java.text.MessageFormat;

/**
 * 自定义服务运行异常
 * 
 * @author zening
 * @since 1.0.0
 */
public  class ServerRuntimeException extends RuntimeException {
	private static final long serialVersionUID = -1;
	@Getter
	protected int code;
	@Getter
	protected String codeMessage;

	public ServerRuntimeException(ExceptionEnumable exceptionEnum) {
		super(MessageFormat.format(exceptionEnum.loadByLang(exceptionEnum.name()).getExceptionMessage(), new Object[] {"","","",""}));
		codeMessage = super.getMessage();
		this.code = exceptionEnum.getExceptionCode();
	}
	
	public ServerRuntimeException(int code, String message) {
		super(message);
		this.code = code;
		this.codeMessage = message;
	}
	
//	public AbstractServerException(ExceptionEnumable exceptionEnum, Exception exception) {
//		super(exception);
//		this.exception = exception;
//		codeMessage = getBundleMessage(exceptionEnum.getExceptionCode());
//		this.code = exceptionEnum.getExceptionCode();
//	}
	
	public ServerRuntimeException(ExceptionEnumable exceptionEnum, Object... arguments) {
		super(MessageFormat.format(exceptionEnum.loadByLang(exceptionEnum.name()).getExceptionMessage(),
				Checker.beEmpty(arguments) ? new Object[] { "", "", "", "" } : arguments));
		codeMessage = super.getMessage();
		this.code = exceptionEnum.getExceptionCode();
	}
	
	public  String json() {
		return JSON.toJSONString(this);
	}
	
	
}
