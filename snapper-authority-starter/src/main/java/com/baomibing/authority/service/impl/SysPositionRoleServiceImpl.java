
package com.baomibing.authority.service.impl;

import com.baomibing.authority.dto.PositionRoleDto;
import com.baomibing.authority.dto.RoleDto;
import com.baomibing.authority.entity.SysPositionRole;
import com.baomibing.authority.entity.SysRole;
import com.baomibing.authority.mapper.SysPositionRoleMapper;
import com.baomibing.authority.service.SysPositionRoleService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 职位角色关系
 * 
 * @author zening
 * @since 1.0.0
 */
@Service
public class SysPositionRoleServiceImpl extends MBaseServiceImpl<SysPositionRoleMapper, SysPositionRole, PositionRoleDto> implements SysPositionRoleService {

	@Override
	public List<RoleDto> listRolesByPosition(String positionId) {
		if (Checker.beEmpty(positionId)) {
			return Lists.newArrayList();
		}
		List<SysRole> roles = this.baseMapper.listRolesByPosition(positionId);
		return Checker.beEmpty(roles) ? Lists.newArrayList()
				: Lists.newArrayList(this.collectionMapper.mapCollection(roles, RoleDto.class));
	}

	@Override
	public void savePositionRoles(String positionId, Set<String> roles) {
		Assert.CheckArgument(positionId);
		deleteByPosition(positionId);
		List<PositionRoleDto> prs = Lists.newArrayList();
		roles.forEach(r -> prs.add(new PositionRoleDto().setRoleId(r).setPositionId(positionId)));
		if (Checker.beNotEmpty(prs)) {
			super.saveItBatch(prs);
		}
	}

	@Override
	public void deleteByPosition(String positionId) {
		Assert.CheckArgument(positionId);
		this.baseMapper.delete(lambdaQuery().eq(SysPositionRole::getPositionId, positionId));
	}

	@Override
	public void deleteByRoles(Set<String> roles) {
		Assert.CheckArgument(roles);
		this.baseMapper.delete(lambdaQuery().in(SysPositionRole::getRoleId, roles));
	}
}
