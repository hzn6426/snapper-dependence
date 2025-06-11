package com.baomibing.authority.service.impl;

import com.baomibing.authority.dto.GateLimitStagDto;
import com.baomibing.authority.entity.SysGateLimitStag;
import com.baomibing.authority.mapper.SysGateLimitStagMapper;
import com.baomibing.authority.service.SysGateLimitStagService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * SysGateLimitStagServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysGateLimitStagServiceImpl extends MBaseServiceImpl<SysGateLimitStagMapper, SysGateLimitStag, GateLimitStagDto> implements SysGateLimitStagService {

    @Override
    public List<GateLimitStagDto> listByLimit(String limitId) {
        if (Checker.beEmpty(limitId)) {
            return Lists.newArrayList();
        }
        return mapper(baseMapper.selectList(lambdaQuery().eq(SysGateLimitStag::getLimitId, limitId)));
    }

    @Override
    public void saveLimitStag(GateLimitStagDto limitStag) {
        saveIt(limitStag);
    }

    @Override
    public void updateLimitStag(GateLimitStagDto limitStag) {
        updateIt(limitStag);
    }

    @Override
    public void deleteLimitStag(Set<String> ids) {
        deletes(ids);
    }

    @Override
    public void deleteByLimit(String limitId) {
        Assert.CheckArgument(limitId);
        baseMapper.delete(lambdaQuery().eq(SysGateLimitStag::getLimitId, limitId));
    }
}
