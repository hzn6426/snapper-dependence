
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
import com.baomibing.authority.dto.UserDataPermDto;
import com.baomibing.authority.entity.SysUserDataPerm;
import com.baomibing.authority.mapper.SysUserDataPermMapper;
import com.baomibing.authority.service.SysUserDataPermService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        Assert.CheckArgument(perm);
        Assert.CheckArgument(perm.getPermId());
        baseMapper.delete(lambdaQuery().eq(SysUserDataPerm::getOrgId, perm.getOrgId()).eq(SysUserDataPerm::getUserId, perm.getUserId()).eq(SysUserDataPerm::getPermId, perm.getPermId()));
        if (Checker.beNotEmpty(perm.getSearchExpresses())) {
            perm.setPermExpress(JSONArray.toJSONString(perm.getSearchExpresses()));
            super.saveIt(perm);
        }


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

    @Override
    public List<UserDataPermDto> listUserDataPerms(Set<String> userIds, String orgId) {
        if (Checker.beEmpty(userIds) || Checker.beEmpty(orgId)) {
            return Lists.newArrayList();
        }
        return mapper(baseMapper.selectList(lambdaQuery().in(SysUserDataPerm::getUserId, userIds).eq(SysUserDataPerm::getOrgId, orgId)));
    }

    @Override
    public void deleteUserDataPerms(String orgId, Set<String> userIds) {
        Assert.CheckArgument(orgId, userIds);
        baseMapper.delete(lambdaQuery().eq(SysUserDataPerm::getOrgId, orgId).in(SysUserDataPerm::getUserId, userIds));
    }

    @Override
    public void doCopyUserDataPerms(String uid, String gid, String toUserId) {
        Assert.CheckArgument(uid, gid, toUserId);
        List<UserDataPermDto> userDataPerms = listUserDataPerms(Sets.newHashSet(uid), gid);
        if (Checker.beEmpty(userDataPerms)) {
            return;
        }

        List<UserDataPermDto> toUserDataPerms = listUserDataPerms(Sets.newHashSet(toUserId), gid);
        Set<String> permIds = toUserDataPerms.stream().map(UserDataPermDto::getPermId).collect(Collectors.toSet());

        List<UserDataPermDto> newUserDataPerms = Lists.newArrayList();
        for (UserDataPermDto userDataPerm : userDataPerms) {
            if (!permIds.contains(userDataPerm.getPermId())) {
                newUserDataPerms.add(userDataPerm.setId(null).setUserId(toUserId));
            }
        }

        if (Checker.beNotEmpty(newUserDataPerms)) {
            saveItBatch(newUserDataPerms);
        }
    }
}
