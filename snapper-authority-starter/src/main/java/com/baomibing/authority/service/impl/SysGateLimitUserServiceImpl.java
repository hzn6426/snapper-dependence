package com.baomibing.authority.service.impl;

import com.baomibing.authority.dto.GateLimitUserDto;
import com.baomibing.authority.entity.SysGateLimitUser;
import com.baomibing.authority.mapper.SysGateLimitUserMapper;
import com.baomibing.authority.service.SysGateLimitUserService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * SysGateLimitUserServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysGateLimitUserServiceImpl extends MBaseServiceImpl<SysGateLimitUserMapper, SysGateLimitUser, GateLimitUserDto> implements SysGateLimitUserService {

    @Override
    public List<GateLimitUserDto> listByLimit(String limitId) {
        if (Checker.beEmpty(limitId)) {
            return Lists.newArrayList();
        }
        return mapper(baseMapper.selectList(lambdaQuery().eq(SysGateLimitUser::getLimitId, limitId)));
    }

    @Override
    public void saveLimitUser(GateLimitUserDto limitUser) {
        saveIt(limitUser);
    }

    @Override
    public void updateLimitUser(GateLimitUserDto limitUser) {
        updateIt(limitUser);
    }

    @Override
    public void deleteLimitUser(Set<String> limitUserIds) {
        deletes(limitUserIds);
    }

    @Override
    public void deleteByLimit(String limitId) {
        Assert.CheckArgument(limitId);
        baseMapper.delete(lambdaQuery().eq(SysGateLimitUser::getLimitId, limitId));
    }
}
