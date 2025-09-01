/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.web.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 跨域过滤器
 *
 * @author zening
 * @since 1.0.0
 */
public class CrosFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //允许请求方式
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
        //请求包含的字段内容，如有多个可用哪个逗号分隔如下
        response.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, Access-Control-Request-Method, "
                + "Access-Control-Request-Headers, Accept, Origin, Content-Type, x-requested-with, Authorization, x-ui-request, lang, timestamp");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Max-Age", "3600");
        if (((HttpServletRequest) servletRequest).getMethod().equals("OPTIONS")) {
            response.getWriter().println("ok");
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
