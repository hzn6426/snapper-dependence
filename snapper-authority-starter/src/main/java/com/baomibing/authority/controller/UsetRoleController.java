/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.controller;

import com.baomibing.authority.dto.UsetRoleDto;
import com.baomibing.authority.service.SysUsetRoleService;
import com.baomibing.web.annotation.ULog;
import com.baomibing.web.base.MBaseController;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * UsetRoleController
 *
 * @author zening
 * @version 1.0.0
 */
@RequestMapping(path = "/api/usetRole", consumes = {"application/json", "application/x-www-form-urlencoded"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class UsetRoleController extends MBaseController<UsetRoleDto> {

    @Autowired
    private SysUsetRoleService usetRoleService;

    @GetMapping("/listRolesByUset")
    List<String> listRolesByUset(@RequestParam String usetId) {
        return Lists.newArrayList(usetRoleService.listRolesByUset(usetId));
    }

    @ULog("用户组设置角色")
    @PostMapping("/saveFromUset")
    void saveFromUset(@RequestBody UsetRoleDto usetRole) {
        usetRoleService.saveFromUset(usetRole.getUsetId(), usetRole.getRoleIds());
    }
}
