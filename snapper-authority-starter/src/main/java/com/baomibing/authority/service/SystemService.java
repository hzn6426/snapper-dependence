package com.baomibing.authority.service;


import com.baomibing.authority.jwt.JwtTokenResponse;

public interface SystemService {

    /**
     * 登录
     * @param userName
     * @param password
     * @param tag
     * @param orgId
     * @return
     */
    JwtTokenResponse login(String userName, String password, String tag, String orgId);

    /**
     * 登出接口
     */
    void logout();
}
