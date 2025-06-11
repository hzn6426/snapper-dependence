package com.baomibing.authority.service;



import com.baomibing.authority.dto.GateLimitDto;
import com.baomibing.core.base.MBaseService;
import com.baomibing.core.common.SearchResult;

import java.util.List;
import java.util.Set;

public interface SysGateLimitService extends MBaseService<GateLimitDto> {

    SearchResult<GateLimitDto> searchGateLimit(GateLimitDto limit, int pageNo, int pageSize);

    GateLimitDto getGateLimitById(String id);

    void saveGateLimit(GateLimitDto limit);

    void updateGateLimit(GateLimitDto limit);

    void deleteLimits(Set<String> limits);

    void doStop(Set<String> limits);

    void doUse(Set<String> limits);

    List<GateLimitDto> listUsed();

    void refreshLimitCache();
}
