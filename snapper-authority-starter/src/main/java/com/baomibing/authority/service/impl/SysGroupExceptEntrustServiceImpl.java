
/*
 * Copyright (c) 2020-2025, zening (316279828@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.baomibing.authority.service.impl;

import com.baomibing.authority.dto.GroupDto;
import com.baomibing.authority.dto.GroupExceptEntrustDto;
import com.baomibing.authority.entity.SysGroup;
import com.baomibing.authority.entity.SysGroupExceptEntrust;
import com.baomibing.authority.mapper.SysGroupExceptEntrustMapper;
import com.baomibing.authority.service.SysGroupExceptEntrustService;
import com.baomibing.core.annotation.NotAuthAop;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 委托组织实现类
 * @author zening
 * @version 1.0.0
 */
@Service
public class SysGroupExceptEntrustServiceImpl extends MBaseServiceImpl<SysGroupExceptEntrustMapper, SysGroupExceptEntrust, GroupExceptEntrustDto>
	implements SysGroupExceptEntrustService {

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

    @Override
    public List<GroupExceptEntrustDto> listUserGroupExceptEntrusts(Set<String> userIds, String orgId) {
        if (Checker.beEmpty(userIds) || Checker.beEmpty(orgId)) {
            return Lists.newArrayList();
        }
        return mapper(baseMapper.selectList(lambdaQuery().in(SysGroupExceptEntrust::getUserId, userIds).eq(SysGroupExceptEntrust::getOrgId, orgId)));
    }

    @Override
    public void deleteUserExceptGroupEntrusts(String orgId, Set<String> userIds) {
        Assert.CheckArgument(userIds);
        Assert.CheckArgument(orgId);
        baseMapper.delete(lambdaQuery().eq(SysGroupExceptEntrust::getOrgId, orgId).eq(SysGroupExceptEntrust::getUserId, userIds));
    }

    @Override
    public void doCopyUserExceptGroupEntrusts(String uid, String gid, String toUserId) {
        Assert.CheckArgument(uid, gid, toUserId);
        List<GroupExceptEntrustDto> groupExceptEntrusts = listUserGroupExceptEntrusts(Sets.newHashSet(uid), gid);

        if (Checker.beEmpty(groupExceptEntrusts)) {
            return;
        }
        List<GroupExceptEntrustDto> toUserGroupExceptEntrusts = listUserGroupExceptEntrusts(Sets.newHashSet(toUserId), gid);
        Set<String> exceptGroupIds = toUserGroupExceptEntrusts.stream().map(GroupExceptEntrustDto::getGroupExceptEntrustId).collect(Collectors.toSet());
        List<GroupExceptEntrustDto> newGroupExceptEntrusts = Lists.newArrayList();

        for (GroupExceptEntrustDto groupExceptEntrust : groupExceptEntrusts) {
            if (!exceptGroupIds.contains(groupExceptEntrust.getGroupExceptEntrustId())) {
                newGroupExceptEntrusts.add(groupExceptEntrust.setUserId(toUserId).setId(null));
            }
        }

        if (Checker.beNotEmpty(newGroupExceptEntrusts)) {
            saveItBatch(newGroupExceptEntrusts);
        }
    }

	
	

}
