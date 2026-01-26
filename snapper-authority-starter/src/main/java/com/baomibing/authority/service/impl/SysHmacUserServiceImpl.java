
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

import com.alibaba.fastjson.JSONObject;
import com.baomibing.authority.dto.HmacUserDto;
import com.baomibing.authority.dto.UserDto;
import com.baomibing.authority.entity.SysHmacUser;
import com.baomibing.authority.mapper.SysHmacUserMapper;
import com.baomibing.authority.service.SysHmacUserService;
import com.baomibing.authority.service.SysUserService;
import com.baomibing.core.common.Assert;
import com.baomibing.core.common.SearchResult;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.user.UserKey;
import com.baomibing.tool.util.Checker;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.baomibing.tool.constant.RedisKeyConstant.CACHE_HMAC_USER_PREFIX;

/**
 * SysHmacUserServiceImpl
 *
 * @author zening
 * @version 1.0.0
 */
@Service
public class SysHmacUserServiceImpl extends MBaseServiceImpl<SysHmacUserMapper, SysHmacUser, HmacUserDto> implements SysHmacUserService {

    @Autowired private SysUserService userService;

    @Override
    public SearchResult<HmacUserDto> searchHmacUser(HmacUserDto user, int pageNo, int pageSize) {
        int count = baseMapper.countHmacUser(user.getSystemName(), user.getAppId(), user.getUserId(), user.getBindType());
        if (count == 0) {
            return new SearchResult<>(0, Lists.newArrayList());
        }
        List<HmacUserDto> list = mapper(baseMapper.searchHamcUser(user.getSystemName(), user.getAppId(), user.getUserId(), user.getBindType(), offset(pageNo, pageSize), pageSize));
        return new SearchResult<>(count, list);
    }

    @Override
    public void saveHmacUser(HmacUserDto user) {
        Assert.CheckArgument(user);
        if (Checker.beNotEmpty(user.getUserId()) && user.getUserId().contains(Strings.HASH)) {
            String orgId = user.getUserId().split(Strings.HASH)[0];
            String userNo = user.getUserId().split(Strings.HASH)[1];
            if (Checker.beNotEmpty(userNo)) {
                UserDto iuser = userService.getByUserNo(userNo);
                user.setUserId(iuser.getId());
            }
            user.setOrgId(orgId);
        }
        user.setAppId(RandomStringUtils.randomAlphanumeric(20)).setAppKey(RandomStringUtils.randomAlphanumeric(20));
        super.saveIt(user);
    }

    @Override
    public void updateHmacUser(HmacUserDto user) {
        Assert.CheckArgument(user);
        if (Checker.beNotEmpty(user.getUserId()) && user.getUserId().contains(Strings.HASH)) {
            String orgId = user.getUserId().split(Strings.HASH)[0];
            String userNo = user.getUserId().split(Strings.HASH)[1];
            if (Checker.beNotEmpty(userNo)) {
                UserDto iuser = userService.getByUserNo(userNo);
                user.setUserId(iuser.getId());
            }
            user.setOrgId(orgId);
        }
        user.setAppKey(null).setAppId(null);
        super.updateIt(user);
    }

    @Override
    public void deleteHmacUsers(Set<String> ids) {
        Assert.CheckArgument(ids);
        List<HmacUserDto> users = gets(ids);
        List<String> keys = users.stream().map(u -> UserKey.userHmacAppIdKey(u.getAppId())).collect(Collectors.toList());
        cacheService.del(keys);
        super.deletes(ids);
    }

    @Override
    public void refreshCaches(Set<String> ids) {
        Assert.CheckArgument(ids);

        List<HmacUserDto> hmacUsers = mapper(baseMapper.listForCache(Lists.newArrayList(ids)));
        List<HmacUserDto> hmacTenantUsers = mapper(baseMapper.listForTenantCache(Lists.newArrayList(ids)));
        if (Checker.beNotEmpty(hmacTenantUsers)) {
            hmacUsers.addAll(hmacTenantUsers);
        }
        hmacUsers.forEach(u -> {
            if (Checker.beNotEmpty(u.getRoleIds())) {
                Set<String> roles = Sets.newHashSet(Splitter.on(Strings.COMMA).splitToList(u.getRoleIds()));
                u.setRoleIds(String.join(Strings.COMMA, roles));
            }
        });
        hmacUsers.forEach(u -> cacheService.set(UserKey.userHmacAppIdKey(u.getAppId()), JSONObject.toJSONString(u)));
    }

    @Override
    public void refreshPrivileges() {
        cacheService.deleteByKeyPrefix(CACHE_HMAC_USER_PREFIX);
        List<HmacUserDto> hmacUsers = mapper(baseMapper.listForCache(Lists.newArrayList()));
        List<HmacUserDto> hmacTenantUsers = mapper(baseMapper.listForTenantCache(Lists.newArrayList()));
        if (Checker.beNotEmpty(hmacTenantUsers)) {
            hmacUsers.addAll(hmacTenantUsers);
        }
        hmacUsers.forEach(u -> {
            if (Checker.beNotEmpty(u.getRoleIds())) {
                Set<String> roles = Sets.newHashSet(Splitter.on(Strings.COMMA).splitToList(u.getRoleIds()));
                u.setRoleIds(String.join(Strings.COMMA, roles));
            }
        });
        hmacUsers.forEach(u -> cacheService.set(UserKey.userHmacAppIdKey(u.getAppId()), JSONObject.toJSONString(u)));
    }

    @Override
    public HmacUserDto getHmacUser(String id) {
        return mapper2v(baseMapper.getHmacUser(id));
    }
}
