
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


import com.baomibing.authority.dto.UserEntrustDto;
import com.baomibing.authority.entity.SysUserEntrust;
import com.baomibing.authority.mapper.SysUserEntrustMapper;
import com.baomibing.authority.service.SysUserEntrustService;
import com.baomibing.core.annotation.NotAuthAop;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 委托用户服务实现类
 * 
 * @author zening
 * @since 1.0.0
 */
@Service
public class SysUserEntrustServiceImpl extends MBaseServiceImpl<SysUserEntrustMapper, SysUserEntrust, UserEntrustDto>
	implements SysUserEntrustService {

	@Transactional
	@NotAuthAop
	@Override
	public List<String> listEntrustUserIdsByGroupAndUserAndPerm(String orgId, String userId, String permId) {
		if (Checker.beEmpty(userId) || Checker.beNull(permId) || Checker.beEmpty(orgId))
			return Lists.newArrayList();
		return this.baseMapper.listEntrustUserIdsByGroupAndUserAndPerm(orgId, userId, permId);
	}

	@Transactional
	@NotAuthAop
	@Override
	public List<String> listEntrustUserCodesByGroupAndUserAndPerm(String orgId, String userId, String permId) {
		if (Checker.beEmpty(userId) || Checker.beNull(permId))
			return Lists.newArrayList();
		return this.baseMapper.listEntrustUserCodesByGroupAndUserAndPerm(orgId, userId, permId);
	}

	@Transactional
	@Override
	public void deleteByGroupAndUserAndPerm(String orgId, String userId, String permId) {
		Assert.CheckArgument(userId);
		Assert.CheckArgument(permId);
		baseMapper.deleteByGroupAndUserAndPerm(orgId, userId, permId);

	}

    @Override
    public List<UserEntrustDto> listUserEntrusts(Set<String> userIds, String orgId) {
        if (Checker.beEmpty(userIds) || Checker.beNull(orgId)) {
            return Lists.newArrayList();
        }

        return mapper(baseMapper.selectList(lambdaQuery().in(SysUserEntrust::getUserId, userIds).eq(SysUserEntrust::getOrgId, orgId)));
    }

    @Override
    public void deleteUserEntrusts(String orgId, Set<String> userIds) {
        Assert.CheckArgument(userIds);
        Assert.CheckArgument(orgId);
        baseMapper.delete(lambdaQuery().eq(SysUserEntrust::getOrgId, orgId).in(SysUserEntrust::getUserId, userIds));
    }

    @Override
    public void doCopyUserEntrusts(String uid, String gid, String toUserId) {
        Assert.CheckArgument(uid, gid, toUserId);
        List<UserEntrustDto> userEntrusts = listUserEntrusts(Sets.newHashSet(uid), gid);

        if (Checker.beEmpty(userEntrusts)) {
            return;
        }

        List<UserEntrustDto> toUserEntrusts = listUserEntrusts(Sets.newHashSet(toUserId), gid);
        Set<String> entrustIds = toUserEntrusts.stream().map(UserEntrustDto::getUserEntrustId).collect(Collectors.toSet());

        List<UserEntrustDto> newUserEntrusts = Lists.newArrayList();
        for (UserEntrustDto userEntrust : userEntrusts) {
            if (!entrustIds.contains(userEntrust.getUserEntrustId())) {
                newUserEntrusts.add(userEntrust.setUserId(toUserId).setId(null));
            }
        }

        if (Checker.beNotEmpty(newUserEntrusts)) {
            saveItBatch(newUserEntrusts);
        }
    }
	
}
