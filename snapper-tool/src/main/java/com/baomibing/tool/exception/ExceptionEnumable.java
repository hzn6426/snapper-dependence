/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
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
