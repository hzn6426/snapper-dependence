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
package com.baomibing.security.filter;

import com.baomibing.authority.constant.enums.SystemTagEnum;
import com.baomibing.authority.dto.*;
import com.baomibing.authority.exception.AuthPassExpiredException;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.service.*;
import com.baomibing.authority.state.TenantUserState;
import com.baomibing.authority.state.UserState;
import com.baomibing.cache.CacheService;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.security.exception.NotSupportPointException;
import com.baomibing.security.exception.UserLockedException;
import com.baomibing.security.exception.UserNotActiveException;
import com.baomibing.security.exception.UserStoppedException;
import com.baomibing.security.jwt.SecurityUser;
import com.baomibing.security.jwt.TenantSecurityUser;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.constant.TenantRedisKeyConstant;
import com.baomibing.tool.util.Checker;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.baomibing.tool.constant.RedisKeyConstant.CACHE_USER_LOGIN_ORG_KEY;

/**
 * 获取用户的实现
 * 
 * @author zening
 * @since 1.0.0
 */
public class CommonUserDetailManager implements UserDetailsService {

	@Autowired private SysUserService userService;
	@Autowired private SysUserRoleService userRoleService;
	@Autowired private SysUserGroupService userGroupService;
	@Autowired private SysUserPositionService userPositionService;
	@Autowired private SysUserUsetService userUsetService;
	@Autowired private SysGroupService groupService;
	@Autowired private CacheService cacheService;

	@Autowired private SysTenantUserService tenantUserService;
	@Autowired private SysTenantUserRoleService tenantUserRoleService;
	@Autowired private SysTenantUserGroupService tenantUserGroupService;
	@Autowired private SysTenantUserPositionService tenantUserPositionService;
	@Autowired private SysTenantGroupService tenantGroupService;
	@Autowired private SysTenantService tenantService;

	@Override
	public UserDetails loadUserByUsername(String userCode) throws AuthenticationException {
		List<String> splitters = Splitter.on(Strings.HASH).splitToList(userCode);
		String userName = splitters.get(0);
		String systemTag = splitters.get(1);
		if (splitters.size() == 3 && Strings.TENANT.equalsIgnoreCase(splitters.get(2))) {

			SysTenantUserDto userDto = tenantUserService.getByUserNo( userName);
			if (Checker.beNull(userDto)) {
				throw new UsernameNotFoundException("user account is not be found!");
			}

			if (TenantUserState.UNACTIVE.name().equals(userDto.getState())) {
				throw new UserNotActiveException("user is not Active!");
			} else if (TenantUserState.STOPPED.name().equals(userDto.getState())) {
				throw new UserStoppedException("user account is Stopped!");
			} else if (TenantUserState.LOCKED.name().equals(userDto.getState())) {
				throw new UserLockedException("user account is Locked!");
			}

			return mockTenantUser(userDto, systemTag);
		} else {
			UserDto userDto = userService.getByUserNo(userName);
			if (Checker.beNull(userDto)) {
				throw new UsernameNotFoundException("user account is not be found!");
			}

			if (UserState.UNACTIVE.name().equals(userDto.getState())) {
				throw new UserNotActiveException("user is not Active!");
			} else if (UserState.STOPPED.name().equals(userDto.getState())) {
				throw new UserStoppedException("user account is Stopped!");
			} else if (UserState.LOCKED.name().equals(userDto.getState())) {
				throw new UserLockedException("user account is Locked!");
			}

			if (Checker.beEmpty(userDto.getPointTag()) || !userDto.getPointTag().contains(systemTag)) {
				throw new NotSupportPointException("user not support point tag!");
			}
			//授权码登录验证是否过期
			if (SystemTagEnum.temp.name().equals(systemTag)) {
				Date start = userDto.getAuthPassStart();
				Date end = userDto.getAuthPassEnd();
				Date now = new Date();
				boolean beforeStart = Checker.beNotNull(start) && now.before(start);
				boolean afterEnd = Checker.beNotNull(end) && now.after(end);
				if (beforeStart || afterEnd) {
					throw new AuthPassExpiredException("auth code expired");
				}
			}


			return mockUser(userDto, systemTag);
		}
	}

	private User mockUser(UserDto authUser, String systemTag) {
		String cacheHashKey = (authUser.getUserName() + Strings.HASH + systemTag).toLowerCase();
		Collection<GrantedAuthority> authorities = Sets.newHashSet();
		PositionDto position = null;
		//根据用户获取部门
		List<UserGroupDto> ugs = userGroupService.listByUser(authUser.getId());
		UserGroupDto defaultGroup = null;
		if (Checker.beNotEmpty(ugs)) {
			//用户登录选择的组织ID
			String selectedOrgId = cacheService.getAndSetExpire(
					MessageFormat.format(CACHE_USER_LOGIN_ORG_KEY, cacheHashKey), 1);
			if (Checker.beEmpty(selectedOrgId)) {
				defaultGroup = ugs.get(0);
			} else {
				defaultGroup = ugs.stream().filter(ug -> ug.getGroupId().equals(selectedOrgId)).findFirst().orElse(null);
			}
		}
		// 默认选择第一个部门
		String userGroupId = Checker.beNotNull(defaultGroup) ? defaultGroup.getGroupId() : null;// (ugs.size() == 1 ?
		String groupName = Checker.beNotNull(defaultGroup)
				? (Checker.beEmpty(defaultGroup.getCompanyName()) ? "" : defaultGroup.getCompanyName() + " - ")
				+ defaultGroup.getGroupName()
				: null;
		GroupDto companyGroup = groupService.getParentCompanyById(userGroupId);
		String companyId = Checker.beNotNull(companyGroup) ? companyGroup.getId() : null;
		String companyName = Checker.beNotNull(companyGroup) ? companyGroup.getGroupName() : null;
		//如果用户只位于一个组织中获取职位，根据职位获取角色ID
		if (Checker.beNotEmpty(userGroupId)) {
			//获取用户职位
			position = userPositionService.getPositionByUserAndGroup(authUser.getId(), userGroupId);
			List<RoleDto> roles = userRoleService.listRolesByGroupAndUser(userGroupId, authUser.getId());
			List<String> roleIds = roles.stream().map(RoleDto::getId).collect(Collectors.toList());
			List<String> positionRoleIds = userPositionService.listPositionRoleIdsByUserAndGroup(authUser.getId(), userGroupId);
			roleIds.addAll(positionRoleIds);
			List<String> usetRoleIds = userUsetService.listUsetRoleIdsByUserAndGroup(authUser.getId(), userGroupId);
			roleIds.addAll(usetRoleIds);
			roleIds.forEach(r -> authorities.add(new SimpleGrantedAuthority((r))));
		}
		String pass = authUser.getUserPasswd();
		if (SystemTagEnum.temp.name().equals(systemTag) && Checker.beNotEmpty(authUser.getAuthPass())) {
			pass = DigestUtils.md5Hex(DigestUtils.md5Hex(authUser.getAuthPass()));
		}
		SecurityUser user = new SecurityUser(authUser.getUserName(), new BCryptPasswordEncoder().encode(pass), authorities);
		user.setGroupId(userGroupId).setUserId(authUser.getId()).setPositionId(Checker.beNull(position) ? null : position.getId())
				.setUserCnName(authUser.getUserRealCnName()).setUserEnName(authUser.getUserRealEnName()).setGroupName(groupName).setCompanyId(companyId).setCompanyName(companyName)
				.setUserEmail(authUser.getUserEmail()).setUserEmailPwd(authUser.getUserEmailPwd()).setUserEmailHost(authUser.getUserEmailHost())
				.setUserEmailProtocol(authUser.getUserEmailProtocol()).setUserTag(authUser.getUserTag()).setBeMultiLogin(Boolean.TRUE.equals(authUser.getBeMultiLogin()))
				.setExpirePolicy(authUser.getExpirePolicy());
		return user;
	}

	private TenantSecurityUser mockTenantUser(SysTenantUserDto authUser, String systemTag) {
		String cacheHashKey = (authUser.getUserName() + Strings.HASH + systemTag + Strings.HASH + Strings.TENANT).toLowerCase();
		Collection<GrantedAuthority> authorities = Sets.newHashSet();
		SysTenantPositionDto position = null;
		String tenantId = authUser.getTenantId();
		//根据用户获取部门
		List<SysTenantUserGroupDto> ugs = tenantUserGroupService.listByUser(tenantId, authUser.getId());
		SysTenantUserGroupDto defaultGroup = null;
		if (Checker.beNotEmpty(ugs)) {
			//用户登录选择的组织ID
			String selectedOrgId = cacheService.getAndSetExpire(
					MessageFormat.format(TenantRedisKeyConstant.CACHE_USER_LOGIN_ORG_KEY, cacheHashKey), 1);
			if (Checker.beEmpty(selectedOrgId)) {
				defaultGroup = ugs.get(0);
			} else {
				defaultGroup = ugs.stream().filter(ug -> ug.getGroupId().equals(selectedOrgId)).findFirst().orElse(null);
			}
		}

		String userGroupId = Checker.beNotNull(defaultGroup) ? defaultGroup.getGroupId() : null;// (ugs.size() == 1 ?
		// ugs.get(0).getGroupId() : null) : null;
		String groupName = Checker.beNotNull(defaultGroup)
				? (Checker.beEmpty(defaultGroup.getCompanyName()) ? "" : defaultGroup.getCompanyName() + " - ")
				+ defaultGroup.getGroupName()
				: null;

		SysTenantGroupDto companyGroup = tenantGroupService.getParentCompanyById(tenantId, userGroupId);
		String companyId = Checker.beNotNull(companyGroup) ? companyGroup.getId() : null;
		String companyName = Checker.beNotNull(companyGroup) ? companyGroup.getGroupName() : null;
		if (Checker.beEmpty(userGroupId)) {
			throw new ServerRuntimeException(AuthorizationExceptionEnum.USER_GROUP_NOT_CLARITY);
		}
		//如果用户只位于一个组织中获取职位，根据职位获取角色ID
		if (Checker.beNotEmpty(userGroupId)) {
			//获取用户职位
			position = tenantUserPositionService.getPositionByUserAndGroup(tenantId, authUser.getId(), userGroupId);
			List<SysTenantRoleDto> roles = tenantUserRoleService.listRolesByGroupAndUser(tenantId, userGroupId, authUser.getId());
			List<String> roleIds = roles.stream().map(SysTenantRoleDto::getId).collect(Collectors.toList());
			List<String> positionRoleIds = tenantUserPositionService.listPositionRoleIdsByUserAndGroup(tenantId, authUser.getId(), userGroupId);
			roleIds.addAll(positionRoleIds);
			roleIds.forEach(r -> authorities.add(new SimpleGrantedAuthority((r))));
		}
		String pass = authUser.getUserPasswd();
		TenantSecurityUser user = new TenantSecurityUser(authUser.getUserName(), new BCryptPasswordEncoder().encode(pass), authorities);
		user.setGroupId(userGroupId).setUserId(authUser.getId()).setPositionId(Checker.beNull(position) ? null : position.getId())
				.setUserCnName(authUser.getUserRealCnName()).setUserEnName(authUser.getUserRealEnName()).setGroupName(groupName).setCompanyId(companyId).setCompanyName(companyName)
				.setUserEmail(authUser.getUserEmail())
				.setUserTag(authUser.getUserTag());
		SysTenantDto tenant = tenantService.getIt(authUser.getTenantId());
		if (Checker.beNull(tenant)) {
			throw new ServerRuntimeException(ExceptionEnum.CAN_NOT_FIND_ID_OF_TENANT, authUser.getTenantId());
		}
		user.setTenantId(tenantId).setRank(tenant.getTenantRank()).setScore(tenant.getScore()).setState(tenant.getState());
		return user;
	}
}
