/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 访问资源鉴权失败处理
 * 
 * @author zening
 * @since 1.0.0
 */
@Slf4j
public class CommonAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		String message = Checker.beEmpty(accessDeniedException.getMessage()) ? "" : accessDeniedException.getMessage();
		log.error(message);
		WebHelper.write(response, R.build(new ServerRuntimeException(ExceptionEnum.NO_PRIVILEGE_EXCEPTION)).json());
	}

}
