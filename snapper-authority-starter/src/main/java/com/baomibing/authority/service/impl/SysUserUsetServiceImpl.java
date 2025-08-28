
package com.baomibing.authority.service.impl;

import com.baomibing.authority.dto.UserDto;
import com.baomibing.authority.dto.UserGroupDto;
import com.baomibing.authority.dto.UserUsetDto;
import com.baomibing.authority.dto.UsetDto;
import com.baomibing.authority.entity.SysUser;
import com.baomibing.authority.entity.SysUserUset;
import com.baomibing.authority.entity.SysUset;
import com.baomibing.authority.mapper.SysUserUsetMapper;
import com.baomibing.authority.service.SysUserService;
import com.baomibing.authority.service.SysUserUsetService;
import com.baomibing.authority.service.SysUsetService;
import com.baomibing.core.common.Assert;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : zening
 * @since: 1.0.0
 */
@Service
public class SysUserUsetServiceImpl extends MBaseServiceImpl<SysUserUsetMapper, SysUserUset, UserUsetDto> implements SysUserUsetService {

	@Autowired private SysUsetService usetService;
	@Autowired private SysUserService userService;

    @Override
    public void deleteByUsers(Set<String> userIds) {
        Assert.CheckArgument(userIds);
        this.baseMapper.delete(lambdaQuery().in(SysUserUset::getUserId, userIds));
        
    }
    
    @Override
    public void deleteByUsets(Set<String> usetIds) {
        Assert.CheckArgument(usetIds);
        this.baseMapper.delete(lambdaQuery().in(SysUserUset::getUsetId, usetIds));
    }

	@Override
	public List<UserDto> listGroupUsersByUset(String usetId) {
		if (Checker.beEmpty(usetId)) {
			return Lists.newArrayList();
		}
		List<SysUser> userList = this.baseMapper.listGroupUsersByUset(usetId);
		return Checker.beEmpty(userList) ? Lists.newArrayList() : new ArrayList<>(this.collectionMapper.mapCollection(userList, UserDto.class));
	}

	@Override
	public void saveFromUset(String usetId, List<UserGroupDto> userGroups) {
		Assert.CheckArgument(usetId);
		UsetDto uset = usetService.getIt(usetId);
		// 检查userId合法性
		Assert.CheckArgument(uset, ExceptionEnum.OBJECT_IS_NULL, usetId);
		if (Checker.beEmpty(userGroups)) {
			this.baseMapper.delete(lambdaQuery().eq(SysUserUset::getUsetId, usetId));
			return;
		}
		// 根据userGroups中的groupId分组 key为groupId, value为用户ID列表
		Map<String, Set<String>> map = userGroups.stream().collect(Collectors.groupingBy(ug -> ug.getGroupId(), Collectors.mapping(UserGroupDto::getUserId, Collectors.toSet())));

		// 删除用户组组织对应的用户角色关系
		this.baseMapper.delete(lambdaQuery().eq(SysUserUset::getUsetId, usetId).in(SysUserUset::getOrgId, map.keySet()));
//		this.baseMapper.deleteByRoleAndGroups(roleId, map.keySet());

		List<UserUsetDto> toAdds = Lists.newArrayList();
		map.forEach((k, v) -> {
			v.forEach(u -> toAdds.add(new UserUsetDto().setUsetId(usetId).setOrgId(k).setUserId(u)));
		});

		if (Checker.beNotEmpty(toAdds)) {
			super.saveItBatch(toAdds);
		}
	}

	@Override
	public List<UsetDto> listUsetsByGroupAndUser(String orgId, String userId) {
		List<UsetDto> list = new ArrayList<>();
		if (Checker.beEmpty(userId) || Checker.beEmpty(orgId)) {
			return list;
		}
		List<SysUset> usetList = this.baseMapper.listUsetsByGroupAndUser(orgId, userId);
		if (Checker.beEmpty(usetList)) {
			return list;
		}

		list = new ArrayList<>(this.collectionMapper.mapCollection(usetList, UsetDto.class));
		return list;
	}

	@Override
	public List<UserUsetDto> listByGroupAndUsers(String orgId, Set<String> userIds) {
		List<UserUsetDto> list = Lists.newArrayList();
		if (Checker.beEmpty(orgId) || Checker.beEmpty(userIds)) {
			return list;
		}
		return mapper(baseMapper.selectList(lambdaQuery().eq(SysUserUset::getOrgId, orgId).in(SysUserUset::getUserId, userIds)));
	}

	@Override
	public List<String> listUsetRoleIdsByUserAndGroup(String userId, String userGroupId) {
		if (Checker.beEmpty(userId) || Checker.beEmpty(userGroupId)) return Lists.newArrayList();
		return this.baseMapper.listUsetRoleIdsByUserAndGroup(userId, userGroupId);
	}
}
