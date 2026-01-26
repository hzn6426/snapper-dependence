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

import cn.hutool.core.util.URLUtil;
import com.baomibing.authority.constant.enums.MenuTypeEnum;
import com.baomibing.authority.constant.enums.ResourceTypeEnum;
import com.baomibing.authority.constant.enums.SystemTagEnum;
import com.baomibing.authority.dto.MenuTenantDto;
import com.baomibing.authority.dto.SysTenantPositionDto;
import com.baomibing.authority.dto.SysTenantUserDto;
import com.baomibing.authority.dto.SysTenantUserGroupDto;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.exception.UserNameNotFoundException;
import com.baomibing.authority.service.*;
import com.baomibing.authority.vo.TenantBatchVo;
import com.baomibing.cache.CacheService;
import com.baomibing.core.common.CollectionMapperDecorator;
import com.baomibing.core.common.SearchResult;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.tool.common.PageQuery;
import com.baomibing.tool.constant.UserHeaderConstant;
import com.baomibing.tool.util.CharacterUtil;
import com.baomibing.tool.util.Checker;
import com.baomibing.web.annotation.NotWrap;
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
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.baomibing.tool.user.UserContext.currentTenantId;

@RestController
//@JsonRequestMapping(path = "/api/user")
@RequestMapping(path = {"/eapi/user"}, consumes = {"application/json", "application/x-www-form-urlencoded"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class TenantUserController extends MBaseController<SysTenantUserDto> {

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
     * 获取用户权限对应的菜单信息-业务端
     *
     * @return
     */
    @GetMapping(path = "/listMenus")
    public List<MenuTenantDto> listAllMenusOfBusiness() {
        List<MenuTenantDto> menus = Lists.newArrayList();

         menus = roleResourceService.listAllPermPointMenusByRoles(currentUserRoles(), MenuTypeEnum.BUSINESS, currentTenantId());

        return menus;
    }

    /**
     * 获取用户权限对应的菜单信息-管理端
     *
     * @return
     */
    @GetMapping("/listAdminMenus")
    public List<MenuTenantDto> listAllMenusOfAdmin() {
        List<MenuTenantDto> menus = Lists.newArrayList();
        menus = roleResourceService.listAllPermPointMenusByRoles(currentUserRoles(), MenuTypeEnum.ADMIN, currentTenantId());

        return menus;
    }

    /**
     * 获取用户权限对应的按钮信息
     *
     * @return
     */
    @GetMapping(path = "/listButtons")
    public List<String> listAllButtons() {
        return roleResourceService.listPermResourceIdsByRoles(currentUserRoles(), ResourceTypeEnum.BUTTON, currentTenantId());
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
     * 获取当前登录用户信息
     *
     * @return
     */
    @GetMapping("currentUser")
    public SysTenantUserDto currentUser() {
//        if (UserUtil.beSuper(currentUserName())) {
//            return new SysTenantUserDto().setUserName(currentUserName()).setName(currentUserCnName()).setId("SUPER");
//        }
        SysTenantUserDto u = userService.getIt(currentUserId());
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
        SysTenantPositionDto positionDto = positionService.getPosition(u.getPositionId());
        if (Checker.beNotNull(positionDto)) {
            u.setPostName(positionDto.getPostName());
        }
        u.setGroupName(URLUtil.decode(currentUserGroupName()));
        u.setGroupId(currentUserGroupId());

        return u;
    }


//    /**
//     * 通过工号列表获取用户列表
//     *
//     * @param userNos 工号集合
//     * @Return: java.lang.String
//     */
//    @NotWrap
//    @PostMapping("listUserByUserNos")
//    List<SysTenantUserDto> listUserByUserNos(@RequestBody TenantBatchVo vo) {
//        List<SysTenantUserDto> dtos = userService.listByUserNos(vo.getIds(), vo.getTenantId());
//        return dtos;
//    }

    /**
     * 通过用户标签和关键字来匹配
     *
     * @param userTag 用户属性
     * @param keyWord 搜索关键字
     * @Return: java.util.List<CustomerDto>
     */
    @GetMapping("listByTagAndKeyWord")
    public List<SysTenantUserDto> listByKeyWordAndType(@RequestParam String tid, @RequestParam String userTag, @RequestParam String keyWord) {
        return userService.listByKeyWordAndType(tid, userTag, keyWord);
    }

//    /**
//     * 通过用户标签和关键字匹配分部(目标夸公司分部)用户
//     *
//     * @param userTag
//     * @param keyWord
//     * @param agentId
//     * @return
//     */
//    @GetMapping("listByTagAndKeyWordOfAgent")
//    public List<SysTenantUserDto> listByKeyWordAndTypeOfAgent(@RequestParam String userTag, @RequestParam String keyWord, @RequestParam String agentId) {
//        return userService.listByKeyWordAndTypeOfAgent(userTag, keyWord, agentId);
//    }
//
//    /**
//     * 通过用户标签和关键字匹配分公司用户
//     *
//     * @param userTag
//     * @param keyWord
//     * @param companyId
//     * @return
//     */
//    @GetMapping("listByTagAndKeyWordOfCompany")
//    public List<SysTenantUserDto> listByKeyWordAndTypeOfCompany(@RequestParam String userTag, @RequestParam String keyWord, @RequestParam String companyId) {
//        return userService.listByKeyWordAndTypeOfCompany(userTag, keyWord, companyId);
//    }

    /**
     * 根据角色获取对应的用户列表
     *
     * @param rid 角色ID
     * @return
     */
//    @GetMapping("/listByRole")
//    public List<SysTenantUserDto> listByRole(@RequestParam String orgId, @RequestParam String rid) {
//        return userRoleService.listUsersByGroupAndRole(orgId, rid);
//    }

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

//    /**
//     * 根据组织获取对应的用户列表
//     *
//     * @param gid 组织ID
//     * @return
//     */
//    @GetMapping("/listByGroup")
//    public List<SysTenantUserDto> listByGroup(@RequestParam String tid, @RequestParam String gid) {
//        return userService.listUsersByGroupId(tid, gid);
//    }

//    /**
//     * 通过用户账号获取用户列表
//     *
//     * @param userNoStr 多个用户账号逗号分隔
//     * @Return: java.util.List<com.baomibing.authority.dto.UserWrapDto>
//     */
//    @GetMapping("/listByUserNoStr/{userNoStr}")
//    public List<SysTenantUserDto> listByUserNoStr(@PathVariable String userNoStr) {
//        List<String> userNos = Arrays.asList(userNoStr.split(Strings.COMMA));
//        return userService.listByUserNos(userNos);
//    }

//    @GetMapping("/listByUserNoStrOfAgent/{userNoStr}")
//    public List<SysTenantUserDto> listByUserNoStrOfAgent(@PathVariable String userNoStr, @RequestParam String agentId) {
//        List<String> userNos = Arrays.asList(userNoStr.split(Strings.COMMA));
//        return userService.listByUserNosOfAgent(userNos, agentId);
//    }


//    @GetMapping("/listByUserCnNames")
//    public List<SysTenantUserDto> listByUserCnNames(@RequestParam String userCnNames) {
//        if (Checker.beEmpty(userCnNames)) {
//            return Lists.newArrayList();
//        }
//        List<String> cnNames = Arrays.asList(URLUtil.decode(userCnNames).split(Strings.COMMA));
//        return userService.listByUserCnNames(cnNames);
//    }

//    @GetMapping("/listByUserCnNamesOfAgent")
//    public List<SysTenantUserDto> listByUserCnNamesOfAgent(@RequestParam String userCnNames, @RequestParam String agentId) {
//        if (Checker.beEmpty(userCnNames)) {
//            return Lists.newArrayList();
//        }
//        List<String> cnNames = Arrays.asList(userCnNames.split(Strings.COMMA));
//        return userService.listByUserCnNamesOfAgent(cnNames, agentId);
//    }


    /**
     * 更新用户自己的信息
     *
     * @param user 用户信息
     */
    @PutMapping("/updateSelfUser")
    public void updateSelfUser(@RequestBody SysTenantUserDto user, HttpServletRequest req) {
        if (SystemTagEnum.temp.name().equals(currentUserSystemTag())) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.THIS_LOGIN_MODE_NOT_ALLOW_FOR_THE_OPERATION);
        }
        SysTenantUserDto currentUser = currentUser();
        currentUser.setUserMobile(user.getUserMobile()).setUserEmail(user.getUserEmail())
                .setAvatar(user.getAvatar()).setNewPassword(user.getNewPassword())
                .setUserPasswd(user.getUserPasswd());
        if (Checker.beNotNull(currentUser.getNewPassword())) {
            userService.updatePassword(currentUser);
        }

        userService.doUpdate(currentUser);
    }

//    @PostMapping("validateEmail")
//    public void validateEmail(@RequestBody SysTenantUserDto user) {
//        Assert.CheckArgument(user);
//        outHandlerService.sendUserValidateEmail(user.getUserEmail(), user.getUserEmailPwd(), user.getUserEmailHost(), user.getUserEmailProtocol());
//    }

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


//    /**
//     * 获取用户主管列表
//     *
//     * @param groupId
//     * @Return: java.util.List<com.baomibing.authority.dto.SysTenantUserDto>
//     */
//    @NotWrap
//    @GetMapping("listManagersByGroupId/{groupId}")
//    public List<SysTenantUserDto> listManagersByGroupId(@PathVariable("groupId") String groupId) {
//        return userService.listManagersByGroupId(groupId);
//    }

//    @NotWrap
//    @PostMapping("listManagersByGroupIds")
//    public List<SysTenantUserDto> listManagersByGroupIds(@RequestBody List<String> groupIds) {
//        return userService.listManagersByGroupIds(groupIds);
//    }


//    /**
//     * 获取财务主管列表
//     *
//     * @param treasurerMark
//     * @Return: java.util.List<com.baomibing.authority.dto.SysTenantUserDto>
//     */
//    @NotWrap
//    @GetMapping("listManagersByTreasurerMark/{treasurerMark}")
//    public List<SysTenantUserDto> listManagersByTreasurerMark(@PathVariable("treasurerMark") String treasurerMark) {
//        return userService.listManagersByTreasurerMark(treasurerMark);
//    }


//    /**
//     * 菜单权限获取用户列表
//     *
//     * @param menuId
//     * @Return: java.util.List<com.baomibing.authority.dto.SysTenantUserDto>
//     */
//    @NotWrap
//    @GetMapping("listUserByMenuId/{menuId}")
//    public List<SysTenantUserDto> listUserByMenuId(@PathVariable("menuId") String menuId) {
//        return userService.listUserByMenuId(menuId);
//    }

    /**
     * 验证用户名密码并获取组织列表
     *
     * @param user 用户信息封装
     * @return
     */
    @PostMapping("validateUserForDepartments")
    public List<SysTenantUserGroupDto> userDepartments(@RequestBody SysTenantUserDto user, HttpServletRequest request) {
        String userName = user.getUserName().trim();
        String realUserName = CharacterUtil.justLeftStringAndNumbers(userName);
        if (Checker.beEmpty(realUserName) || realUserName.length() != userName.length()) {
//			lockIp(request);
            throw new ServerRuntimeException(AuthorizationExceptionEnum.USER_NAME_HAS_INVALID_CHARS_WILL_BE_LOCKED);
        }
        String password = user.getUserPasswd();
        String md5Password = DigestUtils.md5Hex(password);
        SysTenantUserDto dbUser = userService.getByUserNo( userName);
        if (Checker.beNull(dbUser)) {
//			lockIp(request);
            throw new UserNameNotFoundException(" user account not exist!");
        }

        if (!dbUser.getUserPasswd().equals(md5Password)) {
//			lockUser(userName);
            throw new ServerRuntimeException(ExceptionEnum.USER_NAME_OR_PASSWD_NOT_CORRECT);
        }


        List<SysTenantUserGroupDto> userGroups = userGroupService.listByUser(user.getTenantId(), dbUser.getId());
        if (Checker.beEmpty(userGroups)) {
            throw new ServerRuntimeException(ExceptionEnum.USER_GROUP_NOT_ASSIGN);
        }
        return userGroups;
    }


//    @PostMapping("updateFullPinyin")
//    public void updateFullPinyin() {
//        List<SysTenantUserDto> SysTenantUserDtos = userService.listAll();
//        for (SysTenantUserDto SysTenantUserDto : SysTenantUserDtos) {
//            SysTenantUserDto.setFullPinYin(CharacterUtil.getFullPinYin(SysTenantUserDto.getUserRealCnName()));
//        }
//        userService.updateItBatch(SysTenantUserDtos);
//    }

//    /**
//     * @param
//     * @Return:
//     */
//    @GetMapping("searchUserByCnName")
//    public List<SysTenantUserDto> searchUserByCnName(@RequestParam String cnName) {
//        return userService.searchUserByCnName(cnName);
//    }
//
//    @PostMapping("listUsersByUsetName")
//    public List<SysTenantUserDto> listUsersByUsetName(@RequestBody UsetDto dto) {
//        return userService.listUsersByUsetName(dto.getUsetName(), dto.getUserName());
//    }
//
//    @NotWrap
//    @GetMapping("listByUsetName")
//    public List<SysTenantUserDto> listByUsetName(@RequestParam String usetName) {
//        return userService.listUsersByUsetName(usetName, Strings.EMPTY);
//    }
}
