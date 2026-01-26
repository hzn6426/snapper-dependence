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

import com.baomibing.authority.service.SysTenantRoleResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * FeignTenantResourceController
 *
 * @author zening 2024/10/30 11:47
 * @version 1.0.0
 **/
@RestController
@RequestMapping(path = {"/fapi/tenant/resource",}, consumes = {"application/json","application/x-www-form-urlencoded"},
        produces = MediaType.APPLICATION_JSON_VALUE)
public class FeignTenantResourceController {

    @Autowired
    private SysTenantRoleResourceService roleResourceService;

    @GetMapping("getRolesByUrl")
    String getRolesByUrl(@RequestParam String tid, @RequestParam String url, @RequestParam String m) {
        return roleResourceService.getRolesByUrl(tid, url, m);
    }
}
