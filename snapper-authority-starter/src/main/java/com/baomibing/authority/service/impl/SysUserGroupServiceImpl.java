
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

import com.baomibing.authority.dto.UserGroupDto;
import com.baomibing.authority.entity.SysUserGroup;
import com.baomibing.authority.mapper.SysUserGroupMapper;
import com.baomibing.authority.service.*;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * 用户组织服务
 * 
 * @author zening
 * @since 1.0.0
 */
@Service
public class SysUserGroupServiceImpl extends MBaseServiceImpl<SysUserGroupMapper, SysUserGroup, UserGroupDto> implements SysUserGroupService {

	@Override
	public List<UserGroupDto> listByUser(String userId) {
		if (Checker.beEmpty(userId)) {
			return Lists.newArrayList();
		}
		return mapper(this.baseMapper.listByUser(userId));
			
	}

	@Override
	public List<UserGroupDto> listItAndChilldByGroup(String groupId) {
		if (Checker.beEmpty(groupId)) {
			return Lists.newArrayList();
		}
		return mapper(this.baseMapper.listItAndChilldByGroup(groupId));
	}


	@Override
	public List<UserGroupDto> listByUsersAndGroup(Set<String> users, String gid) {
		return mapper(baseMapper.selectList(lambdaQuery().eq(SysUserGroup::getGroupId, gid).in(SysUserGroup::getUserId, users)));
	}

	@Transactional
	@Override
	public void deleteByGroupIdAndUsers(Set<String> users, String gid) {
		baseMapper.delete(lambdaQuery().eq(SysUserGroup::getGroupId, gid).in(SysUserGroup::getUserId, users));
	}

	@Transactional
	@Override
	public void deleteByGroups(Set<String> gids) {
		baseMapper.delete(lambdaQuery().in(SysUserGroup::getGroupId, gids));
	}

	@Transactional
	@Override
	public void deleteByUsers(Set<String> uids) {
		baseMapper.delete(lambdaQuery().in(SysUserGroup::getUserId, uids));
	}

	@Override
	public String getGroupIdByUserNo(String userNo) {
		List<String> strings = super.baseMapper.listGroupIdByUserNo(userNo);
		if (Checker.beEmpty(strings)){
			return Strings.EMPTY;
		}
		return strings.get(0);
	}

	@Override
	public List<UserGroupDto> listByUserNos(Set<String> userNos) {
		if (Checker.beEmpty(userNos)) {
			return emptyList();
		}
		return mapper(baseMapper.listByUserNos(Lists.newArrayList(userNos)));
	}
}
