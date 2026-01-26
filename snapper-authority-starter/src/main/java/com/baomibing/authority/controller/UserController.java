
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
import com.baomibing.authority.constant.enums.ResourceTypeEnum;
import com.baomibing.authority.constant.enums.SystemTagEnum;
import com.baomibing.authority.dto.*;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.exception.UserNameNotFoundException;
import com.baomibing.authority.service.*;
import com.baomibing.core.common.Assert;
import com.baomibing.core.common.SearchResult;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.core.wrap.CommonTreeWrap;
import com.baomibing.tool.common.PageQuery;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.constant.UserHeaderConstant;
import com.baomibing.tool.util.CharacterUtil;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.URLUtil;
import com.baomibing.web.annotation.NotWrap;
import com.baomibing.web.annotation.ULog;
import com.baomibing.web.base.MBaseController;
import com.baomibing.web.common.R;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RestController
//@JsonRequestMapping(path = "/api/user")
@RequestMapping(path = {"/api/user", "/fapi/user"}, consumes = {"application/json", "application/x-www-form-urlencoded"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController extends MBaseController<UserDto> {
    
    @Autowired private SysUserService userService;
    @Autowired private SysRoleResourceService roleResourceService;
    @Autowired private SysUserRoleService userRoleService;
    //获取职位和部门信息
    @Autowired SysPositionService positionService;
    @Autowired SysGroupService groupService;
    
    @Autowired private SysUserPositionService sysUserPositionService;
    @Autowired private SysUserUsetService userUsetService;

    @Autowired private SysUserGroupService userGroupService;

    /**
     * 查询用户
     *
     * @param query 构建的查询信息
     * @return com.baomibing.core.common.R<com.baomibing.authority.dto.UserDto>
     */
    @ULog("用户查询")
    @PostMapping("/search")
    public R<UserDto> search(@RequestBody PageQuery<UserDto> query) {
        SearchResult<UserDto> result = userService.search(query.getDto(), query.getPageNo(), query.getPageSize());
        return R.build(result);
    }

    @GetMapping("getByCode")
    public UserDto getByCode(@RequestParam String code) {
        return userService.getByUserNo(code);
    }
    /**
     * 根据ID获取用户信息
     *
     * @param id 用户ID
     * @return
     */
    @ULog("用户明细")
    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable("id") String id) {
        return userService.getIt(id);
    }
    
    /**
     * 保存用户
     *
     * @param user 用户信息
     */
    @ULog("用户新增")
    @PostMapping
    public void saveUser(@RequestBody UserDto user) {
        userService.doSave(user);
    }
    
    /**
     * 更新新用户
     *
     * @param user 用户信息
     */
    @ULog("用户更新")
    @PutMapping
    public void updateUser(@RequestBody UserDto user) {
        userService.doUpdate(user);
    }
    
    /**
     * 删除用户
     *
     * @param ids 待删除的ID列表
     */
    @ULog("用户删除")
    @DeleteMapping
    public void deletes(@RequestBody List<String> ids) {
        userService.deleteByIds(Sets.newHashSet(ids));
        ;
    }
    
    /**
     * 获取用户权限对应的菜单信息-业务端
     *
     * @return
     */
    @GetMapping(path = "/listMenus")
    public List<MenuDto> listAllMenusOfBusiness() {
        return roleResourceService.listAllPermPointMenusByRoles(currentUserRoles(), MenuTypeEnum.BUSINESS);
    }
    
    /**
     * 获取用户权限对应的菜单信息-管理端
     *
     * @return
     */
    @GetMapping("/listAdminMenus")
    public List<MenuDto> listAllMenusOfAdmin() {
        return roleResourceService.listAllPermPointMenusByRoles(currentUserRoles(), MenuTypeEnum.ADMIN);
    }

    /**
     * 获取用户权限对应的按钮信息
     *
     * @return
     */
    @GetMapping(path = "/listButtons")
    public List<String> listAllButtons() {
        return roleResourceService.listPermResourceIdsByRoles(currentUserRoles(), ResourceTypeEnum.BUTTON);
    }
    
    /**
     * 批量激活用户
     *
     * @param ids ID列表
     */
    @ULog("用户激活")
    @PostMapping("/active")
    public void activeUsers(@RequestBody List<String> ids) {
        userService.activeUsers(Sets.newHashSet(ids));
    }
    
    /**
     * 批量锁定用户
     *
     * @param ids ID列表
     */
    @ULog("用户锁定")
    @PostMapping("/lock")
    public void lockUsers(@RequestBody List<String> ids) {
        userService.lockUsers(Sets.newHashSet(ids));
    }
    
    /**
     * 批量解锁用户
     *
     * @param ids ID列表
     */
    @ULog("用户解锁")
    @PostMapping("/unlock")
    public void unlockUsers(@RequestBody List<String> ids) {
        userService.unlockUsers(Sets.newHashSet(ids));
    }
    
    /**
     * 批量停用用户
     *
     * @param ids ID列表
     */
    @ULog("用户停用")
    @PostMapping("/stop")
    public void stopUsers(@RequestBody List<String> ids) {
        userService.stopUsers(Sets.newHashSet(ids));
    }
    
    /**
     * 批量取消停用用户
     *
     * @param ids ID列表
     */
    @ULog("用户启用")
    @PostMapping("/unstop")
    public void unstopUsers(@RequestBody List<String> ids) {
        userService.unstopUsers(Sets.newHashSet(ids));
    }
    
    /**
     * 批量重置用户密码
     *
     * @param ids ID列表
     */
    @ULog("用户重置密码")
    @PostMapping("/resetPasswd")
    public void resetUserPasswd(@RequestBody List<String> ids) {
        userService.resetPasswd(Sets.newHashSet(ids));
    }
    
    /**
     * 获取当前登录用户信息
     *
     * @return
     */
    @GetMapping("currentUser")
    public UserDto currentUser() {
        UserDto u = userService.getIt(currentUserId());
        if (Checker.beNull(u)) {
            return null;
        }
        String cnName = u.getUserRealCnName();
        ///授权码登录
        if (SystemTagEnum.temp.name().equals(currentUserSystemTag())) {
            cnName += "*";
        }
        u.setName(cnName);

        //获取组织架构 静态获取request
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = attributes.getRequest();
        //获取职位和部门id
        u.setPositionId(request.getHeader(UserHeaderConstant.USER_POSITION));
        //通过id查找职位和部门名
        PositionDto positionDto = positionService.getPosition(u.getPositionId());
        if (Checker.beNotNull(positionDto)) {
            u.setPostName(positionDto.getPostName());
        }
        String groupName = URLUtil.decode(currentUserGroupName());
        u.setGroupName(groupName);
        if (groupName.contains("-")) {
            u.setDepartName(groupName.split("-")[1].trim());
        }
        u.setGroupId(currentUserGroupId());

        return u;
    }
    
    
    /**
     * 通过工号列表获取用户列表
     *
     * @param userNos 工号集合
     * @Return: java.lang.String
     */
    @NotWrap
    @PostMapping("listUserByUserNos")
    List<UserDto> listUserByUserNos(@RequestBody List<String> userNos) {
        return userService.listByUserNos(userNos);
    }
    
    /**
     * 通过用户标签和关键字来匹配
     *
     * @param userTag 用户属性
     * @param keyWord 搜索关键字
     * @Return: java.util.List<CustomerDto>
     */
    @GetMapping("listByTagAndKeyWord")
    public List<UserDto> listByKeyWordAndType(@RequestParam String userTag, @RequestParam String keyWord) {
        return userService.listByKeyWordAndType(userTag, keyWord);
    }

    /**
     * 通过用户标签和关键字匹配分公司用户
     *
     * @param userTag
     * @param keyWord
     * @param companyId
     * @return
     */
    @GetMapping("listByTagAndKeyWordOfCompany")
    public List<UserDto> listByKeyWordAndTypeOfCompany(@RequestParam String userTag, @RequestParam String keyWord, @RequestParam String companyId) {
        return userService.listByKeyWordAndTypeOfCompany(userTag, keyWord, companyId);
    }

    /**
     * 根据角色ID获取组织成员用户列表
     *
     * @param rid 角色ID
     * @return
     */
    @GetMapping("/listGroupUsersByRole")
    public List<UserDto> listGroupUsersByRole(@RequestParam String rid) {
        return userRoleService.listGroupUsersByRoles(rid);
    }

    /**
     * 根据用户组ID获取组织成员用户列表
     *
     * @param usetId 用户组ID
     * @return
     */
    @GetMapping("/listGroupUsersByUset")
    public List<UserDto> listGroupUsersByUset(@RequestParam String usetId) {
        return userUsetService.listGroupUsersByUset(usetId);
    }
    
    /**
     * 根据组织获取对应的用户列表
     *
     * @param gid 组织ID
     * @return
     */
    @GetMapping("/listByGroup")
    public List<UserDto> listByGroup(@RequestParam String gid) {
        return userService.listUsersByGroupId(gid);
    }
    
    /**
     * 通过用户账号获取用户列表
     *
     * @param userNoStr 多个用户账号逗号分隔
     * @Return: java.util.List<com.baomibing.authority.dto.UserWrapDto>
     */
    @GetMapping("/listByUserNoStr/{userNoStr}")
    public List<UserDto> listByUserNoStr(@PathVariable String userNoStr) {
        List<String> userNos = Arrays.asList(userNoStr.split(Strings.COMMA));
        return userService.listByUserNos(userNos);
    }

    @GetMapping("/listByUserCnNames")
    public List<UserDto> listByUserCnNames(@RequestParam String userCnNames) {
        if (Checker.beEmpty(userCnNames)) {
            return Lists.newArrayList();
        }
        List<String> cnNames = Arrays.asList(URLUtil.decode(userCnNames).split(Strings.COMMA));
        return userService.listByUserCnNames(cnNames);
    }
    
    /**
     * 更新用户自己的信息
     *
     * @param user 用户信息
     */
    @PutMapping("/updateSelfUser")
    public void updateSelfUser(@RequestBody UserDto user) {
        if (SystemTagEnum.temp.name().equals(currentUserSystemTag())) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.THIS_LOGIN_MODE_NOT_ALLOW_FOR_THE_OPERATION);
        }
        UserDto currentUser = currentUser();
        currentUser.setUserMobile(user.getUserMobile()).setQq(user.getQq()).setWeixin(user.getWeixin()).setUserEmailPwd(user.getUserEmailPwd()).setUserEmail(user.getUserEmail())
                .setUserEmailHost(user.getUserEmailHost()).setUserEmailProtocol(user.getUserEmailProtocol()).setAvatar(user.getAvatar()).setNewPassword(user.getNewPassword())
                .setUserPasswd(user.getUserPasswd()).setLinePhone(user.getLinePhone()).setAuthPass(user.getAuthPass()).setAuthPassStart(user.getAuthPassStart()).setAuthPassEnd(user.getAuthPassEnd());
        if (Checker.beNotNull(currentUser.getNewPassword())) {
            userService.updatePassword(currentUser);
        }
        userService.doUpdate(currentUser);
    }
    
    
    /**
     * 查找职位对应的用户名
     *
     * @param positionId 职位ID
     * @Return: java.util.List<java.lang.String>
     */
    @NotWrap
    @GetMapping("listUserByPosition/{positionId}")
    public List<String> listUserByPosition(@PathVariable String positionId) {
        List<String> userIds = sysUserPositionService.listUserByPosition(positionId);
        return userService.gets(new HashSet<>(userIds)).stream().map(UserDto::getUserName).collect(Collectors.toList());
    }

    /**
     * 验证用户名密码并获取组织列表
     *
     * @param user 用户信息封装
     * @return
     */
    @PostMapping("validateUserForDepartments")
    public List<UserGroupDto> userDepartments(@RequestBody UserDto user, HttpServletRequest request) {
        String userName = user.getUserName().trim();
        String realUserName = CharacterUtil.justLeftStringAndNumbers(userName);
        if (Checker.beEmpty(realUserName) || realUserName.length() != userName.length()) {
//			lockIp(request);
            throw new ServerRuntimeException(AuthorizationExceptionEnum.USER_NAME_HAS_INVALID_CHARS_WILL_BE_LOCKED);
        }
        String password = user.getUserPasswd();
        String md5Password = DigestUtils.md5Hex(password);
        UserDto dbUser = userService.getByUserNo(userName);
        if (Checker.beNull(dbUser)) {
//			lockIp(request);
            throw new UserNameNotFoundException(" user account not exist!");
        }
        if (SystemTagEnum.temp.name().equals(user.getSystemTag())) {
            if (!DigestUtils.md5Hex(DigestUtils.md5Hex(dbUser.getAuthPass())).equals(md5Password)) {
                throw new ServerRuntimeException(ExceptionEnum.USER_NAME_OR_PASSWD_NOT_CORRECT);
            }
        } else {
            if (!dbUser.getUserPasswd().equals(md5Password)) {
//			lockUser(userName);
                throw new ServerRuntimeException(ExceptionEnum.USER_NAME_OR_PASSWD_NOT_CORRECT);
            }
        }

        List<UserGroupDto> userGroups = userGroupService.listByUser(dbUser.getId());
        if (Checker.beEmpty(userGroups)) {
            throw new ServerRuntimeException(ExceptionEnum.USER_GROUP_NOT_ASSIGN);
        }
        return userGroups;
    }

    /**
     * 保存菜单资源
     *
     * @param userPerm 授权资源
     */
    @ULog("用户保存菜单权限")
    @PostMapping("/saveMenuPerm")
    public void saveMenuPerm(@RequestBody UserPermDto userPerm) {
        Assert.CheckArgument(userPerm);
        Assert.CheckArgument(userPerm.getUserId());
        Assert.CheckArgument(userPerm.getGroupId());
        userService.saveMenuPerm(userPerm.getUserId(), userPerm.getGroupId(), Sets.newHashSet(userPerm.getMenus()));
    }

    /**
     * 保存按钮资源
     *
     * @param userPerm 授权资源
     */
    @ULog("用户保存按钮权限")
    @PostMapping("/saveButtonPerm")
    public void saveButtonPerm(@RequestBody UserPermDto userPerm) {
        Assert.CheckArgument(userPerm);
        Assert.CheckArgument(userPerm.getUserId());
        Assert.CheckArgument(userPerm.getMenuId());
        Assert.CheckArgument(userPerm.getGroupId());
        userService.saveButtonPerm(userPerm.getUserId(), userPerm.getGroupId(), userPerm.getMenuId(),
                Sets.newHashSet(userPerm.getButtons()));
    }

    /**
     * 获取用户对应的资源(菜单)ID列表
     * @param uid 用户ID
     * @param gid 组织ID
     * @return
     */
    @GetMapping("listPermMenus")
    public List<String> listPermMenus(@RequestParam String uid, @RequestParam String gid) {
        return userService.listPermMenuIdsByUser(uid, gid);
    }

    /**
     * 获取用户对应的资源(按钮)ID列表
     * @param uid 用户ID
     * @param gid 组织ID
     * @param mid 菜单ID
     * @return
     */
    @GetMapping("listPermButtons")
    public List<String> listPermButtons(@RequestParam String uid, @RequestParam String gid, @RequestParam String mid) {
        List<ButtonDto> buttons = userService.listPermButtonsByUserAndMenu(uid, gid, mid);
        return buttons.stream().map(ButtonDto::getId).distinct().collect(Collectors.toList());
    }

    /**
     * 根据用户组织及用户获取对应的菜单和按钮列表
     * @param uid 用户ID
     * @param gid 组织ID
     * @return
     */
    @GetMapping("listPermMenusAndButtons")
    public List<CommonTreeWrap> listPermMenusAndButtons(@RequestParam String uid, @RequestParam String gid, @RequestParam String rid, @RequestParam Boolean fp, @RequestParam Boolean np) {
        List<MenuDto> menus = userService.listPermMenusAndButtonsByUser(uid, gid, rid, Boolean.TRUE.equals(fp), Boolean.TRUE.equals(np));
        return loopMenus(menus);
    }

    /**
     * 递归获取子菜单和按钮
     *
     * @param menus
     * @return
     */
    private static List<CommonTreeWrap> loopMenus(List<MenuDto> menus) {
        List<CommonTreeWrap> list = Lists.newArrayList();
        if (Checker.beEmpty(menus)) {
            return list;
        }
        menus.forEach(m -> {
            CommonTreeWrap wrap = new CommonTreeWrap().setTitle(m.getName()).setKey(m.getId()).setMenuType(m.getMenuType())
                    .setTag(m.getTag()).setIsLeaf(Checker.beEmpty(m.getChildren())).setBeUnAuth(m.getBeUnauth())
                    .setPermId(m.getPermId());
            List<CommonTreeWrap> cmenus = loopMenus(m.getChildren());
            wrap.setChildren(cmenus);
            list.add(wrap);
        });
        return list;
    }

    /**
     * 根据用户组织及用户名获取对应的角色列表
     * @param uid 用户ID
     * @param gid 组织ID
     * @return
     */
    @GetMapping("listRoles")
    public List<RoleDto> listRoles(@RequestParam String uid, @RequestParam String gid) {
        return userRoleService.listRolesByGroupAndUser(gid, uid);
    }

    /**
     * 根据用户组织及用户名获取对应的用户组列表
     * @param uid 用户ID
     * @param gid 组织ID
     * @return
     */
    @GetMapping("listUsets")
    public List<UsetDto> listUsets(@RequestParam String uid, @RequestParam String gid) {
        return userUsetService.listUsetsByGroupAndUser(gid, uid);
    }

    /**
     * 根据用户组织及用户名获取对应的职位列表
     * @param uid 用户ID
     * @param gid 组织ID
     * @return
     */
    @GetMapping("listPositions")
    public List<PositionDto> listPositions(@RequestParam String uid, @RequestParam String gid) {
        PositionDto p = sysUserPositionService.getPositionByUserAndGroup(uid, gid);
        if (Checker.beNull(p)) {
            return Lists.newArrayList();
        }
        return Lists.newArrayList(p);
    }

    /**
     * 根据用户和组织获取@ACTION标注的菜单列表和按钮列表
     * @param uid 用户ID
     * @param gid 组织ID
     * @return
     */
    @GetMapping("listActionPermMenusAndButtons")
    public List<MenuDto> listActionPermMenusAndButtons(@RequestParam String uid, @RequestParam String gid) {
        return userService.listActionMenusAndButtonsByUser(uid, gid);
    }
    
}
