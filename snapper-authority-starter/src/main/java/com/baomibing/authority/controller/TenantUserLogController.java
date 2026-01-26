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

import com.baomibing.authority.dto.*;
import com.baomibing.authority.entity.SysTenantUserLog;
import com.baomibing.authority.service.SysTenantUserLogService;
import com.baomibing.authority.service.SysTenantUserUsetService;
import com.baomibing.authority.vo.GroupUserVo;
import com.baomibing.authority.vo.TenantUsetUserVo;
import com.baomibing.core.common.Assert;
import com.baomibing.core.common.SearchResult;
import com.baomibing.core.wrap.UserLogEventWrap;
import com.baomibing.web.base.MBaseController;
import com.baomibing.web.common.R;
import com.baomibing.tool.common.PageQuery;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(path = {"/eapi/userLog","/api/tuserLog"}, consumes = { "application/json", "application/x-www-form-urlencoded" }, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class TenantUserLogController extends MBaseController<SysTenantUserLogDto> {

	@Autowired private SysTenantUserLogService tenantUserLogService;

	@PostMapping("/search")
	public R<SysTenantUserLogDto> search(@RequestBody PageQuery<SysTenantUserLogDto> pager) {
		SearchResult<SysTenantUserLogDto> result = tenantUserLogService.searchLog(pager.getDto(), pager.getPageNo(), pager.getPageSize());
		return R.build(result);
	}

	@GetMapping("/{id}")
	public SysTenantUserLogDto getLog(@PathVariable("id") String id) {
		return tenantUserLogService.getIt(id);
	}

}
