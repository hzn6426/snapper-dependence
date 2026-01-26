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
