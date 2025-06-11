/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.web.enums;

import lombok.Getter;

/**
 * 方法名映射日志类型规则
 * 
 * @author zening
 * @since 1.0.0
 */
public enum MethodNameLogRuleEnum {

	login("OP_LOGIN"), save("OP_NEW"), update("OP_UPDATE"), delete("OP_DELETE"), export("OP_EXPORT"), impor("IMPORT"), 
	download("OP_DOWNLOAD"), upload("OP_UPLOAD"), other("OP_OTHER");
	
	@Getter
	private String logType;
	
	MethodNameLogRuleEnum(String logType){
		this.logType = logType;
	}
}
