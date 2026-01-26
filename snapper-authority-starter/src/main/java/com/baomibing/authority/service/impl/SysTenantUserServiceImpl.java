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

package com.baomibing.authority.service.impl;

import com.baomibing.authority.action.TenantUserAction;
import com.baomibing.authority.dto.SysTenantDto;
import com.baomibing.authority.dto.SysTenantUserDto;
import com.baomibing.authority.entity.SysTenantUser;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.mapper.SysTenantUserMapper;
import com.baomibing.authority.service.*;
import com.baomibing.core.common.Assert;
import com.baomibing.core.common.SearchResult;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.core.process.StateWorkFlow;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.util.CharacterUtil;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ObjectUtil;
import com.baomibing.tool.util.Patterns;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.baomibing.tool.constant.NumberConstant.MAX_IN_BATCH_SIZE;
import static com.baomibing.tool.util.PageUtil.offsetCurrent;

/**
 * SysTenantUserServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysTenantUserServiceImpl extends MBaseServiceImpl<SysTenantUserMapper, SysTenantUser, SysTenantUserDto> implements SysTenantUserService {

    @Autowired
    private SysTenantUserRoleService userRoleService;
    @Autowired
    private SysTenantUserPositionService userPositionService;
    @Autowired
    private SysTenantUserGroupService userGroupService;
    @Autowired
    private SysTenantUserBusinessPermService userBusinessPermService;
    @Autowired
    private SysTenantPositionUserEntrustService positionUserEntrustService;
    @Autowired
    private FileOutHandlerService outHandlerService;
    @Autowired
    private SysTenantGroupService groupService;
    @Autowired
    private SysTenantService tenantService;

    @Override
    public SearchResult<SysTenantUserDto> search(SysTenantUserDto user, int pageNumber, int pageSize) {
        doSetTenantId(user);
        if (Checker.beEmpty(user.getTenantId())) {
            return new SearchResult<>();
        }
        List<SysTenantUserDto> vlist = Lists.newArrayList();
        int offset = offsetCurrent(pageNumber, pageSize);
        List<String> tags = Checker.beNotEmpty(user.getUserTag()) ? Splitter.on(Strings.COMMA).splitToList(user.getUserTag()) : null;
        List<SysTenantUser> list = this.baseMapper.searchByCondition(user.getTenantId(), user.getUserName(), user.getUserRealCnName(),
                 user.getUserEmail(), user.getState(), user.getUserRoles(), tags, user.getGroupId(), pageSize, offset);
        int count = this.baseMapper.countByCondition(user.getTenantId(), user.getUserName(), user.getUserRealCnName(),
                 user.getUserEmail(), user.getState(), user.getUserRoles(), tags, user.getGroupId());
        list.forEach(u -> {
            SysTenantUserDto dto = mapper2v(u);
            vlist.add(dto);
            dto.setUserPasswd("nopassword");
            if (Checker.beNotEmpty(u.getUserRoleIds())) {
                dto.setRoles(Splitter.on(",").splitToList(u.getUserRoleIds()));
            }
        });//仅仅设置一个混淆密码
        return new SearchResult<>(count, vlist);
    }

    @Override
    public SysTenantUserDto getByUserNo(String userNo) {
        if (Checker.beEmpty(userNo)) {
            return null;
        }
//        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        SysTenantUser user = this.baseMapper.selectOne(lambdaQuery().eq(SysTenantUser::getUserNo, userNo));
        //去掉过期的
        if (Checker.beNotNull(user)) {
            if (Checker.beNotNull(user.getExpireTime()) && user.getExpireTime().getTime() < System.currentTimeMillis()) {
                return null;
            }
        }
        return Checker.beNotNull(user) ? mapper2v(user) : null;
    }

    @Override
    public void deleteByIds(Set<String> uids, String tenantId) {
        Assert.CheckArgument(uids);
        Assert.CheckArgument(tenantId);
        SysTenantDto tenant = tenantService.getIt(tenantId);
        if (Checker.beNull(tenant)) {
            throw new ServerRuntimeException(ExceptionEnum.CAN_NOT_FIND_ID_OF_TENANT, tenantId);
        }
        if (MAX_IN_BATCH_SIZE < uids.size()) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.INVALID_DATA_NUMBER_OF_BATCH_SIZE, MAX_IN_BATCH_SIZE);
        }
        List<SysTenantUserDto> list = gets(uids);
        if (list.size() != uids.size()) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.INVALID_DATA_NUMBER_OF_BATCH_SIZE);
        }
        for (SysTenantUserDto u : list) {
            if (Boolean.TRUE.equals(u.getBeSuper())) {
                throw new ServerRuntimeException(ExceptionEnum.TENANT_ROOT_USER_NOT_BE_DELETED);
            }
//            String rootUser = tenant.getRoot() + Strings.UNDERSCORE + Strings.SUPER;
//            if (rootUser.equalsIgnoreCase(u.getUserName())) {
//                throw new ServerRuntimeException(ExceptionEnum.TENANT_ROOT_USER_NOT_BE_DELETED);
//            }
        }
        userRoleService.deleteByUsers(uids, tenantId);
        userGroupService.deleteByUsers(uids, tenantId);
        userPositionService.deleteByUsers(uids, tenantId);
        userBusinessPermService.deleteByUsers(uids, tenantId);
        positionUserEntrustService.deleteByUsers(uids, tenantId);
        super.deletes(uids);
    }

    @Override
    public void deleteByTenant(Set<String> tenantIds) {
        Assert.CheckArgument(tenantIds);
        baseMapper.delete(lambdaQuery().in(SysTenantUser::getTenantId, tenantIds));
    }

    @Override
    public void updateUserSecret(String userId, String secret) {
        SysTenantUserDto u = super.getIt(userId);
        Assert.CheckArgument(u, AuthorizationExceptionEnum.ID_OF_USER_NOT_EXIST, userId);
//        u.setSecretKey(secret);
        super.updateIt(new SysTenantUserDto().setId(userId).setSecretKey(secret));
    }

    @Override
    public void doSave(SysTenantUserDto user) {
        Assert.CheckArgument(user);
        doSetTenantId(user);
        Assert.CheckArgument(user.getTenantId());
        Assert.CheckArgument(user.getUserName());
        SysTenantUser dbUser = this.baseMapper.selectOne(lambdaQuery().eq(SysTenantUser::getTenantId, user.getTenantId()).eq(SysTenantUser::getUserNo, user.getUserName()));
        if (Checker.beNotNull(dbUser)) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.USER_NAME_HAS_EXIST, dbUser.getUserNo());
        }
        user.setUserName(user.getUserName().trim())
                .setUserRealEnName(Checker.beNotEmpty(user.getUserRealEnName()) ? user.getUserRealEnName().trim() : null)
                .setUserRealCnName(Checker.beNotEmpty(user.getUserRealCnName()) ? user.getUserRealCnName().trim() : null);
        StateWorkFlow.doInitState(user);
        String pinyin = CharacterUtil.getShortPinYin(user.getUserRealCnName());
        user.setPinYin(pinyin).setFullPinYin(CharacterUtil.getFullPinYin(user.getUserRealCnName()));
        super.saveIt(user);
    }

    @Override
    public void doUpdate(SysTenantUserDto user) {
        Assert.CheckArgument(user);
        Assert.CheckArgument(user.getUserName());
        doSetTenantId(user);
        user.setUserPasswd(null);
        String pinyin = CharacterUtil.getShortPinYin(user.getUserRealCnName());
        user.setPinYin(pinyin).setFullPinYin(CharacterUtil.getFullPinYin(user.getUserRealCnName()));
        super.updateIt(user);
    }

    @Override
    public void updatePassword(SysTenantUserDto user) {
        SysTenantUserDto userDto = super.getIt(user.getId());
        user.setUserPasswd(DigestUtils.md5Hex(DigestUtils.md5Hex(user.getUserPasswd())));
        user.setNewPassword(DigestUtils.md5Hex(DigestUtils.md5Hex(user.getNewPassword())));
        if (!user.getUserPasswd().equals(userDto.getUserPasswd())) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.ORIGINAL_PASSWROD_FAIL);
        }
        user.setUserPasswd(user.getNewPassword());
        user.setNewPassword(null);
        super.updateIt(user);
    }

    private void updateStateByIds(Set<String> ids, TenantUserAction action) {
        Assert.CheckArgument(ids);
        Assert.CheckArgument(action);
        List<SysTenantUserDto> users = super.gets(ids);
        if (users.size() != ids.size()) {
            throw new ServerRuntimeException(ExceptionEnum.IDS_CONTAINS_INVALID_ID);
        }
        for (SysTenantUserDto u : users) {
            StateWorkFlow.doProcess(u, action);
        }
        super.updateItBatch(users);
    }

    @Override
    public void activeUsers(Set<String> ids) {
        Assert.CheckArgument(ids);
        List<SysTenantUserDto> users = super.gets(ids);
        if (users.size() != ids.size()) {
            throw new ServerRuntimeException(ExceptionEnum.IDS_CONTAINS_INVALID_ID);
        }
        for (SysTenantUserDto u : users) {
            StateWorkFlow.doProcess(u, TenantUserAction.ACTIVE);
            if (Checker.beEmpty(u.getUserEmail()) && !Patterns.VALID_EMAIL_ADDRESS_REGEX.matcher(u.getUserEmail()).find()) {
                throw new ServerRuntimeException(AuthorizationExceptionEnum.USER_EMAIL_INVALID_CANNOT_SEND_PASSWORD_EMAIL);
            }
            //生成密码
            String password = RandomStringUtils.randomAlphanumeric(6);
            u.setUserPasswd(DigestUtils.md5Hex(DigestUtils.md5Hex(password))).setUnencryptPassword(password);
        }
        super.updateItBatch(users);
        //发送邮件
        outHandlerService.sendTenantUserCreatedEmail(users);
    }

    @Override
    public void lockUsers(Set<String> ids) {
        updateStateByIds(ids, TenantUserAction.LOCK);
    }

    @Override
    public void unlockUsers(Set<String> ids) {
        updateStateByIds(ids, TenantUserAction.UNLOCK);
    }

    @Override
    public void stopUsers(Set<String> ids) {
        updateStateByIds(ids, TenantUserAction.STOP);
    }

    @Override
    public void unstopUsers(Set<String> ids) {
        updateStateByIds(ids, TenantUserAction.UNSTOP);
    }

    @Override
    public void resetPasswd(Set<String> ids) {
        Assert.CheckArgument(ids);
        List<SysTenantUserDto> users = super.gets(ids);
        if (users.size() != ids.size()) {
            throw new ServerRuntimeException(ExceptionEnum.IDS_CONTAINS_INVALID_ID);
        }
        for (SysTenantUserDto u : users) {
            if (Checker.beEmpty(u.getUserEmail()) && !Patterns.VALID_EMAIL_ADDRESS_REGEX.matcher(u.getUserEmail()).find()) {
                throw new ServerRuntimeException(AuthorizationExceptionEnum.USER_EMAIL_INVALID_CANNOT_SEND_PASSWORD_EMAIL);
            }
            //生成密码
            String password = RandomStringUtils.randomAlphanumeric(6);
            u.setUserPasswd(DigestUtils.md5Hex(DigestUtils.md5Hex(password))).setUnencryptPassword(password).setOpenId(Strings.EMPTY);
        }
        super.updateItBatch(users);
        //发送邮件
        outHandlerService.sendTenantUserRestPasswdEmail(users);
    }

    @Override
    public List<SysTenantUserDto> listAllGroupUsers(String tenantId) {
        if (Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        return mapper(baseMapper.listAllGroupUsers(tenantId));
    }

    @Override
    public SearchResult<SysTenantUserDto> searchByGroupCondition(SysTenantUserDto user, int pageNumber, int pageSize) {
        List<SysTenantUserDto> vlist = Lists.newArrayList();
        doSetTenantId(user);
        if (Checker.beEmpty(user.getGroupId()) || Checker.beEmpty(user.getTenantId())) {
            return new SearchResult<SysTenantUserDto>(0, vlist);
        }
        int offset = offsetCurrent(pageNumber, pageSize);
        List<SysTenantUser> list = this.baseMapper.searchByGroupCondition(user.getGroupId(), user.getTenantId(), pageSize, offset);
        int count = this.baseMapper.countByGroupCondition(user.getGroupId(), user.getTenantId());
        return new SearchResult<>(count, mapper(list));
    }

    @Override
    public SearchResult<SysTenantUserDto> searchForNotAssignGroup(SysTenantUserDto user, int pageNumber, int pageSize) {
        doSetTenantId(user);
        if (Checker.beEmpty(user.getTenantId())) {
            return new SearchResult<>();
        }
        int offset = offsetCurrent(pageNumber, pageSize);
        List<SysTenantUser> list = this.baseMapper.listForNotAssignGroup(user.getTenantId(), pageSize, offset);
        int count = this.baseMapper.countForNotAssignGroup(user.getTenantId());
        return new SearchResult<>(count, mapper(list));
    }

    @Override
    public SysTenantUserDto doInitUser(String tenantId) {
        SysTenantDto tenant = tenantService.getIt(tenantId);
        if (Checker.beNull(tenant)) {
            throw new ServerRuntimeException(ExceptionEnum.CAN_NOT_FIND_ID_OF_TENANT, tenantId);
        }
        SysTenantUserDto user = new SysTenantUserDto();
        user.setTenantId(tenantId).setUserName((tenant.getRoot() + Strings.UNDERSCORE + Strings.SUPER).toLowerCase())
                .setUserRealEnName("Super Admin").setUserRealCnName("超级管理员").setBeSuper(Boolean.TRUE);
        user.setUserPasswd(DigestUtils.md5Hex(DigestUtils.md5Hex("123456")));
        doSave(user);
        return user;
    }

    @Override
    public SysTenantUserDto getSuper(String tenantId) {
        SysTenantDto tenant = tenantService.getIt(tenantId);
        if (Checker.beNull(tenant)) {
            throw new ServerRuntimeException(ExceptionEnum.CAN_NOT_FIND_ID_OF_TENANT, tenantId);
        }
        SysTenantUser user = baseMapper.selectOne(lambdaQuery()
                .eq(SysTenantUser::getBeSuper, Boolean.TRUE)
//                .eq(SysTenantUser::getUserNo, (tenant.getRoot() + Strings.UNDERSCORE + Strings.SUPER).toLowerCase())
                .eq(SysTenantUser::getTenantId, tenantId));
        return mapper2v(user);
    }

    @Override
    public List<SysTenantUserDto> listByKeyWordAndType(String tenantId, String userTag, String KeyWord) {
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        if (Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }
        return mapper(super.baseMapper.listByKeyWordAndType(tenantId, userTag, KeyWord));
    }

    @Override
    public List<SysTenantUserDto> listAllGroupUsersByTag(String tenantId, String tag) {
        return mapper(baseMapper.listAllGroupUsersByTag(tenantId, tag));
    }
}
