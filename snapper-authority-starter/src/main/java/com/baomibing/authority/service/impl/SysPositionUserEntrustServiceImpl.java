
package com.baomibing.authority.service.impl;

import com.baomibing.authority.dto.PositionUserEntrustDto;
import com.baomibing.authority.dto.UserDto;
import com.baomibing.authority.entity.SysPositionUserEntrust;
import com.baomibing.authority.entity.SysUser;
import com.baomibing.authority.mapper.SysPositionUserEntrustMapper;
import com.baomibing.authority.service.SysPositionUserEntrustService;
import com.baomibing.core.annotation.NotAuthAop;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 职位用户委托服务
 * 
 * @author zening
 * @since 1.0.0
 */
@Service
public class SysPositionUserEntrustServiceImpl extends MBaseServiceImpl<SysPositionUserEntrustMapper, SysPositionUserEntrust, PositionUserEntrustDto>
		implements SysPositionUserEntrustService {

	@NotAuthAop
	@Override
	public List<String> listEntrustUserCodesByPosition(String positionId) {
		List<UserDto> users = listEntrustUsersByPosition(positionId);
		return Checker.beEmpty(users) ? Lists.newArrayList()
				: users.stream().map(UserDto::getUserName).collect(Collectors.toList());
	}

	@Override
	public List<UserDto> listEntrustUsersByPosition(String positionId) {
		if (Checker.beEmpty(positionId)) {
			return Lists.newArrayList();
		}
		List<SysUser> users = this.baseMapper.listEntrustUsersByPosition(positionId);
		return Checker.beEmpty(users) ? Lists.newArrayList()
				: new ArrayList<>(this.collectionMapper.mapCollection(users, UserDto.class));
	}

	@Transactional
	@Override
	public void deleteByUsers(Set<String> uids) {
		Assert.CheckArgument(uids);
		this.baseMapper.delete(lambdaQuery().in(SysPositionUserEntrust::getUserId, uids));
	}

	@Transactional
	@Override
	public void deleteByPosition(String positionId) {
		Assert.CheckArgument(positionId);
		this.baseMapper.delete(lambdaQuery().eq(SysPositionUserEntrust::getPositionId, positionId));

	}

	@Override
	public void deleteByPositions(Set<String> positionIds) {
		Assert.CheckArgument(positionIds);
		this.baseMapper.delete(lambdaQuery().in(SysPositionUserEntrust::getPositionId, positionIds));
	}
}
