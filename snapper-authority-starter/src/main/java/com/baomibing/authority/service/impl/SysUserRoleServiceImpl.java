
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

import com.baomibing.authority.dto.RoleDto;
import com.baomibing.authority.dto.UserDto;
import com.baomibing.authority.dto.UserGroupDto;
import com.baomibing.authority.dto.UserRoleDto;
import com.baomibing.authority.entity.SysRole;
import com.baomibing.authority.entity.SysUser;
import com.baomibing.authority.entity.SysUserRole;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.mapper.SysUserRoleMapper;
import com.baomibing.authority.service.SysRoleResourceService;
import com.baomibing.authority.service.SysRoleService;
import com.baomibing.authority.service.SysUserRoleService;
import com.baomibing.authority.service.SysUserService;
import com.baomibing.core.common.Assert;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ConvertUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户角色服务
 * 
 * @author zening
 * @since 1.0.0
 */
@Service
public class SysUserRoleServiceImpl extends MBaseServiceImpl<SysUserRoleMapper, SysUserRole, UserRoleDto>
		implements SysUserRoleService {

	@Autowired private SysRoleService roleService;
	@Autowired private SysUserService userService;
	@Autowired private SysRoleResourceService roleResourceService;
	

	@Transactional
	@Override
	public void deleteByGroupAndUsers(String orgId, Set<String> uids) {
		Assert.CheckArgument(uids);
		Assert.CheckArgument(orgId);
		baseMapper.deleteByGroupAndUsers(orgId, uids);
	}

	@Override
	public List<RoleDto> listRolesByGroupAndUser(String orgId, String userId) {
		List<RoleDto> list = new ArrayList<>();
		if (Checker.beEmpty(userId) || Checker.beEmpty(orgId)) {
			return list;
		}
		List<SysRole> roleList = this.baseMapper.listRolesByGroupAndUser(orgId, userId);
		if (Checker.beEmpty(roleList)) {
			return list;
		}

		list = new ArrayList<>(this.collectionMapper.mapCollection(roleList, RoleDto.class));
		return list;

	}


	@Transactional
	@Override
	public void saveFromRole(String roleId, List<UserGroupDto> userGroups) {
		Assert.CheckArgument(roleId);
		Assert.CheckArgument(userGroups);
//		RoleDto role = roleService.getIt(roleId);
//		assertBeLock(role);
		// 检查userId合法性
		Assert.CheckArgument(roleService.getIt(roleId), ExceptionEnum.OBJECT_IS_NULL, roleId);
		if (Checker.beEmpty(userGroups)) {
			this.baseMapper.deleteByRole(roleId);
			return;
		}
		// 根据userGroups中的groupId分组 key为groupId, value为用户ID列表
		Map<String, Set<String>> map = userGroups.stream()
				.collect(Collectors.groupingBy(UserGroupDto::getGroupId, Collectors.mapping(UserGroupDto::getUserId, Collectors.toSet())));

		// 删除角色组织对应的用户角色关系
		this.baseMapper.deleteByRoleAndGroups(roleId, map.keySet());

		List<UserRoleDto> toAdds = Lists.newArrayList();
		map.forEach((k, v) -> {
			v.forEach(u -> toAdds.add(new UserRoleDto().setRoleId(roleId).setOrgId(k).setUserId(u)));
		});
		
		if (Checker.beNotEmpty(toAdds)) {
			super.saveItBatch(toAdds);
		}
		// 刷新权限
//		roleResourceService.refreshPrivileges();
	}

	@Transactional
	@Override
	public void saveFromUser(String orgId, String userId, List<String> roleIdList) {
		Assert.CheckArgument(userId);
		Assert.CheckArgument(orgId);
		UserDto user = userService.getIt(userId);
		// 检查userId合法性
		Assert.CheckArgument(user, ExceptionEnum.OBJECT_IS_NULL, userId);
//		assertBeLock(user);
		if (Checker.beEmpty(roleIdList)) {
			this.baseMapper.deleteByGroupAndUser(orgId, userId);
			return;
		}

		Set<String> idSet = ConvertUtil.toSet(roleIdList);
		// 检查角色id列表是否都合法
		List<RoleDto> roleList = roleService.gets(idSet);
		if (roleList.size() != idSet.size()) {
			throw new ServerRuntimeException(AuthorizationExceptionEnum.CONTAINS_INVALID_ID_IN_LIST);
		}
//		for (RoleDto r : roleList) {
//			assertBeLock(r);
//		}

		// 查找之前分配的
		List<RoleDto> assignedRoleList = this.listRolesByGroupAndUser(orgId, userId);
		// 剔除掉超级管理员
		assignedRoleList.removeIf(RoleDto::getBeSuper);
		List<String> assignedRoleIdList = assignedRoleList.stream().map(RoleDto::getId).collect(Collectors.toList());
		/*
		 * 处理规则为： 
		 * 以当前分配的角色列表为全集，以之前分配的角色列表为子集，进行补集运算，运算结果为需要添加的用户角色关系
		 * 以之前分配的角色列表为全集，以当前分配的角色列表为子集，进行补集运算，运算结果为需要删除的用户角色关系
		 */
		List<String> toAddRoleIdList = ListUtils.subtract(Lists.newArrayList(idSet), assignedRoleIdList);
		List<String> toDeleteRoleIdList = ListUtils.subtract(assignedRoleIdList, Lists.newArrayList(idSet));

		if (Checker.beNotEmpty(toDeleteRoleIdList)) {
			Map<String, Object> map = new HashMap<>();
			map.put("userId", userId);
			map.put("orgId", orgId);
			map.put("roleIdList", toDeleteRoleIdList);
			this.baseMapper.deleteByMapUserIdAndRoleIdList(map);
		}

		List<UserRoleDto> userRoles = Lists.newArrayList();
		for (String id : toAddRoleIdList) {
			UserRoleDto ur = new UserRoleDto();
			ur.setOrgId(orgId);
			ur.setUserId(userId);
			ur.setRoleId(id);
			userRoles.add(ur);
		}
		if (Checker.beNotEmpty(userRoles)) {
			super.saveItBatch(userRoles);
		}
		// 刷新权限
//		roleResourceService.refreshPrivileges();


	}

//	@Override
//	public List<UserDto> listUsersByGroupAndRole(String orgId, String roleId) {
//		List<UserDto> list = new ArrayList<>(0);
//		// 验证参数
//		if (Checker.beEmpty(roleId) || Checker.beEmpty(orgId)) {
//			return list;
//		}
//		List<SysUser> userList = this.baseMapper.listUserByGroupAndRole(orgId, roleId);
//		if (Checker.beEmpty(userList)) {
//			return list;
//		}
//		list = new ArrayList<>(this.collectionMapper.mapCollection(userList, UserDto.class));
//		return list;
//	}

//	@Transactional
//	@Override
//	public void deleteByGroupAndRole(String orgId, String roleId) {
//		Assert.CheckArgument(roleId);
//		Assert.CheckArgument(orgId);
//		baseMapper.deleteByGroupAndRole(orgId, roleId);
//	}
//
//	@Transactional
//	@Override
//	public void deleteByGroupAndRoles(String orgId, Set<String> roleIds){
//		Assert.CheckArgument(roleIds);
//		Assert.CheckArgument(orgId);
//		baseMapper.deleteByGroupAndRoles(orgId, roleIds);
//	}

	@Override
	public void deleteByRoles(Set<String> roleIds) {
		Assert.CheckArgument(roleIds);
		this.baseMapper.delete(lambdaQuery().in(SysUserRole::getRoleId, roleIds));
	}

	@Override
	public void deleteByUsers(Set<String> userIds) {
		Assert.CheckArgument(userIds);
		this.baseMapper.delete(lambdaQuery().in(SysUserRole::getUserId, userIds));
	}

	@Override
	public List<UserDto> listGroupUsersByRoles(String roleId) {
		if (Checker.beEmpty(roleId)) {
			return Lists.newArrayList();
		}
		List<SysUser> userList = this.baseMapper.listGroupUsersByRole(roleId);
		return Checker.beEmpty(userList) ? Lists.newArrayList()
				: new ArrayList<>(this.collectionMapper.mapCollection(userList, UserDto.class));
	}

	@Override
	public List<UserRoleDto> listByGroupAndUsers(String orgId, Set<String> userIds) {
		List<UserRoleDto> list = Lists.newArrayList();
		if (Checker.beEmpty(orgId) || Checker.beEmpty(userIds)) {
			return list;
		}
		return mapper(baseMapper.selectList(lambdaQuery().eq(SysUserRole::getOrgId, orgId).in(SysUserRole::getUserId, userIds)));
	}

    @Override
    public void doCopyUserRole(String uid, String gid, String toUserId) {
        Assert.CheckArgument(uid, gid, toUserId);
        //查询用户角色
        List<UserRoleDto> urs =  this.listByGroupAndUsers(gid, Sets.newHashSet(uid));
        if (Checker.beEmpty(urs)) {
            return;
        }
        //查询目标用户角色
        List<UserRoleDto> toUserRoles =  this.listByGroupAndUsers(gid, Sets.newHashSet(toUserId));
        Set<String> toUserRoleIds = toUserRoles.stream().map(UserRoleDto::getRoleId).collect(Collectors.toSet());
        List<UserRoleDto> newUserRoles = Lists.newArrayList();
        urs.forEach(userRole -> {
            if (!toUserRoleIds.contains(userRole.getRoleId())) {
                newUserRoles.add(new  UserRoleDto().setRoleId(userRole.getRoleId()).setUserId(toUserId).setOrgId(userRole.getOrgId()));
            }
        });
        if (Checker.beNotEmpty(newUserRoles)) {
            saveItBatch(newUserRoles);
        }
    }

}
