
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

import com.alibaba.fastjson.JSONArray;
import com.baomibing.authority.dto.UserColumnPermDto;
import com.baomibing.authority.entity.SysUserColumnPerm;
import com.baomibing.authority.mapper.SysUserColumnPermMapper;
import com.baomibing.authority.service.SysUserColumnPermService;
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
 * SysUserColumnPermServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysUserColumnPermServiceImpl extends MBaseServiceImpl<SysUserColumnPermMapper, SysUserColumnPerm, UserColumnPermDto> implements SysUserColumnPermService {

    @Override
    public void saveColumnPerm(UserColumnPermDto perm) {
        Assert.CheckArgument(perm);
        Assert.CheckArgument(perm.getPermId());
        deleteUserColumnPerm(perm.getUserId(), perm.getOrgId(), perm.getPermId());
        if (Checker.beNotEmpty(perm.getTableColumns())) {
            perm.setColumnExpress(JSONArray.toJSONString(perm.getTableColumns()));
            saveIt(perm);
        }

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

    @Override
    public List<UserColumnPermDto> listUserColumnPerms(Set<String> userIds, String orgId) {
        if (Checker.beEmpty(orgId) || Checker.beEmpty(userIds)) {
            return Lists.newArrayList();
        }
        return mapper(baseMapper.selectList(lambdaQuery().in(SysUserColumnPerm::getUserId, userIds).eq(SysUserColumnPerm::getOrgId, orgId)));
    }

    @Override
    public void deleteUserColumnPerms(String orgId, Set<String> userIds) {
        Assert.CheckArgument(orgId, userIds);
        baseMapper.delete(lambdaQuery().eq(SysUserColumnPerm::getOrgId, orgId).in(SysUserColumnPerm::getUserId, userIds));
    }

    @Override
    public void doCopyUserColumnPerms(String uid, String gid, String toUserId) {
        Assert.CheckArgument(uid, gid, toUserId);
        List<UserColumnPermDto> columnPerms = listUserColumnPerms(Sets.newHashSet(uid), gid);
        if (Checker.beEmpty(columnPerms)) {
            return;
        }

        List<UserColumnPermDto> toUserColumnPerms = listUserColumnPerms(Sets.newHashSet(toUserId), gid);
        Set<String> permIds = toUserColumnPerms.stream().map(UserColumnPermDto::getPermId).collect(Collectors.toSet());
        List<UserColumnPermDto> newColumnPerms = Lists.newArrayList();
        for (UserColumnPermDto columnPerm : columnPerms) {
            if (!permIds.contains(columnPerm.getPermId())) {
                newColumnPerms.add(columnPerm.setUserId(toUserId).setId(null));
            }

        }

        if (Checker.beNotEmpty(newColumnPerms)) {
            super.saveItBatch(newColumnPerms);
        }
    }
}
