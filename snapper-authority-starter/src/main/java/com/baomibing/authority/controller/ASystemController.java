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

package com.baomibing.authority.controller;

import com.baomibing.authority.dto.UserDto;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.jwt.JwtTokenResponse;
import com.baomibing.authority.service.SystemService;
import com.baomibing.cache.CacheService;
import com.baomibing.core.common.Assert;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.tool.constant.RedisKeyConstant;
import com.baomibing.tool.user.RequestContext;
import com.baomibing.web.annotation.ULog;
import com.baomibing.web.base.ActionController;
import com.google.common.base.Optional;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ASystemController
 *
 * @author zening 2023/8/9 17:48
 * @version 1.0.0
 **/
public abstract class ASystemController extends ActionController {


    private final SystemService systemService;

    @Autowired private CacheService cacheService;

    public ASystemController(SystemService s) {
        systemService = s;
    }

    private void beLockIp(HttpServletRequest request) {
        String ip = RequestContext.reqIp();
        //检查是否超过重复登录次数
        String retryCacheKey = MessageFormat.format(RedisKeyConstant.CACHE_RETRY_IP_PREFIX, ip);

        int retryTime = Integer.parseInt(Optional.fromNullable(cacheService.get(retryCacheKey)).or("0"));

        AtomicInteger atomicInteger = new AtomicInteger(retryTime);
        int number = atomicInteger.incrementAndGet();

        long ONE_DAY = 60 * 60 * 24 * 7;
        cacheService.set(retryCacheKey, String.valueOf(number), ONE_DAY);
        // 如果重试次数超出则抛出异常
        if (atomicInteger.incrementAndGet() > 5) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.USER_IP_LOCKED, ip);
        }
    }

    @ULog("系统登录")
    @PostMapping(value = "/token")
    public JwtTokenResponse login(@RequestBody UserDto userDto, HttpServletRequest request) {
        Assert.CheckArgument(userDto);
        String userName = userDto.getUserName();
        String password = userDto.getUserPasswd();
        String tag = userDto.getSystemTag();
        String orgId = userDto.getGroupId();
        Assert.CheckArgument(userName);
        Assert.CheckArgument(password);

        beLockIp( request);

        if (!validateCaptcha(userDto.getCaptcha())) {
            throw new ServerRuntimeException(ExceptionEnum.CAPTCHA_NOT_CORRECT);
        }

        String md5Password = DigestUtils.md5Hex(password);



        JwtTokenResponse response =  systemService.login(userName, md5Password, tag, orgId);
        String ip = RequestContext.reqIp();
        //检查是否超过重复登录次数
        String retryCacheKey = MessageFormat.format(RedisKeyConstant.CACHE_RETRY_IP_PREFIX, ip);
        cacheService.del(retryCacheKey);

        return response;
    }

    public abstract boolean validateCaptcha(String captcha);

    @PostMapping("logout")
    public void logout() {
        systemService.logout();
    }
}
