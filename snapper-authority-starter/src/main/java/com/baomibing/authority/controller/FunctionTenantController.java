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

import com.baomibing.authority.dto.SysFunctionTenantDto;
import com.baomibing.authority.dto.SysTenantFunctionDto;
import com.baomibing.authority.service.SysFunctionTenantService;
import com.baomibing.authority.service.SysTenantFunctionService;
import com.baomibing.tool.common.PageQuery;
import com.baomibing.web.common.R;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SysFunctionTenantController
 *
 * @author zening 2024/10/17 10:15
 * @version 1.0.0
 **/

@RestController
@RequestMapping(path = {"/api/function"}, consumes = {"application/json", "application/x-www-form-urlencoded"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class FunctionTenantController {

    @Autowired private SysFunctionTenantService functionTenantService;
    @Autowired private SysTenantFunctionService tenantFunctionService;

    @PostMapping("search")
    public R<SysFunctionTenantDto> search(PageQuery<SysFunctionTenantDto> query) {
        return R.build(functionTenantService.search(query.getDto(), query.getPageNo(), query.getPageSize()));
    }

    @GetMapping("/{id}")
    public SysFunctionTenantDto getFunctionTenant(@PathVariable String id) {
        return functionTenantService.getIt(id);
    }

    @PostMapping
    public void saveFunction(@RequestBody SysFunctionTenantDto functionTenant) {
        functionTenantService.saveFunction(functionTenant);
    }

    @PutMapping
    public void updateFunction(@RequestBody SysFunctionTenantDto functionTenant) {
        functionTenantService.updateFunction(functionTenant);
    }

    @DeleteMapping
    public void deleteFunction(@RequestBody List<String> ids) {
        functionTenantService.deleteFunction(Sets.newHashSet(ids));
    }

    @PostMapping("doOffline")
    public void doOffline(@RequestBody List<String> ids) {
        functionTenantService.doOffline(Sets.newHashSet(ids));
    }

    @PostMapping("doOnline")
    public void doOnline(@RequestBody List<String> ids) {
        functionTenantService.doOnline(Sets.newHashSet(ids));
    }

    @PostMapping("openFunction")
    public void openTenantFunction(@RequestBody SysTenantFunctionDto function) {
        functionTenantService.doOpenFunction(function.getTenantId(), function.getFunctionId());
    }

    @PostMapping("closeFunction")
    public void closeTenantFunction(@RequestBody SysTenantFunctionDto function) {
        functionTenantService.doCloseFunction(function.getTenantId(), function.getFunctionId());
    }

    @PostMapping("deferFunction")
    public void deferTenantFunction(@RequestBody SysTenantFunctionDto function) {
        functionTenantService.doDeferFunction(function.getTenantId(), function.getFunctionId());
    }

    @GetMapping("listAllOnlineFunction")
    public List<SysFunctionTenantDto> listAllOnlineFunction() {
        return functionTenantService.listAllOnlineFunction();
    }

    @GetMapping("listByTenant")
    public List<String> listByTenant(@RequestParam String tid) {
        return tenantFunctionService.listByTenant(tid);
    }
}
