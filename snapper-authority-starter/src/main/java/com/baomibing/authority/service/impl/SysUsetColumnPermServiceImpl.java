
package com.baomibing.authority.service.impl;

import com.baomibing.authority.dto.UsetColumnPermDto;
import com.baomibing.authority.entity.SysUsetColumnPerm;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.mapper.SysUsetColumnPermMapper;
import com.baomibing.authority.service.SysUsetColumnPermService;
import com.baomibing.core.common.Assert;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * SysUsetColumnPermServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysUsetColumnPermServiceImpl extends MBaseServiceImpl<SysUsetColumnPermMapper, SysUsetColumnPerm, UsetColumnPermDto> implements SysUsetColumnPermService {

    @Override
    public void saveColumnPerm(UsetColumnPermDto perm) {
        throw new ServerRuntimeException(AuthorizationExceptionEnum.THE_VERSION_CAN_NOT_HAVE_THIS_FUNCTION);
//        Assert.CheckArgument(perm);
//        Assert.CheckArgument(perm.getPermId());
//        deleteUsetColumnPerm(perm.getUsetId(), perm.getPermId());
//        if (Checker.beNotEmpty(perm.getTableColumns())) {
//            perm.setColumnExpress(JSONArray.toJSONString(perm.getTableColumns()));
//            saveIt(perm);
//        }
    }

    @Override
    public UsetColumnPermDto getUsetColumnPerm(String usetId, String permId) {
        if (Checker.beEmpty(usetId) || Checker.beEmpty(permId)) {
            return null;
        }
        return mapper2v(baseMapper.selectOne(lambdaQuery().eq(SysUsetColumnPerm::getPermId, permId).eq(SysUsetColumnPerm::getUsetId, usetId)));
    }

    @Override
    public List<UsetColumnPermDto> listUsetColumnPerm(Set<String> usetIds, String permId) {
        if (Checker.beEmpty(usetIds) || Checker.beEmpty(permId)) {
            return Lists.newArrayList();
        }
        return mapper(baseMapper.selectList(lambdaQuery().eq(SysUsetColumnPerm::getPermId, permId).in(SysUsetColumnPerm::getUsetId, usetIds)));
    }

    @Override
    public void deleteUsetColumnPerm(String usetId, String permId) {
        Assert.CheckArgument(usetId, permId);
        baseMapper.delete(lambdaQuery().eq(SysUsetColumnPerm::getPermId, permId).eq(SysUsetColumnPerm::getUsetId, usetId));
    }
}
