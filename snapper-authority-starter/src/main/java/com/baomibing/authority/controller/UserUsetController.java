/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.controller;

import com.baomibing.authority.dto.UserGroupDto;
import com.baomibing.authority.dto.UserRoleDto;
import com.baomibing.authority.service.SysUserUsetService;
import com.baomibing.authority.vo.GroupUserVo;
import com.baomibing.authority.vo.UsetUserVo;
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

@RequestMapping(path = "/api/userUset", consumes = { "application/json", "application/x-www-form-urlencoded" }, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class UserUsetController extends MBaseController<UserRoleDto> {

	@Autowired
	private SysUserUsetService userUsetService;

	@ULog("用户组设置用户")
	@PostMapping("saveFromUset")
	public void saveFromRole(@RequestBody UsetUserVo usetUser) {
		Assert.CheckArgument(usetUser);
		List<UserGroupDto> ugs = Lists.newArrayList();
		List<GroupUserVo> vos = usetUser.getGroupUsers();
		if (Checker.beEmpty(vos)) {
			userUsetService.saveFromUset(usetUser.getUsetId(), ugs);
			return;
		}
		vos.forEach(v -> {
			v.getUsers().forEach(u -> ugs.add(new UserGroupDto().setUserId(u).setGroupId(v.getGroupId())));
		});

		userUsetService.saveFromUset(usetUser.getUsetId(), ugs);
	}
}
