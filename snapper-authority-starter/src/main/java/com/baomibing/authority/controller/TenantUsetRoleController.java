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

import com.baomibing.authority.dto.SysTenantUsetRoleDto;
import com.baomibing.authority.service.SysTenantUsetRoleService;
import com.baomibing.web.base.MBaseController;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * UsetRoleController
 *
 * @author zening 2022/6/24 09:50
 * @version 1.0.0
 */
@RequestMapping(path = "/eapi/usetRole", consumes = {"application/json", "application/x-www-form-urlencoded"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class TenantUsetRoleController extends MBaseController<SysTenantUsetRoleDto> {

    @Autowired private SysTenantUsetRoleService usetRoleService;

    @GetMapping("/listRolesByUset")
    List<String> listRolesByUset(@RequestParam String tid, @RequestParam String usetId) {
        return Lists.newArrayList(usetRoleService.listRolesByUset(tid, usetId));
    }

    @PostMapping("/saveFromUset")
    void saveFromUset(@RequestBody SysTenantUsetRoleDto usetRole) {
        usetRoleService.saveFromUset(usetRole.getTenantId(), usetRole.getUsetId(), usetRole.getRoleIds());
    }
}
