
package com.baomibing.authority.service.impl;

import com.baomibing.authority.dto.GroupDto;
import com.baomibing.authority.dto.UsetGroupExceptEntrustDto;
import com.baomibing.authority.entity.SysGroup;
import com.baomibing.authority.entity.SysUsetGroupExceptEntrust;
import com.baomibing.authority.mapper.SysUsetGroupExceptEntrustMapper;
import com.baomibing.authority.service.SysUsetGroupExceptEntrustService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * SysUsetGroupEntrustServiceImpl
 *
 * @author zening
 * @version 1.0.0
 */
@Service
public class SysUsetGroupExceptEntrustServiceImpl extends MBaseServiceImpl<SysUsetGroupExceptEntrustMapper, SysUsetGroupExceptEntrust, UsetGroupExceptEntrustDto> implements SysUsetGroupExceptEntrustService {

    @Override
    public List<GroupDto> listEntrustGroupsByUsetAndPerm(Set<String> usetIds, String permId) {
        if (Checker.beEmpty(usetIds) || Checker.beEmpty(permId)) {
            return emptyList();
        }
        List<SysGroup> list = baseMapper.listEntrustGroupsByUsetAndPerm(usetIds, permId);
        return Checker.beEmpty(list) ? emptyList() : new ArrayList<>(this.collectionMapper.mapCollection(list, GroupDto.class));
    }

    @Override
    public void deleteByUsetAndPerm(String usetId, String permId) {
        Assert.CheckArgument(usetId);
        Assert.CheckArgument(permId);
        baseMapper.deleteByUsetAndPerm(usetId, permId);
    }
}
