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
package com.baomibing.tool.exception;

import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.user.RequestContext;
import com.baomibing.tool.util.Checker;
import org.apache.commons.lang3.EnumUtils;

/**
 * 异常枚举接口
 * 
 * @author zening
 * @since 1.0.0
 */
public interface ExceptionEnumable {

	int getExceptionCode();
	
	String getExceptionMessage();

	String name();

	default ExceptionEnumable loadByLang(String v)  {
		try {
			String language = RequestContext.reqLanguage();
			ExceptionEnumable e = null;
			if (Checker.beEmpty(language) || Strings.ZH_CN.equalsIgnoreCase(language)) {
				e = this;
			} else {
				Class clz = Class.forName(this.getClass().getName() + Strings.UNDERSCORE + language);;
				e = (ExceptionEnumable) EnumUtils.getEnum(clz, v);
			}
			if (Checker.beNull(e)) {
				return this;
			}
			return e;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
