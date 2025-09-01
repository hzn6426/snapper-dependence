/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.controller;

import com.alibaba.fastjson.JSON;
import com.baomibing.authority.service.CommonBusinessAuthService;
import com.baomibing.authority.vo.UserVo;
import com.baomibing.core.wrap.EntrustWarpper;
import com.baomibing.tool.user.User;
import com.baomibing.web.annotation.NotWrap;
import com.baomibing.web.base.ActionController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = { "/api/userAuth", "/fapi/userAuth" }, consumes = { "application/json",
		"application/x-www-form-urlencoded" }, produces = MediaType.APPLICATION_JSON_VALUE)
public class BusinessAuthController extends ActionController {

	@Autowired private CommonBusinessAuthService authService;
	
	@NotWrap
	@GetMapping("getPermIdByUrlAndMethod")
	public String getPermIdByUrlAndMethod(@RequestParam("url")String url, @RequestParam("method")String method) {
		return authService.getPermIdByUrlAndMethod(url, method);
	}
	
	@NotWrap
	@GetMapping("getPermIdByAction")
	public String getPermIdByAction(@RequestParam("action")String action) {
		return authService.getPermIdByAction(action);
	}
	
	@NotWrap
	@GetMapping("getEntrustBusinessPerm")
	public String getEntrustBusinessPerm(@RequestBody UserVo userWrap) {
		User user = new User().setId(userWrap.getId()).setUserName(userWrap.getUserName()).setCompanyId(userWrap.getCompanyId()).setCurrentGroupId(userWrap.getCurrentGroupId())
				.setCurrentPositionId(userWrap.getCurrentPositionId());
		EntrustWarpper ew = authService.getEntrustBusinessPerm(user, userWrap.getPermId(), userWrap.getPermScope(), userWrap.isBeIgnoreUserScope(),
				userWrap.isBeIgnoreGroupScope());
		return JSON.toJSONString(ew);
	}

}
