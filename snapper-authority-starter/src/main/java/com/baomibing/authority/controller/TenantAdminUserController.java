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

import com.baomibing.authority.constant.enums.MenuTypeEnum;
import com.baomibing.authority.dto.MenuTenantDto;
import com.baomibing.authority.dto.SysTenantRoleDto;
import com.baomibing.authority.dto.SysTenantUserDto;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.service.*;
import com.baomibing.authority.vo.TenantBatchVo;
import com.baomibing.cache.CacheService;
import com.baomibing.core.common.CollectionMapperDecorator;
import com.baomibing.core.common.SearchResult;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.tool.common.PageQuery;
import com.baomibing.tool.util.Checker;
import com.baomibing.web.annotation.NotWrap;
import com.baomibing.web.base.MBaseController;
import com.baomibing.web.common.R;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RestController
//@JsonRequestMapping(path = "/api/user")
@RequestMapping(path = {"/api/tuser"}, consumes = {"application/json", "application/x-www-form-urlencoded"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class TenantAdminUserController extends MBaseController<SysTenantUserDto> {

    @Autowired private SysTenantUserService userService;
    @Autowired private SysTenantRoleResourceService roleResourceService;
    @Autowired private SysTenantUserRoleService userRoleService;
    @Autowired
    CollectionMapperDecorator decorator;
    //获取职位和部门信息
    @Autowired private SysTenantPositionService positionService;
    @Autowired private SysTenantGroupService groupService;
    @Autowired private SysTenantUserPositionService sysUserPositionService;
    @Autowired private SysTenantUserUsetService userUsetService;
    @Autowired private FileOutHandlerService outHandlerService;

    @Autowired private SysTenantUserGroupService userGroupService;

    @Autowired private CacheService cacheService;

    @Autowired private SysTenantRoleService tenantRoleService;


    /**
     * 查询用户
     *
     * @param query 构建的查询信息
     * @return com.baomibing.core.common.R<com.baomibing.authority.dto.SysTenantUserDto>
     */
    @PostMapping("/search")
    public R<SysTenantUserDto> search(@RequestBody PageQuery<SysTenantUserDto> query) {
        SearchResult<SysTenantUserDto> result = userService.search(query.getDto(), query.getPageNo(), query.getPageSize());
        return R.build(result);
    }

    /**
     * 根据ID获取用户信息
     *
     * @param id 用户ID
     * @return
     */
    @GetMapping("/{id}")
    public SysTenantUserDto getUser(@PathVariable("id") String id) {
        return userService.getIt(id);
    }

    /**
     * 保存用户
     *
     * @param user 用户信息
     */
    @PostMapping
    public void saveUser(@RequestBody SysTenantUserDto user) {
        userService.doSave(user);
    }

    /**
     * 更新新用户
     *
     * @param user 用户信息
     */
    @PutMapping
    public void updateUser(@RequestBody SysTenantUserDto user) {
        userService.doUpdate(user);
    }

    /**
     * 删除用户
     *
     * @param vo 待删除的ID列表
     */
    @DeleteMapping
    public void deletes(@RequestBody TenantBatchVo vo) {
        userService.deleteByIds(Sets.newHashSet(vo.getIds()), vo.getTenantId());
    }


    /**
     * 获取用户权限对应的菜单信息-管理端
     *
     * @return
     */
    @GetMapping("/listAdminMenus")
    public List<MenuTenantDto> listAllMenusOfAdmin(@RequestParam String tid) {
        List<MenuTenantDto> menus = Lists.newArrayList();
        SysTenantRoleDto role = tenantRoleService.getRootRole(tid);
        if (Checker.beNull(role)) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.TENANT_NOT_INIT_SUPER);
        }
        menus = roleResourceService.listAllPermPointMenusByRoles(Sets.newHashSet(role.getId()), MenuTypeEnum.ADMIN, tid);

        return menus;
    }


    /**
     * 批量激活用户
     *
     * @param ids ID列表
     */
    @PostMapping("/active")
    public void activeUsers(@RequestBody List<String> ids) {
        userService.activeUsers(Sets.newHashSet(ids));
    }

    /**
     * 批量锁定用户
     *
     * @param ids ID列表
     */
    @PostMapping("/lock")
    public void lockUsers(@RequestBody List<String> ids) {
        userService.lockUsers(Sets.newHashSet(ids));
    }

    /**
     * 批量解锁用户
     *
     * @param ids ID列表
     */
    @PostMapping("/unlock")
    public void unlockUsers(@RequestBody List<String> ids) {
        userService.unlockUsers(Sets.newHashSet(ids));
    }

    /**
     * 批量停用用户
     *
     * @param ids ID列表
     */
    @PostMapping("/stop")
    public void stopUsers(@RequestBody List<String> ids) {
        userService.stopUsers(Sets.newHashSet(ids));
    }

    /**
     * 批量取消停用用户
     *
     * @param ids ID列表
     */
    @PostMapping("/unstop")
    public void unstopUsers(@RequestBody List<String> ids) {
        userService.unstopUsers(Sets.newHashSet(ids));
    }

    /**
     * 批量重置用户密码
     *
     * @param ids ID列表
     */
    @PostMapping("/resetPasswd")
    public void resetUserPasswd(@RequestBody List<String> ids) {
        userService.resetPasswd(Sets.newHashSet(ids));
    }

    /**
     * 根据角色ID获取组织成员用户列表
     *
     * @param rid 角色ID
     * @return
     */
    @GetMapping("/listGroupUsersByRole")
    public List<SysTenantUserDto> listGroupUsersByRole(@RequestParam String tid, @RequestParam String rid) {
        return userRoleService.listGroupUsersByRoles(tid, rid);
    }

    /**
     * 根据用户组ID获取组织成员用户列表
     *
     * @param usetId 用户组ID
     * @return
     */
    @GetMapping("/listGroupUsersByUset")
    public List<SysTenantUserDto> listGroupUsersByUset(@RequestParam String tid, @RequestParam String usetId) {
        return userUsetService.listGroupUsersByUset(tid, usetId);
    }

    /**
     * 查找职位对应的用户名
     *
     * @param positionId 职位ID
     * @Return: java.util.List<java.lang.String>
     */
    @NotWrap
    @GetMapping("listUserByPosition/{positionId}")
    public List<String> listUserByPosition(@PathVariable String positionId, @RequestParam String tid) {
        List<String> userIds = sysUserPositionService.listUserByPosition(tid, positionId);
        return userService.gets(new HashSet<>(userIds)).stream().map(SysTenantUserDto::getUserName).collect(Collectors.toList());
    }


    /**
     * 通过用户标签和关键字来匹配
     *
     * @param userTag 用户属性
     * @param keyWord 搜索关键字
     * @Return: java.util.List<CustomerDto>
     */
    @GetMapping("listByTagAndKeyWord")
    public List<SysTenantUserDto> listByKeyWordAndType(@RequestParam String userTag, @RequestParam String keyWord, @RequestParam String tid) {
        return userService.listByKeyWordAndType(tid, userTag, keyWord);
    }


}
