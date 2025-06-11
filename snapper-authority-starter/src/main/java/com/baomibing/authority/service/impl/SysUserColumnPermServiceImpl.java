
package com.baomibing.authority.service.impl;

import com.baomibing.authority.dto.UserColumnPermDto;
import com.baomibing.authority.entity.SysUserColumnPerm;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.mapper.SysUserColumnPermMapper;
import com.baomibing.authority.service.SysUserColumnPermService;
import com.baomibing.core.common.Assert;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import org.springframework.stereotype.Service;

/**
 * SysUserColumnPermServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysUserColumnPermServiceImpl extends MBaseServiceImpl<SysUserColumnPermMapper, SysUserColumnPerm, UserColumnPermDto> implements SysUserColumnPermService {

    @Override
    public void saveColumnPerm(UserColumnPermDto perm) {
        throw new ServerRuntimeException(AuthorizationExceptionEnum.THE_VERSION_CAN_NOT_HAVE_THIS_FUNCTION);
//        Assert.CheckArgument(perm);
//        Assert.CheckArgument(perm.getPermId());
//        deleteUserColumnPerm(perm.getUserId(), perm.getOrgId(), perm.getPermId());
//        if (Checker.beNotEmpty(perm.getTableColumns())) {
//            perm.setColumnExpress(JSONArray.toJSONString(perm.getTableColumns()));
//            saveIt(perm);
//        }

    }

    @Override
    public UserColumnPermDto getUserColumnPerm(String userId, String orgId, String permId) {
        if (Checker.beEmpty(orgId) || Checker.beEmpty(userId) || Checker.beEmpty(permId)) {
            return null;
        }

        return mapper2v(baseMapper.selectOne(lambdaQuery()
                .eq(SysUserColumnPerm::getOrgId, orgId).eq(SysUserColumnPerm::getUserId, userId).eq(SysUserColumnPerm::getPermId, permId)));
    }

    @Override
    public void deleteUserColumnPerm(String userId, String orgId, String permId) {
        Assert.CheckArgument(orgId, userId, permId);
        baseMapper.delete(lambdaQuery()
                .eq(SysUserColumnPerm::getOrgId, orgId).eq(SysUserColumnPerm::getUserId, userId).eq(SysUserColumnPerm::getPermId, permId));
    }
}
