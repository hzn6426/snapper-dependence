package com.baomibing.security.filter;

import com.baomibing.tool.constant.WebConstant;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Sets;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.baomibing.tool.constant.UserHeaderConstant.USER_ROLES;


/**
 * CommonJwtAuthorizationFilter
 *
 * @author zening 2023/9/5 10:33
 * @version 1.0.0
 **/
public class CommonJwtAuthorizationFilter extends BaseFilter {

    private static final CopyOnWriteArraySet<String> whites =  new CopyOnWriteArraySet<>(Sets.newHashSet(
            WebConstant.API_WEIXIN_URL,
            WebConstant.API_DEPATMENT_CHANGE,
            WebConstant.API_USER_DEPARTMENTS,
            WebConstant.API_USER_CURRENT,
            WebConstant.API_USER_BUTTONS,
            WebConstant.API_USER_ADMIN_MENUS,
            WebConstant.API_USER_MENUS,
            WebConstant.API_USER_LOG_URL,
            WebConstant.SOCKET_CLIENT,
            WebConstant.HMAC_API_PREFIX,
            WebConstant.THIRD_API_PREFIX,
            WebConstant.TENANT_API_PREFIX
    ));

    private boolean matchWhiteList(String url) {
        return whites.stream().anyMatch(w -> pathMatch.match(w, url));
    }

    @Override
    public void addWhites(Set<String> urls) {
        if (Checker.beNotEmpty(urls)) {
            whites.addAll(urls);
        }
    }




    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String url = request.getRequestURI();
        String protocol = request.getProtocol();

        if (beSocketProtocol(protocol)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (beLoginOrOutRequest(url)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (beUserValidateForDepartments(url)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 白名单需要添加用户信息
        if (matchWhiteList(url)) {
            filterChain.doFilter(request, response);
            return;
        }
        String userCacheAuths = getHeader(request, USER_ROLES);
        verifyAuthorization(request, userCacheAuths);
        filterChain.doFilter(request, response);
    }
}
