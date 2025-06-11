
package com.baomibing.authority.service.impl;

import com.baomibing.authority.dto.UsetUserExceptEntrustDto;
import com.baomibing.authority.entity.SysUsetUserExceptEntrust;
import com.baomibing.authority.mapper.SysUsetUserExceptEntrustMapper;
import com.baomibing.authority.service.SysUsetUserExceptEntrustService;
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
public class SysUsetUserExceptEntrustServiceImpl extends MBaseServiceImpl<SysUsetUserExceptEntrustMapper, SysUsetUserExceptEntrust, UsetUserExceptEntrustDto> implements SysUsetUserExceptEntrustService {

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
