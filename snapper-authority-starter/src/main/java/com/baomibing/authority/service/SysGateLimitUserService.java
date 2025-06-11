package com.baomibing.authority.service;



import com.baomibing.authority.dto.GateLimitUserDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

public interface SysGateLimitUserService extends MBaseService<GateLimitUserDto> {

    List<GateLimitUserDto> listByLimit(String limitId);

    void saveLimitUser(GateLimitUserDto limitUser);

    void updateLimitUser(GateLimitUserDto limitUser);

    void deleteLimitUser(Set<String> limitUserIds);

    void deleteByLimit(String limitId);
}
