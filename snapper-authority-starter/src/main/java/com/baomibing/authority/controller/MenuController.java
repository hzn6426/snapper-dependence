/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.controller;

import com.baomibing.authority.dto.MenuDto;
import com.baomibing.authority.service.SysMenuService;
import com.baomibing.authority.wrap.MenuWrap;
import com.baomibing.web.annotation.ULog;
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
@RequestMapping(path = {"/api/menu", "/fapi/menu"}, consumes = {"application/json", "application/x-www-form-urlencoded"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class MenuController extends MBaseController<MenuDto> {

    @Autowired private SysMenuService menuService;

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
    @ULog("菜单新增或更新")
    @PostMapping("saveOrUpdateMenu")
    public void saveOrUpdateMenu(@RequestBody MenuDto menu) {
        menuService.saveOrUpdateMenu(menu);
    }


    /**
     * 删除菜单列表
     *
     * @param ids 带删除的菜单ID列表
     */
    @ULog("菜单删除")
    @DeleteMapping
    public void deleteMenus(@RequestBody List<String> ids) {
        menuService.deleteMenu(Sets.newHashSet(ids));
    }
}
