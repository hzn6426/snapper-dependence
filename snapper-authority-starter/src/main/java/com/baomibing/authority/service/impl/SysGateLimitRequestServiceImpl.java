package com.baomibing.authority.service.impl;


import com.baomibing.authority.dto.GateLimitRequestDto;
import com.baomibing.authority.entity.SysGateLimitRequest;
import com.baomibing.authority.mapper.SysGateLimitRequestMapper;
import com.baomibing.authority.service.SysGateLimitRequestService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * SysGateLimitRequestServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysGateLimitRequestServiceImpl extends MBaseServiceImpl<SysGateLimitRequestMapper, SysGateLimitRequest, GateLimitRequestDto> implements SysGateLimitRequestService {

    @Override
    public List<GateLimitRequestDto> listByLimit(String limitId) {
        if (Checker.beEmpty(limitId)) {
            return Lists.newArrayList();
        }
        return mapper(baseMapper.selectList(lambdaQuery().eq(SysGateLimitRequest::getLimitId, limitId).orderByAsc(SysGateLimitRequest::getPriority, SysGateLimitRequest::getCreateTime)));
    }

    @Override
    public void saveLimitRequest(GateLimitRequestDto limitRequest) {
        saveIt(limitRequest);
    }

    @Override
    public void updateLimitRequest(GateLimitRequestDto limitRequest) {
        updateIt(limitRequest);
    }

    @Override
    public void deleteLimitRequest(Set<String> ids) {
        deletes(ids);
    }

    @Override
    public void deleteByLimit(String limitId) {
        Assert.CheckArgument(limitId);
        baseMapper.delete(lambdaQuery().eq(SysGateLimitRequest::getLimitId, limitId));
    }
}
