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

import com.alibaba.fastjson.JSON;
import com.baomibing.authority.service.CommonBusinessTenantAuthService;
import com.baomibing.authority.vo.TenantUserVo;
import com.baomibing.core.wrap.TenantEntrustWarpper;
import com.baomibing.tool.user.TenantUser;
import com.baomibing.web.annotation.NotWrap;
import com.baomibing.web.base.ActionController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = { "/eapi/userAuth" }, consumes = { "application/json",
		"application/x-www-form-urlencoded" }, produces = MediaType.APPLICATION_JSON_VALUE)
public class TenantBusinessAuthController extends ActionController {

	@Autowired
	private CommonBusinessTenantAuthService authService;

	@NotWrap
	@GetMapping("getEntrustBusinessPerm")
	public String getEntrustBusinessPerm(@RequestBody TenantUserVo userWrap) {
		TenantUser user = new TenantUser();
		user.setId(userWrap.getId()).setUserName(userWrap.getUserName()).setCompanyId(userWrap.getCompanyId()).setCurrentGroupId(userWrap.getCurrentGroupId())
				.setCurrentPositionId(userWrap.getCurrentPositionId());
		TenantEntrustWarpper ew = authService.getEntrustBusinessPerm(user, userWrap.getPermId(), userWrap.getPermScope(), userWrap.isBeIgnoreUserScope(),
				userWrap.isBeIgnoreGroupScope());
		return JSON.toJSONString(ew);
	}

}
