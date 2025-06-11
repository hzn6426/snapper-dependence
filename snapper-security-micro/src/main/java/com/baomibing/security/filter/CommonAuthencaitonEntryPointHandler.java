/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.security.filter;

import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.tool.util.Checker;
import com.baomibing.web.common.R;
import com.baomibing.web.common.WebHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 访问认证失败处理-例如token校验失败-鉴权时（accessDecision）抛出AuthenticationException都会进入此方法
 * 
 * @author zening
 * @since 1.0.0
 */
@Slf4j
public class CommonAuthencaitonEntryPointHandler implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		String message = Checker.beEmpty(authException.getMessage()) ? "" : authException.getMessage();
		log.error(message);
		WebHelper.write(response, R.build(new ServerRuntimeException(ExceptionEnum.USER_BE_UNAUTHERIZED)).json());
		
	}

}
