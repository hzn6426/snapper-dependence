
package com.baomibing.authority.service.impl;

import com.baomibing.authority.dto.UsetDataPermDto;
import com.baomibing.authority.entity.SysUsetDataPerm;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.mapper.SysUsetDataPermMapper;
import com.baomibing.authority.service.SysUsetDataPermService;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * SysUsetDataPermServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysUsetDataPermServiceImpl extends MBaseServiceImpl<SysUsetDataPermMapper, SysUsetDataPerm, UsetDataPermDto> implements SysUsetDataPermService {

    @Override
    public UsetDataPermDto getUsetDataPerm(String usetId, String permId) {
        if (Checker.beEmpty(usetId) || Checker.beEmpty(permId)) {
            return null;
        }
        return mapper2v(baseMapper.selectOne(lambdaQuery().eq(SysUsetDataPerm::getUsetId, usetId).eq(SysUsetDataPerm::getPermId, permId)));
    }

    @Override
    public List<UsetDataPermDto> listUsetDataPerm(Set<String> usetIds, String permId) {
        if (Checker.beEmpty(usetIds) || Checker.beEmpty(permId)) {
            return null;
        }
        return mapper(baseMapper.selectList(lambdaQuery().in(SysUsetDataPerm::getUsetId, usetIds).eq(SysUsetDataPerm::getPermId, permId)));
    }

    @Override
    public void saveUserDataPerm(UsetDataPermDto perm) {
        throw new ServerRuntimeException(AuthorizationExceptionEnum.THE_VERSION_CAN_NOT_HAVE_THIS_FUNCTION);
//        Assert.CheckArgument(perm);
//        Assert.CheckArgument(perm.getPermId());
//        baseMapper.delete(lambdaQuery().eq(SysUsetDataPerm::getUsetId, perm.getUsetId()).eq(SysUsetDataPerm::getPermId, perm.getPermId()));
//        if (Checker.beNotEmpty(perm.getSearchExpresses())) {
//            perm.setPermExpress(JSONArray.toJSONString(perm.getSearchExpresses()));
//            super.saveIt(perm);
//        }
    }
}
