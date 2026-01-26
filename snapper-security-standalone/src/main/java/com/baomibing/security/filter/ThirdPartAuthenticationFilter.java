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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.baomibing.authority.exception.InvalidTokenException;
import com.baomibing.authority.util.WebUtil;
import com.baomibing.tool.constant.WebConstant;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.URLUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * ThirdPartAuthenticationFilter
 *
 * @author zening 2023/12/5 09:38
 * @version 1.0.0
 **/
@Slf4j
public class ThirdPartAuthenticationFilter extends BaseFilter {


    private static final List<String> whites = Lists.newArrayList(WebConstant.HMAC_API_PREFIX, WebConstant.JWT_API_PREFIX, WebConstant.TENANT_API_PREFIX);

    @Value("${baomibing.thirdPartParamName:extendedParams}")
    private String thirdPartParam = "extendedParams";

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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String url = request.getRequestURI();

        if (matchWhiteList(url)) {
            filterChain.doFilter(request, response);
            return;
        }


        if (beLoginRequest(url)) {
            filterChain.doFilter(request, response);
            return;
        }

        String params = WebUtil.receivePost(request);
        if (Checker.beEmpty(params)) {
            throw new InvalidTokenException();
        }
        String token;
        //提取token
        try {
            String extendedParams = JSON.parseObject(params).getString(thirdPartParam);
            token = JSON.parseObject(extendedParams).getString("token");
        } catch (JSONException joe) {
            throw new InvalidTokenException();
        }
        if (Checker.beEmpty(token)) {
            throw new InvalidTokenException();
        }
        token = URLUtil.decode(token).substring(WebConstant.JWT_BEAR_TYPE.length()).trim();

        verifyToken(request, token);
        filterChain.doFilter(request, response);
    }
}
