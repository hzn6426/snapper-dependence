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

import com.baomibing.authority.dto.SysTenantUserGroupDto;
import com.baomibing.authority.dto.SysTenantUserUsetDto;
import com.baomibing.authority.service.SysTenantUserUsetService;
import com.baomibing.authority.vo.GroupUserVo;
import com.baomibing.authority.vo.TenantUsetUserVo;
import com.baomibing.core.common.Assert;
import com.baomibing.tool.util.Checker;
import com.baomibing.web.base.MBaseController;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping(path = "/api/tuserUset", consumes = { "application/json", "application/x-www-form-urlencoded" }, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class TenantAdminUserUsetController extends MBaseController<SysTenantUserUsetDto> {

	@Autowired private SysTenantUserUsetService userUsetService;
	
//	@PostMapping("saveFromUser")
//	public void saveFromUser(@RequestBody UserRoleVo userRole) {
//		Assert.CheckArgument(userRole);
//		userRoleService.saveFromUser(userRole.getOrgId(), userRole.getUserId(), userRole.getRoleIds());
//	}
	
	@PostMapping("saveFromUset")
	public void saveFromRole(@RequestBody TenantUsetUserVo usetUser) {
		Assert.CheckArgument(usetUser);
		List<SysTenantUserGroupDto> ugs = Lists.newArrayList();
		List<GroupUserVo> vos = usetUser.getGroupUsers();
		if (Checker.beEmpty(vos)) {
			userUsetService.saveFromUset(usetUser.getTenantId(), usetUser.getUsetId(), ugs);
			return;
		}
		vos.forEach(v -> {
			v.getUsers().forEach(u -> ugs.add(new SysTenantUserGroupDto().setUserId(u).setGroupId(v.getGroupId()).setTenantId(usetUser.getTenantId())));
		});

		userUsetService.saveFromUset(usetUser.getTenantId(), usetUser.getUsetId(), ugs);
	}
}
