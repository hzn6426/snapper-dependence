
package com.baomibing.authority.service.impl;

import com.baomibing.authority.dto.GroupDto;
import com.baomibing.authority.dto.GroupEntrustDto;
import com.baomibing.authority.entity.SysGroup;
import com.baomibing.authority.entity.SysGroupEntrust;
import com.baomibing.authority.mapper.SysGroupEntrustMapper;
import com.baomibing.authority.service.SysGroupEntrustService;
import com.baomibing.core.annotation.NotAuthAop;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 委托组织实现类
 *
 * @author zening
 * @since 1.0.0
 */
@Service
public class SysGroupEntrustServiceImpl extends MBaseServiceImpl<SysGroupEntrustMapper, SysGroupEntrust, GroupEntrustDto>
implements SysGroupEntrustService {
    
    @NotAuthAop
    @Override
    public List<GroupDto> listEntrustGroupsByGroupAndUserAndPerm(String orgId, String userId, String permId) {
        if(Checker.beNull(permId) || Checker.beEmpty(userId)) {
            return emptyList();
        }
        List<SysGroup> list = this.baseMapper.listEntrustGroupsByGroupAndUserAndPerm(orgId, userId, permId);
        return Checker.beEmpty(list) ? emptyList() : new ArrayList<>(this.collectionMapper.mapCollection(list, GroupDto.class));
    }
    
    @Transactional
    @Override
    public void deleteByGroupAndUserAndPerm(String orgId, String userId, String permId) {
        Assert.CheckArgument(userId);
        Assert.CheckArgument(permId);
        this.baseMapper.deleteByGroupAndUserAndPerm(orgId, userId, permId);

    }
    
    
}
