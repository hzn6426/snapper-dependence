
package com.baomibing.authority.service.impl;

import com.baomibing.authority.dto.PositionDto;
import com.baomibing.authority.dto.UserPositionDto;
import com.baomibing.authority.entity.SysPosition;
import com.baomibing.authority.entity.SysUserPosition;
import com.baomibing.authority.mapper.SysUserPositionMapper;
import com.baomibing.authority.service.SysPositionService;
import com.baomibing.authority.service.SysUserPositionService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户职位服务
 * 
 * @author zening
 * @since 1.0.0
 */
@Service
public class SysUserPositionServiceImpl extends MBaseServiceImpl<SysUserPositionMapper, SysUserPosition, UserPositionDto> implements SysUserPositionService {
	 
	@Autowired private SysPositionService positionService;

	@Override
	public List<String> listPositionRoleIdsByUserAndGroup(String userId, String groupId) {
		if (Checker.beEmpty(userId) || Checker.beEmpty(groupId)) return Lists.newArrayList();
		return this.baseMapper.listPositionRoleIdsByUserAndGroup(userId, groupId);
	}

	@Override
	public PositionDto getPositionByUserAndGroup(String userId, String groupId) {
		if (Checker.beEmpty(userId) || Checker.beEmpty(groupId)) return null;
		List<SysPosition> positions = this.baseMapper.listPositionByUserAndGroup(userId, groupId);
		return Checker.beEmpty(positions) ? null : this.beanMapper.map(positions.get(0), PositionDto.class);
	}


	@Transactional
	@Override
	public void removePositionUsers(String gid, Set<String> users){
		Assert.CheckArgument(gid,users);
		List<PositionDto> positionDtos = positionService.listPositionByGroup(gid);
		if (Checker.beNotNull(positionDtos)) {
			baseMapper.delete(lambdaQuery().in(SysUserPosition::getPositionId, positionDtos.stream().map(PositionDto::getId).collect(Collectors.toSet()))
				.in(SysUserPosition::getUserId, users));
		}
	}


	@Transactional
	@Override
	public void deleteByPositions(Set<String> pids){
		Assert.CheckArgument(pids);
		baseMapper.delete(lambdaQuery().in(SysUserPosition::getPositionId, pids));
	}

	@Transactional
	@Override
	public void deleteByUsers(Set<String> uids) {
		Assert.CheckArgument(uids);
		baseMapper.delete(lambdaQuery().in(SysUserPosition::getUserId, uids));
		
	}

	@Override
	public void deleteByGroupAndUsers(String gid, Set<String> users) {
		Assert.CheckArgument(gid);
		Assert.CheckArgument(users);
		this.baseMapper.delete(lambdaQuery().eq(SysUserPosition::getGroupId, gid).in(SysUserPosition::getUserId, users));
	}


	@Override
	public List<String> listUserByPosition(String positionId){
		Assert.CheckArgument(positionId);
		List<SysUserPosition> list = baseMapper.selectList(this.lambdaQuery().eq(SysUserPosition::getPositionId, positionId));
		if (Checker.beEmpty(list)) {
			return new ArrayList<>();
		}
		return list.stream().map(SysUserPosition::getUserId).collect(Collectors.toList());
	}
	
	

	
}
