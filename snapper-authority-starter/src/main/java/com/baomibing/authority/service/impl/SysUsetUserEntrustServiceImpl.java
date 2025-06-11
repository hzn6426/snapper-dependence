
package com.baomibing.authority.service.impl;

import com.baomibing.authority.dto.UsetUserEntrustDto;
import com.baomibing.authority.entity.SysUsetUserEntrust;
import com.baomibing.authority.mapper.SysUsetUserEntrustMapper;
import com.baomibing.authority.service.SysUsetUserEntrustService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * SysUsetUserEntrustServiceImpl
 *
 * @author zening
 * @version 1.0.0
 */
@Service
public class SysUsetUserEntrustServiceImpl extends MBaseServiceImpl<SysUsetUserEntrustMapper, SysUsetUserEntrust, UsetUserEntrustDto> implements SysUsetUserEntrustService {

    @Override
    public List<String> listEntrustUserCodesByUsetAndPerm(Set<String> usetIds, String permId) {
        if (Checker.beEmpty(usetIds) || Checker.beEmpty(permId)) {
            return Lists.newArrayList();
        }
        return baseMapper.listEntrustUserCodesByUsetAndPerm(usetIds, permId);
    }

    @Override
    public List<String> listEntrustUserIdsByUsetAndPerm(Set<String> usetIds, String permId) {
        if (Checker.beEmpty(usetIds) || Checker.beEmpty(permId)) {
            return Lists.newArrayList();
        }
        return baseMapper.listEntrustUserIdsByUsetAndPerm(usetIds, permId);
    }

    @Override
    public void deleteByUsetAndPerm(String usetId, String permId) {
        Assert.CheckArgument(usetId);
        Assert.CheckArgument(permId);
        baseMapper.deleteByUsetAndPerm(usetId, permId);
    }
}
