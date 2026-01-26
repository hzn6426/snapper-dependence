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

import com.baomibing.authority.dto.SysTenantRoleDto;
import com.baomibing.authority.dto.SysTenantUserDto;
import com.baomibing.authority.dto.SysTenantUserRoleDto;
import com.baomibing.authority.dto.UserGroupDto;
import com.baomibing.authority.entity.SysTenantRole;
import com.baomibing.authority.entity.SysTenantUser;
import com.baomibing.authority.entity.SysTenantUserRole;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.mapper.SysTenantUserRoleMapper;
import com.baomibing.authority.service.SysTenantRoleResourceService;
import com.baomibing.authority.service.SysTenantRoleService;
import com.baomibing.authority.service.SysTenantUserRoleService;
import com.baomibing.authority.service.SysTenantUserService;
import com.baomibing.core.common.Assert;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ObjectUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * SysTenantUserRoleServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysTenantUserRoleServiceImpl extends MBaseServiceImpl<SysTenantUserRoleMapper, SysTenantUserRole, SysTenantUserRoleDto> implements SysTenantUserRoleService {

    @Autowired
    private SysTenantRoleService roleService;
    @Autowired
    private SysTenantUserService userService;
    @Autowired
    private SysTenantRoleResourceService roleResourceService;

    @Override
    public List<SysTenantRoleDto> listRolesByGroupAndUserName(String tenantId, String orgId, String userName) {
        List<SysTenantRoleDto> list = Lists.newArrayList();
        // 验证参数
        if (Checker.beEmpty(tenantId) || Checker.beEmpty(orgId) || Checker.beEmpty(userName)) {
            return list;
        }
        // 查询
        List<SysTenantRole> roleList = this.baseMapper.listRolesByGroupAndUserName(tenantId, orgId, userName);
        if (!Checker.beNotEmpty(roleList)) {
            return list;
        }
        // 转换
        return Lists.newArrayList(this.collectionMapper.mapCollection(roleList, SysTenantRoleDto.class));
    }

    @Override
    public void deleteByGroupAndUsers(String tenantId, String orgId, Set<String> uids) {
        Assert.CheckArgument(tenantId);
        Assert.CheckArgument(uids);
        Assert.CheckArgument(orgId);
        this.baseMapper.deleteByGroupAndUsers(tenantId, orgId, uids);
    }

    @Override
    public void deleteByTenant(Set<String> tenantIds) {
        Assert.CheckArgument(tenantIds);
        baseMapper.delete(lambdaQuery().in(SysTenantUserRole::getTenantId, tenantIds));
    }

    @Override
    public List<SysTenantRoleDto> listRolesByGroupAndUser(String tenantId, String orgId, String userId) {
        List<SysTenantRoleDto> list = Lists.newArrayList();
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        if (Checker.beEmpty(userId) || Checker.beEmpty(orgId) || Checker.beEmpty(tenantId)) {
            return list;
        }
        List<SysTenantRole> roleList = this.baseMapper.listRolesByGroupAndUser(tenantId, orgId, userId);
        if (Checker.beEmpty(roleList)) {
            return list;
        }

        return Lists.newArrayList(this.collectionMapper.mapCollection(roleList, SysTenantRoleDto.class));
    }

    @Override
    public List<SysTenantUserRoleDto> listByGroupAndUsers(String tenantId, String orgId, Set<String> userIds) {
        List<SysTenantUserRoleDto> list = Lists.newArrayList();
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        if (Checker.beEmpty(orgId) || Checker.beEmpty(userIds) || Checker.beEmpty(tenantId)) {
            return list;
        }
        return mapper(baseMapper.selectList(lambdaQuery().eq(SysTenantUserRole::getOrgId, orgId).in(SysTenantUserRole::getUserId, userIds).eq(SysTenantUserRole::getTenantId, tenantId)));
    }

    @Override
    public void saveFromUser(String tenantId, String orgId, String userId, List<String> roleIdList) {
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        Assert.CheckArgument(userId);
        Assert.CheckArgument(orgId);
        Assert.CheckArgument(tenantId);
        // 检查userId合法性
        Assert.CheckArgument(userService.getIt(userId), ExceptionEnum.OBJECT_IS_NULL, userId);
        if (Checker.beEmpty(roleIdList)) {
            this.baseMapper.deleteByGroupAndUser(tenantId, orgId, userId);
            return;
        }

        Set<String> idSet = Sets.newHashSet(roleIdList);
        // 检查角色id列表是否都合法
        List<SysTenantRoleDto> roleList = roleService.gets(idSet);
        if (roleList.size() != idSet.size()) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.CONTAINS_INVALID_ID_IN_LIST);
        }

        // 查找之前分配的
        List<SysTenantRoleDto> assignedRoleList = this.listRolesByGroupAndUser(tenantId, orgId, userId);
        List<String> assignedRoleIdList = assignedRoleList.stream().map(SysTenantRoleDto::getId).collect(Collectors.toList());
        /*
         * 处理规则为： 以当前分配的角色列表为全集，以之前分配的角色列表为子集，进行补集运算，运算结果为需要添加的用户角色关系
         * 以之前分配的角色列表为全集，以当前分配的角色列表为子集，进行补集运算，运算结果为需要删除的用户角色关系
         */
        List<String> toAddRoleIdList = ListUtils.subtract(Lists.newArrayList(idSet), assignedRoleIdList);
        List<String> toDeleteRoleIdList = ListUtils.subtract(assignedRoleIdList, Lists.newArrayList(idSet));

        if (Checker.beNotEmpty(toDeleteRoleIdList)) {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", userId);
            map.put("orgId", orgId);
            map.put("roleIdList", toDeleteRoleIdList);
            map.put("tenantId", tenantId);
            this.baseMapper.deleteByMapUserIdAndRoleIdList(map);
        }

        List<SysTenantUserRoleDto> userRoles = Lists.newArrayList();
        for (String id : toAddRoleIdList) {
            SysTenantUserRoleDto ur = new SysTenantUserRoleDto();
            ur.setOrgId(orgId);
            ur.setUserId(userId);
            ur.setRoleId(id);
            ur.setTenantId(tenantId);
            userRoles.add(ur);
//			this.baseMapper.insert(ur);
        }
        if (Checker.beNotEmpty(userRoles)) {
            super.saveItBatch(userRoles);
        }
        // 刷新权限
//        roleResourceService.refreshPrivileges();
    }

    @Override
    public void saveFromRole(String tenantId, String roleId, List<UserGroupDto> userGroups) {
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        Assert.CheckArgument(roleId);
        Assert.CheckArgument(tenantId);
        final String tid = tenantId;
//		Assert.CheckArgument(userGroups);
        // 检查userId合法性
        Assert.CheckArgument(roleService.getIt(roleId), ExceptionEnum.OBJECT_IS_NULL, roleId);
        if (Checker.beEmpty(userGroups)) {
            this.baseMapper.deleteByRole(tenantId, roleId);
            return;
        }
        // 根据userGroups中的groupId分组 key为groupId, value为用户ID列表
        Map<String, Set<String>> map = userGroups.stream()
                .collect(Collectors.groupingBy(ug -> ug.getGroupId(), Collectors.mapping(UserGroupDto::getUserId, Collectors.toSet())));

        // 删除角色组织对应的用户角色关系
        this.baseMapper.deleteByRoleAndGroups(tenantId, roleId, map.keySet());

        List<SysTenantUserRoleDto> toAdds = Lists.newArrayList();
        map.forEach((k, v) -> {
            v.forEach(u -> toAdds.add(new SysTenantUserRoleDto().setRoleId(roleId).setOrgId(k).setUserId(u).setTenantId(tid)));
        });

        if (Checker.beNotEmpty(toAdds)) {
            super.saveItBatch(toAdds);
        }
//		Set<String> idSet = ConvertUtil.toSet(userIdList);
//		// 检查角色id列表是否都合法
//		List<UserDto> userList = userService.gets(idSet);
//		if (userList.size() != idSet.size()) {
//			throw new ServerRuntimeException(AuthorizationExceptionEnum.CONTAINS_INVALID_ID_IN_LIST);
//		}
//
//		// 查找之前分配的
//		List<UserDto> assignedUserList = this.listUsersByGroupAndRole(orgId, roleId);
//		// 剔除掉超级管理员
////		Iterator<UserDto> itUsers = assignedUserList.iterator();
////		while(itUsers.hasNext()) {
////			UserDto r = itUsers.next();
////			if (r.getBeSuper()) {
////				itRoles.remove();
////			}
////		}
//		List<String> assignedUserIdList = assignedUserList.stream().map(UserDto::getId).collect(Collectors.toList());
//		/*
//		 * 处理规则为： 以当前分配的角色列表为全集，以之前分配的角色列表为子集，进行补集运算，运算结果为需要添加的用户角色关系
//		 * 以之前分配的角色列表为全集，以当前分配的角色列表为子集，进行补集运算，运算结果为需要删除的用户角色关系
//		 */
//		List<String> toAddRoleIdList = ListUtils.subtract(Lists.newArrayList(idSet), assignedUserIdList);
//		List<String> toDeleteRoleIdList = ListUtils.subtract(assignedUserIdList, Lists.newArrayList(idSet));
//
//		if (Checker.BeNotEmpty(toDeleteRoleIdList)) {
//			Map<String, Object> map = new HashMap<>();
//			map.put("roleId", roleId);
//			map.put("orgId", orgId);
//			map.put("userIdList", toDeleteRoleIdList);
//			this.baseMapper.deleteByMapRoleIdAndUserIdList(map);
//		}
//
//		List<UserRoleDto> userRoles = Lists.newArrayList();
//		for (String id : toAddRoleIdList) {
//			UserRoleDto ur = new UserRoleDto();
//			ur.setUserId(id);
//			ur.setOrgId(orgId);
//			ur.setRoleId(roleId);
//			userRoles.add(ur);
//		}
//
//		if (Checker.BeNotEmpty(userRoles)) {
//			super.saveItBatch(userRoles);
//		}
        // 刷新权限
//        roleResourceService.refreshPrivileges();
    }

    @Override
    public List<SysTenantUserDto> listUsersByGroupAndRole(String tenantId, String orgId, String roleId) {
        List<SysTenantUserDto> list = Lists.newArrayList();
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        // 验证参数
        if (Checker.beEmpty(roleId) || Checker.beEmpty(orgId) || Checker.beEmpty(tenantId)) {
            return list;
        }
        List<SysTenantUser> userList = this.baseMapper.listUserByGroupAndRole(tenantId, orgId, roleId);
        if (Checker.beEmpty(userList)) {
            return list;
        }
        list = new ArrayList<>(this.collectionMapper.mapCollection(userList, SysTenantUserDto.class));
        return list;
    }

    @Override
    public void deleteByGroupAndRole(String tenantId, String orgId, String roleId) {
        Assert.CheckArgument(roleId);
        Assert.CheckArgument(orgId);
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        Assert.CheckArgument(tenantId);
        this.baseMapper.deleteByGroupAndRole(tenantId, orgId, roleId);
    }

    @Override
    public void deleteByGroupAndRoles(String tenantId, String orgId, Set<String> roleIds) {
        Assert.CheckArgument(roleIds);
        Assert.CheckArgument(orgId);
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        Assert.CheckArgument(tenantId);
        this.baseMapper.deleteByGroupAndRoles(tenantId, orgId, roleIds);
    }

    @Override
    public void deleteByRoles(Set<String> roleIds, String tenantId) {
        Assert.CheckArgument(roleIds);
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        Assert.CheckArgument(tenantId);
        this.baseMapper.delete(lambdaQuery().in(SysTenantUserRole::getRoleId, roleIds).eq(SysTenantUserRole::getTenantId, tenantId));
    }

    @Override
    public void deleteByUsers(Set<String> userIds, String tenantId) {
        Assert.CheckArgument(userIds);
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        Assert.CheckArgument(tenantId);
        this.baseMapper.delete(lambdaQuery().in(SysTenantUserRole::getUserId, userIds).eq(SysTenantUserRole::getTenantId, tenantId));
    }

    @Override
    public List<SysTenantUserDto> listGroupUsersByRoles(String tenantId, String roleId) {
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        if (Checker.beEmpty(roleId) || Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }
        List<SysTenantUser> userList = this.baseMapper.listGroupUsersByRole(tenantId, roleId);
        return Checker.beEmpty(userList) ? Lists.newArrayList()
                : Lists.newArrayList(this.collectionMapper.mapCollection(userList, SysTenantUserDto.class));
    }
}
