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
