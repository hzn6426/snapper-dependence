package com.baomibing.authority.service.impl;

import com.baomibing.authority.dto.GateLimitUtagDto;
import com.baomibing.authority.entity.SysGateLimitUtag;
import com.baomibing.authority.mapper.SysGateLimitUtagMapper;
import com.baomibing.authority.service.SysGateLimitUtagService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * SysGateLimitUtagServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysGateLimitUtagServiceImpl extends MBaseServiceImpl<SysGateLimitUtagMapper, SysGateLimitUtag, GateLimitUtagDto> implements SysGateLimitUtagService {

    @Override
    public List<GateLimitUtagDto> listByLimit(String limitId) {
        if (Checker.beEmpty(limitId)) {
            return Lists.newArrayList();
        }
        return mapper(baseMapper.selectList(lambdaQuery().eq(SysGateLimitUtag::getLimitId, limitId)));
    }

    @Override
    public void saveLimitUtag(GateLimitUtagDto limitUtag) {
        saveIt(limitUtag);
    }

    @Override
    public void updateLimitUtag(GateLimitUtagDto limitUtag) {
        updateIt(limitUtag);
    }

    @Override
    public void deleteLimitUtag(Set<String> ids) {
        deletes(ids);
    }

    @Override
    public void deleteByLimit(String limitId) {
        Assert.CheckArgument(limitId);
        baseMapper.delete(lambdaQuery().eq(SysGateLimitUtag::getLimitId, limitId));
    }
}
