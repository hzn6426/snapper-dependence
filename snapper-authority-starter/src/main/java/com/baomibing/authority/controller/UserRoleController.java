
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

import com.baomibing.authority.dto.UserGroupDto;
import com.baomibing.authority.dto.UserRoleDto;
import com.baomibing.authority.service.SysRoleResourceService;
import com.baomibing.authority.service.SysUserRoleService;
import com.baomibing.authority.vo.GroupUserVo;
import com.baomibing.authority.vo.RoleUserVo;
import com.baomibing.authority.vo.UserRoleVo;
import com.baomibing.core.common.Assert;
import com.baomibing.tool.util.Checker;
import com.baomibing.web.annotation.ULog;
import com.baomibing.web.base.MBaseController;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping(path = "/api/userRole", consumes = {"application/json", "application/x-www-form-urlencoded"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class UserRoleController extends MBaseController<UserRoleDto> {

	@Autowired private SysUserRoleService userRoleService;
	@Autowired private SysRoleResourceService roleResourceService;

	@ULog("用户设置角色")
	@PostMapping("saveFromUser")
	public void saveFromUser(@RequestBody UserRoleVo userRole) {
		Assert.CheckArgument(userRole);
		userRoleService.saveFromUser(userRole.getOrgId(), userRole.getUserId(), userRole.getRoleIds());
	}
	@ULog("角色设置用户")
	@PostMapping("saveFromRole")
	public void saveFromRole(@RequestBody RoleUserVo roleUser) {
		Assert.CheckArgument(roleUser);
		List<UserGroupDto> ugs = Lists.newArrayList();
		List<GroupUserVo> vos = roleUser.getGroupUsers();
		if (Checker.beEmpty(vos)) {
			userRoleService.saveFromRole(roleUser.getRoleId(), ugs);
			return;
		}
		vos.forEach(v -> {
			v.getUsers().forEach(u -> ugs.add(new UserGroupDto().setUserId(u).setGroupId(v.getGroupId())));
		});

		userRoleService.saveFromRole(roleUser.getRoleId(), ugs);
	}
}
