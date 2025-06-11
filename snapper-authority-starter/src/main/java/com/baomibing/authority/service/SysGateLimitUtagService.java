package com.baomibing.authority.service;



import com.baomibing.authority.dto.GateLimitUtagDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

public interface SysGateLimitUtagService extends MBaseService<GateLimitUtagDto> {

    List<GateLimitUtagDto> listByLimit(String limitId);

    void saveLimitUtag(GateLimitUtagDto limitUtag);

    void updateLimitUtag(GateLimitUtagDto limitUtag);

    void deleteLimitUtag(Set<String> ids);

    void deleteByLimit(String limitId);
}
