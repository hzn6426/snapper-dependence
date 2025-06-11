package com.baomibing.authority.service;



import com.baomibing.authority.dto.GateLimitRequestDto;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

/**
 * SysGateLimitRequestService
 *
 * @author zening 2023/8/21 14:06
 * @version 1.0.0
 **/
public interface SysGateLimitRequestService extends MBaseService<GateLimitRequestDto> {

    List<GateLimitRequestDto> listByLimit(String limitId);

    void saveLimitRequest(GateLimitRequestDto limitRequest);

    void updateLimitRequest(GateLimitRequestDto limitRequest);

    void deleteLimitRequest(Set<String> ids);

    void deleteByLimit(String limitId);
}
