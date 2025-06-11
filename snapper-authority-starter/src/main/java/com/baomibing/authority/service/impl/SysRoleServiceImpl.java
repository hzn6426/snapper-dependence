
package com.baomibing.authority.service.impl;

import com.baomibing.authority.action.RoleAction;
import com.baomibing.authority.constant.PermActionConst;
import com.baomibing.authority.constant.PermConnectConst;
import com.baomibing.authority.dto.RoleDto;
import com.baomibing.authority.dto.UserDto;
import com.baomibing.authority.dto.UserRoleDto;
import com.baomibing.authority.entity.SysRole;
import com.baomibing.authority.mapper.SysRoleMapper;
import com.baomibing.authority.service.*;
import com.baomibing.authority.state.CommonState;
import com.baomibing.core.annotation.Action;
import com.baomibing.core.annotation.ActionConnect;
import com.baomibing.core.common.Assert;
import com.baomibing.core.common.SearchResult;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.core.process.StateWorkFlow;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.SnowflakeIdWorker;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色服务
 *
 * @author zening
 * @since 1.0.0
 */
@Service
public class SysRoleServiceImpl extends MBaseServiceImpl<SysRoleMapper, SysRole, RoleDto> implements SysRoleService {
    @Autowired private SysUserRoleService userRoleService;
    @Autowired private SysPositionRoleService positionRoleService;
    @Autowired private SysRoleResourceService roleResourceService;
    @Autowired private SysUserService userService;
    
    @Action(value = PermActionConst.ROLE_SEARCH)
    @ActionConnect(value = {PermConnectConst.SELECT_LIST, PermConnectConst.SELECT_COUNT})
    @Override
    public SearchResult<RoleDto> search(RoleDto roleDto, int pageNumber, int pageSize) {
        LambdaQueryWrapper<SysRole> wrapper = lambdaQuery().like(Checker.beNotEmpty(roleDto.getRoleName()), SysRole::getRoleName, roleDto.getRoleName())
                .eq(Checker.beNotEmpty(roleDto.getState()), SysRole::getState, roleDto.getState()).eq(SysRole::getBeDefault, false);
        return super.search(wrapper, pageNumber, pageSize);
    }

    @Override
    public void saveRole(RoleDto role) {
        Assert.CheckArgument(role);
        StateWorkFlow.doInitState(role);
        super.saveIt(role);;
    }

    @Override
    public void updateRole(RoleDto role) {
        RoleDto dbRole = super.getIt(role.getId());
        assertBeLock(dbRole);
        super.updateIt(role);
    }

    private void updateStateByIds(Set<String> ids, RoleAction action) {
        Assert.CheckArgument(ids);
        Assert.CheckArgument(action);
        List<RoleDto> roles = super.gets(ids);
        if (roles.size() != ids.size()) {
            throw new ServerRuntimeException(ExceptionEnum.IDS_CONTAINS_INVALID_ID);
        }
        for (RoleDto role : roles) {
            assertBeLock(role);
            StateWorkFlow.doProcess(role, action);
        }
        super.updateItBatch(roles);
    }
    
    
    @Transactional
    @Override
    public void use(Set<String> ids) {
        updateStateByIds(ids, RoleAction.USE);
    }
    
    @Transactional
    @Override
    public void stop(Set<String> ids) {
        updateStateByIds(ids, RoleAction.STOP);
    }
    
    @Transactional
    @Override
    public void deleteRoles(Set<String> ids) {
        Assert.CheckArgument(ids);
        List<RoleDto> roles = super.gets(ids);
        for (RoleDto r : roles) {
            assertBeLock(r);
            StateWorkFlow.doAssertDelete(r);
        }
        userRoleService.deleteByRoles(ids);
        positionRoleService.deleteByRoles(ids);
        roleResourceService.deleteByRoles(ids);
        super.deleteItBatch(roles);
        
        
    }


    @Action(PermActionConst.ROLE_LIST_ALL)
    @ActionConnect(value = {PermConnectConst.SELECT_LIST})
    @Override
    public List<RoleDto> listAllRoles() {
        //当前拥有的 + 权限限制的

        List<RoleDto> ownerRoles = userRoleService.listRolesByGroupAndUser(currentUserGroupId(), currentUserId());
        ownerRoles = ownerRoles.stream().filter(r -> Boolean.FALSE.equals(r.getBeDefault())).collect(Collectors.toList());
        Set<String> roleIds = ownerRoles.stream().map(RoleDto::getId).collect(Collectors.toSet());

        LambdaQueryWrapper<SysRole> wrapper = lambdaQuery();
        wrapper.eq(SysRole::getState, CommonState.ACTIVE);
        wrapper.eq(SysRole::getBeSuper, 0).eq(SysRole::getBeDefault, 0);
        List<RoleDto> targets =  mapper(this.baseMapper.selectList(wrapper));

        List<RoleDto> notInOwners = targets.stream().filter(r -> !roleIds.contains(r.getId())).collect(Collectors.toList());

        ownerRoles.addAll(notInOwners);
        return ownerRoles;
    }

    @Override
    public RoleDto getDefaultRoleByUser(String userNo) {
        SysRole role = baseMapper.selectOne(lambdaQuery().eq(SysRole::getBeDefault, 1).eq(SysRole::getRoleName, (Strings.ROLE + Strings.UNDERSCORE + userNo).toLowerCase()));
        return mapper2v(role);
    }

    @Override
    public RoleDto makeDefaultRoleByUser(String userNo, String groupId) {
        checkArgument(userNo);
        checkArgument(groupId);
        RoleDto role = getDefaultRoleByUser(userNo);
        if (Checker.beNotNull(role)) {
            return role;
        }
        role = new RoleDto();
        role.setBeDefault(Boolean.TRUE).setRoleName((Strings.ROLE + Strings.UNDERSCORE + userNo).toLowerCase()).setGroupId(groupId).setId(SnowflakeIdWorker.getId());
        StateWorkFlow.doInitState(role);
        StateWorkFlow.doProcess(role, RoleAction.USE);
        super.saveIt(role);
        UserDto user = userService.getByUserNo(userNo);
        UserRoleDto ur = new UserRoleDto().setUserId(user.getId()).setRoleId(role.getId()).setOrgId(groupId);
        userRoleService.saveIt(ur);
        return role;
    }

    @Override
    public void deleteDefaultsByUsers(Set<String> userIds) {
        Assert.CheckArgument(userIds);
        List<UserDto> users = userService.gets(userIds);
        Set<String> roleNames = users.stream().map(u -> (Strings.ROLE + Strings.UNDERSCORE + u.getUserName()).toLowerCase()).collect(Collectors.toSet());
        baseMapper.delete(lambdaQuery().eq(SysRole::getBeDefault, 1).in(SysRole::getRoleName, roleNames));
    }

    @Override
    public List<RoleDto> listRolesByUser(String userId, String orgId) {
        return userRoleService.listRolesByGroupAndUser(orgId, userId);

    }
}
