
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
import com.baomibing.authority.dto.PositionGroupEntrustDto;
import com.baomibing.authority.entity.SysGroup;
import com.baomibing.authority.entity.SysPositionGroupEntrust;
import com.baomibing.authority.mapper.SysPositionGroupEntrustMapper;
import com.baomibing.authority.service.SysPositionGroupEntrustService;
import com.baomibing.core.annotation.NotAuthAop;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 职位组织委托
 * 
 * @author zening
 * @since 1.0.0
 */
@Service
public class SysPositionGroupEntrustServiceImpl extends MBaseServiceImpl<SysPositionGroupEntrustMapper, SysPositionGroupEntrust, PositionGroupEntrustDto>
		implements SysPositionGroupEntrustService {

	@NotAuthAop
	@Override
	public List<GroupDto> listEntrustGroupsByPosition(String positionId) {
		if (Checker.beEmpty(positionId)) return Lists.newArrayList();
		List<SysGroup> groups = this.baseMapper.listEntrustGroupsByPosition(positionId);
		return Checker.beEmpty(groups) ? Lists.newArrayList() : Lists.newArrayList(this.collectionMapper.mapCollection(groups, GroupDto.class));
	}

	@Override
	public void deleteByPosition(String positionId) {
		Assert.CheckArgument(positionId);
		this.baseMapper.delete(lambdaQuery().eq(SysPositionGroupEntrust::getPositionId, positionId));
	}

	@Override
	public void deleteByPositions(Set<String> positionIds) {
		Assert.CheckArgument(positionIds);
		this.baseMapper.delete(lambdaQuery().in(SysPositionGroupEntrust::getPositionId, positionIds));
	}
}
