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
