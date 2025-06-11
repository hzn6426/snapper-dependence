
package com.baomibing.authority.service.impl;

import com.baomibing.authority.dto.UsetRoleDto;
import com.baomibing.authority.entity.SysUsetRole;
import com.baomibing.authority.mapper.SysUsetRoleMapper;
import com.baomibing.authority.service.SysUsetRoleService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SysUsetRoleServiceImpl
 *
 * @author zening
 * @version 1.0.0
 */
@Service
public class SysUsetRoleServiceImpl extends MBaseServiceImpl<SysUsetRoleMapper, SysUsetRole, UsetRoleDto> implements SysUsetRoleService {

    @Override
    public void saveFromUset(String usetId, Set<String> roleIds) {
//        throw new ServerRuntimeException(AuthorizationExceptionEnum.THE_VERSION_CAN_NOT_HAVE_THIS_FUNCTION);
        Assert.CheckArgument(usetId);
        //删除于当前用户组关联的所有角色
        this.baseMapper.delete(lambdaQuery().eq(SysUsetRole::getUsetId, usetId));
        List<UsetRoleDto> urs = Lists.newArrayList();
        roleIds.forEach(r -> urs.add(new UsetRoleDto().setUsetId(usetId).setRoleId(r)));
        //添加用户组角色关联
        if (Checker.beNotEmpty(urs)) {
            super.saveItBatch(urs);
        }
    }

    @Override
    public Set<String> listRolesByUset(String usetId) {
        if (Checker.beEmpty(usetId)) {
            return Sets.newHashSet();
        }
        List<SysUsetRole> urs = baseMapper.selectList(lambdaQuery().eq(SysUsetRole::getUsetId, usetId));
        return urs.stream().map(SysUsetRole::getRoleId).collect(Collectors.toSet());
    }

    @Override
    public void deleteByUsets(Set<String> usetIds) {
        Assert.CheckArgument(usetIds);
        baseMapper.delete(lambdaQuery().in(SysUsetRole::getUsetId, usetIds));
    }
}
