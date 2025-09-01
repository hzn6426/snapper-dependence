/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.web.enums;

import lombok.Getter;

/**
 * 用户日志类型枚举
 * 
 * @author zening
 * @since 1.0.0
 */
public enum UserLogTypeEnum {

	OP_LOGIN("用户登录"), OP_NEW("新增"), OP_UPDATE("更新"), OP_DELETE("删除"), OP_EXPORT("导出"), OP_IMPORT("导入"), OP_DOWNLOAD("下载"), OP_UPLOAD("上传"), OP_OTHER("其他");
	
	@Getter
	private String svalue;
	
	UserLogTypeEnum(String name) {
		this.svalue = name;
	}
}
