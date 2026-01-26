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

package com.baomibing.security.filter;

import com.baomibing.authority.service.SysTenantRoleResourceService;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.constant.UserHeaderConstant;
import com.baomibing.tool.constant.WebConstant;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static com.baomibing.tool.constant.UserHeaderConstant.USER_ROLES;

/**
 * hmac 鉴权
 **/
public class CommonHmacAuthorizationFilter extends BaseFilter {

    @Autowired private SysTenantRoleResourceService tenantRoleResourceService;

    private static final List<String> whites = Lists.newArrayList(WebConstant.JWT_API_PREFIX, WebConstant.THIRD_API_PREFIX, WebConstant.TENANT_API_PREFIX);

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
        String method = request.getMethod();
        String url = request.getRequestURI();
        if (matchWhiteList(url)) {
            filterChain.doFilter(request, response);
            return;
        }

        String userFlag = getHeader(request, UserHeaderConstant.USER_FLAG);
        String userCacheAuths = getHeader(request, USER_ROLES);
        //租户
        if (Strings.TENANT.equalsIgnoreCase(userFlag)) {
            String uri = url.replace("/wapi/", "/eapi/");
            uri = handleGetUri(uri);
            String tenantId = getHeader(request, UserHeaderConstant.USER_TENANT_ID);
            String needRole = tenantRoleResourceService.getRolesByUrl(tenantId, uri, method);
            verifyTenantAuthorization(needRole, userCacheAuths);
            filterChain.doFilter(request, response);
            return;
        }
        verifyAuthorization(request, userCacheAuths);
        filterChain.doFilter(request, response);
    }
}
