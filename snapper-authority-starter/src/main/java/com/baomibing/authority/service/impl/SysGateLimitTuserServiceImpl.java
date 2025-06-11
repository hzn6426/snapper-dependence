package com.baomibing.authority.service.impl;

import com.baomibing.authority.dto.GateLimitTuserDto;
import com.baomibing.authority.entity.SysGateLimitTuser;
import com.baomibing.authority.mapper.SysGateLimitTuserMapper;
import com.baomibing.authority.service.SysGateLimitTuserService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * SysGateLimitTuserServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysGateLimitTuserServiceImpl extends MBaseServiceImpl<SysGateLimitTuserMapper, SysGateLimitTuser, GateLimitTuserDto> implements SysGateLimitTuserService {

    @Override
    public List<GateLimitTuserDto> listByLimit(String limitId) {
        if (Checker.beEmpty(limitId)) {
            return Lists.newArrayList();
        }
        return mapper(baseMapper.selectList(lambdaQuery().eq(SysGateLimitTuser::getLimitId, limitId)));
    }

    @Override
    public void saveLimitUser(GateLimitTuserDto limitUser) {
        saveIt(limitUser);
    }

    @Override
    public void updateLimitUser(GateLimitTuserDto limitUser) {
        updateIt(limitUser);
    }

    @Override
    public void deleteLimitUser(Set<String> limitUserIds) {
        deletes(limitUserIds);
    }

    @Override
    public void deleteByLimit(String limitId) {
        Assert.CheckArgument(limitId);
        baseMapper.delete(lambdaQuery().eq(SysGateLimitTuser::getLimitId, limitId));
    }
}
