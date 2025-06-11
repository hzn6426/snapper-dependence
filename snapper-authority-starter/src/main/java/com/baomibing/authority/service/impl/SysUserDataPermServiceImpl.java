
package com.baomibing.authority.service.impl;

import com.baomibing.authority.dto.UserDataPermDto;
import com.baomibing.authority.entity.SysUserDataPerm;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.mapper.SysUserDataPermMapper;
import com.baomibing.authority.service.SysUserDataPermService;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * SysUserDataPermServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysUserDataPermServiceImpl extends MBaseServiceImpl<SysUserDataPermMapper, SysUserDataPerm, UserDataPermDto> implements SysUserDataPermService {

    @Override
    public void saveUserDataPerm(UserDataPermDto perm) {
        throw new ServerRuntimeException(AuthorizationExceptionEnum.THE_VERSION_CAN_NOT_HAVE_THIS_FUNCTION);
//        Assert.CheckArgument(perm);
//        Assert.CheckArgument(perm.getPermId());
//        baseMapper.delete(lambdaQuery().eq(SysUserDataPerm::getOrgId, perm.getOrgId()).eq(SysUserDataPerm::getUserId, perm.getUserId()).eq(SysUserDataPerm::getPermId, perm.getPermId()));
//        if (Checker.beNotEmpty(perm.getSearchExpresses())) {
//            perm.setPermExpress(JSONArray.toJSONString(perm.getSearchExpresses()));
//            super.saveIt(perm);
//        }


    }

    @Override
    public UserDataPermDto getUserDataPerm(String userId, String orgId, String permId) {
        if (Checker.beEmpty(userId) || Checker.beEmpty(orgId) || Checker.beEmpty(permId)) {
            return null;
        }
        SysUserDataPerm perm = baseMapper.selectOne(lambdaQuery().eq(SysUserDataPerm::getOrgId, orgId).eq(SysUserDataPerm::getUserId, userId).eq(SysUserDataPerm::getPermId, permId));
        if (Checker.beNotNull(perm)) {
            Date start = perm.getPermStartTime();
            Date end = perm.getPermEndTime();
            if (Checker.beNotNull(start) && Checker.beNotNull(end)) {
                Date now = new Date();
                boolean isOk = now.before(end) && now.after(start);
                if (!isOk) {
                    return null;
                }
            }
        }
        return mapper2v(perm);
    }
}
