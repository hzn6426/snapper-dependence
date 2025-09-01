/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.web.filter;

import com.baomibing.web.common.MultiReadHttpServletRequest;
import com.baomibing.web.common.MultiReadHttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;



/**
 * 多次读取Request/Response封装过滤器
 * 
 * @author zening
 * @since 1.0.0
 */
public class MultiRequestWrapFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		final MultiReadHttpServletRequest wrappedRequest = new MultiReadHttpServletRequest(request);
		final MultiReadHttpServletResponse wrappedResponse = new MultiReadHttpServletResponse(response);
		super.doFilter(wrappedRequest, wrappedResponse, filterChain);
		
	}
	
	

}
