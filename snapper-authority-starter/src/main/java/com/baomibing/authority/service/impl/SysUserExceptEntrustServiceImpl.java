
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


import com.baomibing.authority.dto.UserExceptEntrustDto;
import com.baomibing.authority.entity.SysUserExceptEntrust;
import com.baomibing.authority.mapper.SysUserExceptEntrustMapper;
import com.baomibing.authority.service.SysUserExceptEntrustService;
import com.baomibing.core.annotation.NotAuthAop;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 委托用户服务实现类
 * @author zening
 * @version 1.0.0
 */
@Service
public class SysUserExceptEntrustServiceImpl extends MBaseServiceImpl<SysUserExceptEntrustMapper, SysUserExceptEntrust, UserExceptEntrustDto>
	implements SysUserExceptEntrustService {

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
		if (Checker.beEmpty(userId) || Checker.beNull(permId)) {
			return emptyList();
		}
		return this.baseMapper.listEntrustUserCodesByGroupAndUserAndPerm(orgId, userId, permId);
	}

	@Transactional
	@Override
	public void deleteByGroupAndUserAndPerm(String orgId, String userId, String permId) {
		Assert.CheckArgument(userId);
		Assert.CheckArgument(permId);
		this.baseMapper.deleteByGroupAndUserAndPerm(orgId, userId, permId);
		
	}

    @Override
    public List<UserExceptEntrustDto> listUserExceptEntrusts(Set<String> userIds, String orgId) {
        if (Checker.beEmpty(userIds) || Checker.beNull(orgId)) {
            return Collections.emptyList();
        }
        return mapper(baseMapper.selectList(lambdaQuery().in(SysUserExceptEntrust::getUserId, userIds).eq(SysUserExceptEntrust::getOrgId, orgId)));
    }

    @Override
    public void deleteUserExceptEntrusts(String orgId, Set<String> userIds) {
        Assert.CheckArgument(userIds);
        Assert.CheckArgument(orgId);
        baseMapper.delete(lambdaQuery().eq(SysUserExceptEntrust::getOrgId, orgId).eq(SysUserExceptEntrust::getUserId, userIds));
    }

    @Override
    public void doCopyUserExceptEntrusts(String uid, String gid, String toUserId) {
        Assert.CheckArgument(uid, gid, toUserId);
        List<UserExceptEntrustDto> userExceptEntrusts = listUserExceptEntrusts(Sets.newHashSet(uid), gid);

        if (Checker.beEmpty(userExceptEntrusts)) {
            return;
        }

        List<UserExceptEntrustDto> toUserExceptEntrusts = listUserExceptEntrusts(Sets.newHashSet(toUserId), gid);
        Set<String> exceptIds = toUserExceptEntrusts.stream().map(UserExceptEntrustDto::getUserExceptEntrustId).collect(Collectors.toSet());
        List<UserExceptEntrustDto> newUserExceptEntrusts = Lists.newArrayList();

        for (UserExceptEntrustDto exceptDto : userExceptEntrusts) {
            if (!exceptIds.contains(exceptDto.getUserExceptEntrustId())) {
                newUserExceptEntrusts.add(exceptDto.setUserId(toUserId).setId(null));
            }
        }

        if (Checker.beNotEmpty(newUserExceptEntrusts)) {
            saveItBatch(newUserExceptEntrusts);
        }
    }

	
}
