package com.baomibing.authority.controller;

import com.baomibing.authority.dto.UserDto;
import com.baomibing.authority.jwt.JwtTokenResponse;
import com.baomibing.authority.service.SystemService;
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
public abstract class ASystemController extends ActionController {


    private final SystemService systemService;

    public ASystemController(SystemService s) {
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
