/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.web.xss;

import lombok.Setter;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
public class XssFilter extends OncePerRequestFilter {

	//需要排除的url
	@Setter
	private List<String> exclusions = null;
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String servletPath = request.getServletPath();
		if (exclusions != null && exclusions.contains(servletPath)) {
			filterChain.doFilter(request, response);
        } else {
        	filterChain.doFilter(new XssHttpServletRequestWrapper(request), response);
        }
		
	}

	
}
