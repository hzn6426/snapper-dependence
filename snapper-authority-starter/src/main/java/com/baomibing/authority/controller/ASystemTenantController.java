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
import com.baomibing.authority.jwt.JwtTokenResponse;
import com.baomibing.authority.service.SystemTenantService;
import com.baomibing.core.common.Assert;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.web.annotation.ULog;
import com.baomibing.web.base.ActionController;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * ASystemController
 *
 * @author zening 2023/8/9 17:48
 * @version 1.0.0
 **/
public abstract class ASystemTenantController extends ActionController {


    private final SystemTenantService systemService;

    public ASystemTenantController(SystemTenantService s) {
        systemService = s;
    }

    @ULog("系统登录")
    @PostMapping(value = "/token")
    public JwtTokenResponse login(@RequestBody UserDto userDto) {
        Assert.CheckArgument(userDto);
        String userName = userDto.getUserName();
        String password = userDto.getUserPasswd();
        String tag = userDto.getSystemTag();
        String orgId = userDto.getGroupId();
        Assert.CheckArgument(userName);
        Assert.CheckArgument(password);

        if (!validateCaptcha(userDto.getCaptcha())) {
            throw new ServerRuntimeException(ExceptionEnum.CAPTCHA_NOT_CORRECT);
        }

        String md5Password = DigestUtils.md5Hex(password);

        return systemService.login(userName, md5Password, tag, orgId);
    }

    public abstract boolean validateCaptcha(String captcha);

    @PostMapping("logout")
    public void logout() {
        systemService.logout();
    }
}
