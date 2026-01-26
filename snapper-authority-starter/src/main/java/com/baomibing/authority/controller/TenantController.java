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

import com.baomibing.authority.dto.SysTenantDto;
import com.baomibing.authority.dto.SysTenantFeeDto;
import com.baomibing.authority.service.SysTenantButtonService;
import com.baomibing.authority.service.SysTenantFeeService;
import com.baomibing.authority.service.SysTenantMenuService;
import com.baomibing.authority.service.SysTenantService;
import com.baomibing.authority.vo.TenantAssignButtonVo;
import com.baomibing.authority.vo.TenantBatchVo;
import com.baomibing.core.common.SearchResult;
import com.baomibing.tool.common.PageQuery;
import com.baomibing.web.common.R;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * TenantController
 *
 * @author zening 2024/8/9 09:44
 * @version 1.0.0
 **/
@RestController
@RequestMapping(path = {"/api/tenant"}, consumes = {"application/json", "application/x-www-form-urlencoded"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class TenantController {

    @Autowired private SysTenantService tenantService;
    @Autowired private SysTenantButtonService tenantButtonService;
    @Autowired private SysTenantMenuService tenantMenuService;
    @Autowired private SysTenantFeeService tenantFeeService;

    @PostMapping("search")
    public R<SysTenantDto> search(@RequestBody PageQuery<SysTenantDto> pageQuery) {
        SearchResult<SysTenantDto> result = tenantService.search(pageQuery.getDto(), pageQuery.getPageNo(), pageQuery.getPageSize());
        return  R.build(result.getTotalSize(), pageQuery.getPageNo(), pageQuery.getPageSize(), result.getDataList());
    }

    /**
     * 新增租户
     *
     * @param tenant 租户对象
     */
    @PostMapping()
    public void save(@RequestBody @Valid SysTenantDto tenant) {
        tenantService.doSave(tenant);
    }

    /**
     * 根据ID获取租户信息
     *
     * @param id 租户ID
     * @return
     */
    @GetMapping("/{id}")
    public SysTenantDto getTenant(@PathVariable("id") String id) {
        return tenantService.getIt(id);
    }

    /**
     * 更新租户对象
     *
     * @param tenant 租户
     */
    @PutMapping
    public void update(@RequestBody @Valid SysTenantDto tenant) {
        tenantService.updateIt(tenant);
    }

    /**
     * 锁定租户
     * @param ids
     */
    @PostMapping("lock")
    public void lock(@RequestBody List<String> ids) {
        tenantService.lockTenants(Sets.newHashSet(ids));
    }

    /**
     * 解锁租户
     * @param ids
     */
    @PostMapping("unlock")
    public void unlock(@RequestBody List<String> ids) {
        tenantService.unlockTenants(Sets.newHashSet(ids));
    }

    /**
     * 分配菜单
     * @param vo
     */
    @PostMapping("assignMenus")
    public void assignMenus(@RequestBody TenantBatchVo vo) {
        tenantService.doAssignMenus(vo.getTenantId(), Sets.newHashSet(vo.getIds()));
    }

    /**
     * 分配按钮
     * @param vo
     */
    @PostMapping("assignButtons")
    public void assignButtons(@RequestBody TenantAssignButtonVo vo) {
        tenantService.doAssignButtons(vo.getTenantId(), vo.getMenuId(), Sets.newHashSet(vo.getIds()));
    }


    @GetMapping("listAllButtonsByMenu")
    public List<String> listAllButtonsByMenuForGrant(@RequestParam String tid, @RequestParam String menuId) {
        return tenantButtonService.listByMenuForGrant(tid, menuId);
    }


    @GetMapping("listAllMenus")
    public List<String> listAllMenus(@RequestParam String tid) {
        return tenantMenuService.listAllMenus(tid);
    }


    @PostMapping("doInitSUser")
    public void doInitSuperUser(@RequestBody TenantBatchVo vo) {
        tenantService.doInitSuperUser(vo.getTenantId());
    }


    @GetMapping("listByKeyWord")
    public List<SysTenantDto> listByKeyWord(@RequestParam String keyWord) {
        return tenantService.listByKeyWord(keyWord);
    }

    @PostMapping("charge")
    public void charge(@RequestBody SysTenantDto tenant) {
        tenantService.doChargeMoney(tenant.getId(), tenant.getMoney());
    }

    @PostMapping("searchTenantFee")
    public R<SysTenantFeeDto> searchTenantFee(@RequestBody PageQuery<SysTenantFeeDto> pageQuery) {
        SearchResult<SysTenantFeeDto> result = tenantFeeService.search(pageQuery.getDto(), pageQuery.getPageNo(), pageQuery.getPageSize());
        return  R.build(result.getTotalSize(), pageQuery.getPageNo(), pageQuery.getPageSize(), result.getDataList());
    }

    @DeleteMapping
    public void deletes(@RequestBody List<String> ids) {
        tenantService.deleteByIds(Sets.newHashSet(ids));
    }
}
