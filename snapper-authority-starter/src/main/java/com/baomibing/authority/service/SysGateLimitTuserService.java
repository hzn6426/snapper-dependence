package com.baomibing.authority.service;



import com.baomibing.authority.dto.GateLimitTuserDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

public interface SysGateLimitTuserService extends MBaseService<GateLimitTuserDto> {

    List<GateLimitTuserDto> listByLimit(String limitId);

    void saveLimitUser(GateLimitTuserDto limitUser);

    void updateLimitUser(GateLimitTuserDto limitUser);

    void deleteLimitUser(Set<String> limitUserIds);

    void deleteByLimit(String limitId);
}
