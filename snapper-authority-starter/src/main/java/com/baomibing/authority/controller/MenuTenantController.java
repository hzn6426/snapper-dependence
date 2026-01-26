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

import com.baomibing.authority.dto.MenuTenantDto;
import com.baomibing.authority.service.SysMenuTenantService;
import com.baomibing.authority.wrap.MenuWrap;
import com.baomibing.web.base.MBaseController;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理
 *
 * @author zening 2022/3/3 14:52
 * @version 1.0.0
 */
@RestController
@RequestMapping(path = {"/eapi/menu","/api/tmenu"}, consumes = {"application/json", "application/x-www-form-urlencoded"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class MenuTenantController extends MBaseController<MenuTenantDto> {

    @Autowired private SysMenuTenantService menuService;

    /**
     * 以树的方式获取所有菜单
     *
     * @return
     */
    @GetMapping("/treeAllMenus")
    public List<MenuWrap> treeAllGroupsAndUsers() {
        return menuService.treeAllMenus();
    }

    /**
     * 保存或更新菜单
     *
     * @param menu 待保存或更细你的菜单
     */
    @PostMapping("saveOrUpdateMenu")
    public void saveOrUpdateMenu(@RequestBody MenuTenantDto menu) {
        menuService.saveOrUpdateMenu(menu);
    }


    /**
     * 删除菜单列表
     *
     * @param ids 带删除的菜单ID列表
     */
    @DeleteMapping
    public void deleteMenus(@RequestBody List<String> ids) {
        menuService.deleteMenu(Sets.newHashSet(ids));
    }
}
