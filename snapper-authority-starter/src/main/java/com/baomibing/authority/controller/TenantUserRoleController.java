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

package com.baomibing.authority.controller;

import com.baomibing.authority.dto.SysTenantUserRoleDto;
import com.baomibing.authority.dto.UserGroupDto;
import com.baomibing.authority.service.SysTenantRoleResourceService;
import com.baomibing.authority.service.SysTenantUserRoleService;
import com.baomibing.authority.vo.GroupUserVo;
import com.baomibing.authority.vo.TenantRoleUserVo;
import com.baomibing.authority.vo.TenantUserRoleVo;
import com.baomibing.core.common.Assert;
import com.baomibing.web.base.MBaseController;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(path = "/eapi/userRole", consumes = {"application/json", "application/x-www-form-urlencoded"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class TenantUserRoleController extends MBaseController<SysTenantUserRoleDto> {

	@Autowired private SysTenantUserRoleService userRoleService;

	@Autowired private SysTenantRoleResourceService roleResourceService;
	
	@PostMapping("saveFromUser")
	public void saveFromUser(@RequestBody TenantUserRoleVo userRole) {
		Assert.CheckArgument(userRole);
		userRoleService.saveFromUser(userRole.getTenantId(), userRole.getOrgId(), userRole.getUserId(), userRole.getRoleIds());
	}
	
	@PostMapping("saveFromRole")
	public void saveFromRole(@RequestBody TenantRoleUserVo roleUser) {
		Assert.CheckArgument(roleUser);
		List<UserGroupDto> ugs = Lists.newArrayList();
		List<GroupUserVo> vos = roleUser.getGroupUsers();
		if (Checker.beEmpty(vos)) {
			userRoleService.saveFromRole(roleUser.getTenantId(), roleUser.getRoleId(), ugs);
			return;
		}
		vos.forEach(v -> {
			v.getUsers().forEach(u -> ugs.add(new UserGroupDto().setUserId(u).setGroupId(v.getGroupId())));
		});

		userRoleService.saveFromRole(roleUser.getTenantId(), roleUser.getRoleId(), ugs);
	}

	@GetMapping("refreshPrivileges")
	public void refreshPrivileges(){
//		roleResourceService.refreshPrivileges();
	}
}
