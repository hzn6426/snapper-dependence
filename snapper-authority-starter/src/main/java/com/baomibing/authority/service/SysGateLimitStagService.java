package com.baomibing.authority.service;



import com.baomibing.authority.dto.GateLimitStagDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

public interface SysGateLimitStagService extends MBaseService<GateLimitStagDto> {

    List<GateLimitStagDto> listByLimit(String limitId);

    void saveLimitStag(GateLimitStagDto limitStag);

    void updateLimitStag(GateLimitStagDto limitStag);

    void deleteLimitStag(Set<String> ids);

    void deleteByLimit(String limitId);
}
