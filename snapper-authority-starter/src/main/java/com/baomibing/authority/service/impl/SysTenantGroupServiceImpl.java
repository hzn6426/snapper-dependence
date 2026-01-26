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

import com.baomibing.authority.constant.enums.PositionGroupEnum;
import com.baomibing.authority.constant.enums.UserGroupEnum;
import com.baomibing.authority.dto.*;
import com.baomibing.authority.entity.SysTenantGroup;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.mapper.SysTenantGroupMapper;
import com.baomibing.authority.service.*;
import com.baomibing.authority.state.UserState;
import com.baomibing.authority.vo.TenantGroupUserVo;
import com.baomibing.core.common.Assert;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.core.wrap.CommonTreeWrap;
import com.baomibing.core.wrap.GroupIntervalWrap;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.constant.TenantRedisKeyConstant;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * SysTenantGroupServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysTenantGroupServiceImpl extends MBaseServiceImpl<SysTenantGroupMapper, SysTenantGroup, SysTenantGroupDto> implements SysTenantGroupService {
    @Autowired private CodeService codeService;
    @Autowired private SysTenantUserService userService;
    @Autowired private SysTenantPositionService positionService;
    @Autowired private SysTenantUserGroupService userGroupService;
    @Autowired private SysTenantUserPositionService userPositionService;
    @Autowired private SysTenantUserRoleService userRoleService;
    @Autowired private SysTenantUserUsetService userUsetService;
    @Autowired private SysTenantService tenantService;

    @Override
    public SysTenantGroupDto getIt(String tenantId, String id) {
        if (Checker.beEmpty(tenantId) || Checker.beEmpty(id)) {
            return null;
        }
        return mapper2v(baseMapper.selectOne(lambdaQuery().eq(SysTenantGroup::getTenantId, tenantId).eq(SysTenantGroup::getId, id)));
    }

    private List<SysTenantGroupDto> gets(String tenantId, Set<String> ids) {
        if (Checker.beEmpty(tenantId) || Checker.beEmpty(ids)) {
            return Lists.newArrayList();
        }
        return mapper(baseMapper.selectList(lambdaQuery().eq(SysTenantGroup::getTenantId, tenantId).in(SysTenantGroup::getId, ids)));
    }

    /**
     * 获取子组织序列号的最大值
     */
    private final BiFunction<String, String, Integer> getMaxIdByParentGroupId = (tenantId, pid) -> {
        //获取子组织列表，为空则最大值返回0
        List<SysTenantGroup> list = super.baseMapper.selectList(lambdaQuery().eq(SysTenantGroup::getParentId, pid).eq(SysTenantGroup::getTenantId, tenantId));
        if (Checker.beNull(list)) {
            return 0;
        }
        //获取子组织ID中的最大值
        return list.stream().mapToInt(group -> Integer.parseInt(group.getId().replace(pid, ""))).max().getAsInt();
    };

    private final Supplier<String> getMaxId = () -> {
        String maxId = baseMapper.getMaxId();
        return Checker.beEmpty(maxId) ? null : maxId;
    };

    @Override
    public List<GroupIntervalWrap> unionGroupInterval(List<SysTenantGroupDto> groupList) {
        List<GroupIntervalWrap> list = Lists.newArrayList();
        if (Checker.beEmpty(groupList)) {
            return list;
        }
        Map<String, String> groupMap = groupList.stream().collect(Collectors.toMap(g -> g.getGlft() + "_" + g.getGrht(), g -> g.getId(), (v1, v2) -> v1));
        //闭区间求并集
        RangeSet<Integer> rs = TreeRangeSet.create();
        groupList.forEach(g -> rs.add(Range.closed(g.getGlft(), g.getGrht())));
        rs.asRanges().forEach(r -> list.add(new GroupIntervalWrap(groupMap.get(r.lowerEndpoint() + "_" + r.upperEndpoint()), r.lowerEndpoint(), r.upperEndpoint())));
        return list;
    }

    @Override
    public List<SysTenantGroupDto> listChildrenByParent(String tenantId, String parentId) {
        if (Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        SysTenantGroupDto group = getIt(tenantId, parentId);
        if (Checker.beNull(group)) {
            return Lists.newArrayList();
        }
        LambdaQueryWrapper<SysTenantGroup> wrapper = lambdaQuery();
        wrapper.eq(SysTenantGroup::getTenantId, tenantId).gt(SysTenantGroup::getGlft, group.getGlft()).lt(SysTenantGroup::getGrht, group.getGrht());
        List<SysTenantGroup> list = this.baseMapper.selectList(wrapper);
        return Checker.beEmpty(list) ? Lists.newArrayList() : mapper(list);
    }

    @Override
    public SysTenantGroupDto saveChild(SysTenantGroupDto groupDto, boolean beCompany) {
        Assert.CheckArgument(groupDto);
        doSetTenantId(groupDto);
        Assert.CheckArgument(groupDto.getTenantId());
        if (Boolean.TRUE.equals(beCompany)) {
            SysTenantGroupDto parent = getRootGroup(groupDto.getTenantId());
            if (Checker.beNull(parent)) {
//                throw new ServerRuntimeException()
            }
            groupDto.setParentId(parent.getId());
        }
        if (Checker.beNull(groupDto.getParentId())) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.GROUP_PARENT_NOT_EXIST);
        }

        SysTenantGroupDto parentGroup = getIt(groupDto.getTenantId(), groupDto.getParentId());
        if (Checker.beNull(parentGroup) && Boolean.FALSE.equals(groupDto.getBeRoot())) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.GROUP_PARENT_NOT_EXIST);
        } else {
            parentGroup = getIt(groupDto.getParentId());
        }

        List<SysTenantGroup> groups = this.baseMapper.selectList(lambdaQuery().eq(SysTenantGroup::getTenantId, groupDto.getTenantId()).eq(SysTenantGroup::getParentId, groupDto.getParentId())
                .eq(SysTenantGroup::getGroupName, groupDto.getGroupName()));

        if (Checker.beNotEmpty(groups)) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.GROUP_NAME_HAS_EXIST);
        }
        groupDto.setGroupLevel(parentGroup.getGroupLevel() + 1);
        final int left = parentGroup.getGlft();
        if (Boolean.TRUE.equals(groupDto.getBeRoot())) {
            this.baseMapper.updateRootAddParentRight(left);
            this.baseMapper.updateRootAddParentLeft(left);
        } else {
            this.baseMapper.updateAddParentRight(groupDto.getTenantId(), left);
            this.baseMapper.updateAddParentLeft(groupDto.getTenantId(), left);
        }


        groupDto.setGlft(left + 1).setGrht(left + 2);

        if (Checker.beEmpty(groupDto.getId())) {
            String id = codeService.makeTenantOrderedSuffix(TenantRedisKeyConstant.TENANT_GROUP_PREFIX + groupDto.getParentId(), 2, getMaxIdByParentGroupId, groupDto.getTenantId(), groupDto.getParentId());
            groupDto.setId(groupDto.getParentId() + id);
        }
        saveIt(groupDto);
        return groupDto;
    }

    private void assertGroupsExist(String tenantId, Set<String> groupIds) {
        Assert.CheckArgument(tenantId);
        Assert.CheckArgument(groupIds);
        List<SysTenantGroupDto> groups = gets(tenantId, groupIds);
        if (Checker.beEmpty(groups) || groups.size() != groupIds.size()) {
            throw new ServerRuntimeException(ExceptionEnum.IDS_CONTAINS_INVALID_ID);
        }
    }

    private void assertPositionsExist(Set<String> positions) {
        Assert.CheckArgument(positions);
        List<SysTenantPositionDto> positionList = positionService.gets(positions);
        if (Checker.beEmpty(positionList) || positions.size() != positionList.size()) {
            throw new ServerRuntimeException(ExceptionEnum.IDS_CONTAINS_INVALID_ID);
        }
    }

    private void assertUsersExist(Set<String> users) {
        Assert.CheckArgument(users);
        List<SysTenantUserDto> userList = userService.gets(users);
        if (Checker.beNull(userList)) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.ID_OF_USER_NOT_EXIST, users);
        }
    }

    private void addUsersToGroup(Set<String> users, String gid, String tenantId) {
        List<SysTenantUserGroupDto> existGroupUsers = userGroupService.listByUsersAndGroup(users, gid, tenantId);
        if (Checker.beNotEmpty(existGroupUsers)) {
            return;
//            users.removeAll(existGroupUsers.stream().map(SysTenantUserGroupDto::getUserId).collect(Collectors.toSet()));
        }
        List<SysTenantUserGroupDto> dtos = new ArrayList<>();
        users.forEach(user -> {
            SysTenantUserGroupDto userGroupDto = new SysTenantUserGroupDto().setGroupId(gid).setUserId(user).setTenantId(tenantId);
            dtos.add(userGroupDto);
        });
        userGroupService.saveItBatch(dtos);
    }

    @Override
    public void addUsers(String tenantId, String pid, String gid, Set<String> users) {
        Assert.CheckArgument( gid, users);
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        final String tid = tenantId;
        Assert.CheckArgument(tenantId);
        assertGroupsExist(tenantId, Sets.newHashSet(gid));
        assertUsersExist(users);
        if (Checker.beNotEmpty(pid)) {
            assertPositionsExist(Sets.newHashSet(pid));
            userPositionService.deleteByPositions(Sets.newHashSet(pid), tenantId);
//            userPositionService.deleteByUsers(users, tenantId);
//            userPositionService.deleteByGroupAndUsers(tenantId, gid, users);
        }
//        userGroupService.deleteByUsers(users, tenantId);
        //增加用户组织时，Set可能会变，复制一份作为参数
        Set<String> groupUserParam = new HashSet<>(users);
        //查询该组织下是否已存在用户，若已存在则不必重复插入
        addUsersToGroup(groupUserParam, gid, tenantId);
        if (Checker.beNotEmpty(pid)) {
            List<SysTenantUserPositionDto> dtos = new ArrayList<>();
            users.forEach(user -> {
                SysTenantUserPositionDto userPositionDto = new SysTenantUserPositionDto().setPositionId(pid).setUserId(user).setGroupId(gid).setTenantId(tid);
                dtos.add(userPositionDto);
            });
            userPositionService.saveItBatch(dtos);
        }
    }

    @Override
    public void doMoveGroupUsers(String tenantId, String ogid, String togid, Set<String> users) {
        Assert.CheckArgument(ogid, togid, users);
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        final String tid = tenantId;
        Assert.CheckArgument(tenantId);
        assertGroupsExist(tenantId, Sets.newHashSet(ogid, togid));
        assertUsersExist(users);
        userGroupService.deleteByGroupIdAndUsers(users, ogid, tenantId);
        userPositionService.removePositionUsers(tenantId, ogid, users);
        addUsersToGroup(users, togid, tenantId);
        //更新角色
        List<SysTenantUserRoleDto> urs =  userRoleService.listByGroupAndUsers(tenantId, ogid, users);
        urs.forEach(ur -> ur.setOrgId(togid).setTenantId(tid));
        if (Checker.beNotEmpty(urs)) {
            userRoleService.updateItBatch(urs);
        }
        //更新用户组
        List<SysTenantUserUsetDto> uus = userUsetService.listByGroupAndUsers(tenantId, ogid, users);
        uus.forEach(uu -> uu.setOrgId(togid));
        if (Checker.beNotEmpty(uus)) {
            userUsetService.updateItBatch(uus);
        }
    }

    @Override
    public void removeGroupUsers(String tenantId, String gid, Set<String> users) {
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        Assert.CheckArgument(tenantId);
        Assert.CheckArgument(gid, users);
        assertGroupsExist(tenantId, Sets.newHashSet(gid));
        assertUsersExist(users);
        userGroupService.deleteByGroupIdAndUsers(users, gid, tenantId);
        userPositionService.removePositionUsers(tenantId, gid, users);
    }

    @Override
    public void doAssignUserPosition(String tenantId, String uid, String gid, String pid) {
        Assert.CheckArgument(uid, gid);
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        Assert.CheckArgument(tenantId);
        assertUsersExist(Sets.newHashSet(uid));
        assertGroupsExist(tenantId, Sets.newHashSet(gid));
        userPositionService.deleteByGroupAndUsers(tenantId, gid, Sets.newHashSet(uid));

        assertPositionsExist(Sets.newHashSet(pid));
        SysTenantUserPositionDto userPositionDto = new SysTenantUserPositionDto();
        userPositionDto.setUserId(uid).setPositionId(pid).setGroupId(gid).setTenantId(tenantId);
        userPositionService.saveIt(userPositionDto);

    }

    private List<SysTenantGroupDto> listAllChildrenByParent(String tenantId, String gid) {
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        return mapper(baseMapper.selectList(lambdaQuery().eq(SysTenantGroup::getTenantId, tenantId).likeRight(SysTenantGroup::getId, gid)));
    }

    @Override
    public void deleteGroup(String tenantId, String gid) {
        Assert.CheckArgument(gid);
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        Assert.CheckArgument(tenantId);
        assertGroupsExist(tenantId, Sets.newHashSet(gid));
        SysTenantGroupDto group = getIt(tenantId, gid);
        if (Boolean.TRUE.equals(group.getBeRoot())) {
            throw new ServerRuntimeException(ExceptionEnum.ROOT_GROUP_NOT_BE_DELETE);
        }
        List<SysTenantUserGroupDto> ugs = userGroupService.listItAndChildByGroup(tenantId, gid);
        if (Checker.beNotEmpty(ugs)) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.GROUP_HAS_USERS_NOT_BE_DELETE);
        }
        //查询当前组织及其子组织ID
        Set<String> deleteGroupIds = listAllChildrenByParent(tenantId, gid).stream().map(SysTenantGroupDto::getId).collect(Collectors.toSet());
        //逻辑删除 sys_group
//        baseMapper.update(null, new LambdaUpdateWrapper<SysGroup>().in(SysGroup::getId, deleteGroupIds)
//                .set(SysGroup::getBeDeleted, true));

        //物理删除 sys_user_group
        userGroupService.deleteByGroups(deleteGroupIds, tenantId);
        //物理删除 sys_position
        positionService.deleteByGroups(deleteGroupIds, tenantId);
        //删除组织
        this.baseMapper.deleteById(gid);
    }

    @Override
    public void deleteByTenant(Set<String> tenantIds) {
        userGroupService.deleteByTenant(tenantIds);
        positionService.deleteByTenant(tenantIds);
        baseMapper.delete(lambdaQuery().in(SysTenantGroup::getTenantId, tenantIds));
    }

    @Override
    public void updateGroup(SysTenantGroupDto group) {
        Assert.CheckArgument(group);
        doSetTenantId(group);
        Assert.CheckArgument(group.getParentId());
        Assert.CheckArgument(group.getTenantId());
        List<SysTenantGroup> groups = this.baseMapper.selectList(lambdaQuery().eq(SysTenantGroup::getParentId, group.getParentId()).eq(SysTenantGroup::getGroupName, group.getGroupName()).eq(SysTenantGroup::getTenantId, group.getTenantId()));
        if (Checker.beNotEmpty(groups)) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.GROUP_NAME_HAS_EXIST);
        }
        super.updateIt(group);
    }

    @Override
    public TenantGroupUserVo getUserDetail(String tenantId, String uid, String gid) {
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        if (Checker.beEmpty(uid) || Checker.beEmpty(gid) || Checker.beEmpty(tenantId)) {
            return null;
        }
        TenantGroupUserVo groupUserVo = new TenantGroupUserVo();
        groupUserVo.setGroupId(gid);
        groupUserVo.setUserId(uid);
        groupUserVo.setTenantId(tenantId);
        SysTenantPositionDto positionDto = userPositionService.getPositionByUserAndGroup(tenantId, uid, gid);
        if (Checker.beNotNull(positionDto)) {
            groupUserVo.setPositionId(positionDto.getId());
        }
        return groupUserVo;
    }

    @Override
    public List<SysTenantGroupDto> listGroupsByUser(String tenantId, String uid) {
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        if (Checker.beEmpty(uid) || Checker.beEmpty(tenantId)) {
            return new ArrayList<>();
        }
        return mapper(baseMapper.listGroupsByUser(tenantId, uid));
    }

    @Override
    public List<CommonTreeWrap> treeAllGroupsAndUsers(String tenantId) {
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        List<SysTenantGroup> groups = this.baseMapper.selectList(lambdaQuery().isNotNull(SysTenantGroup::getParentId).eq(SysTenantGroup::getTenantId, tenantId));
        List<SysTenantUserDto> users = userService.listAllGroupUsers(tenantId);

        List<CommonTreeWrap> tlist = Lists.newArrayList();

        List<CommonTreeWrap> treeList =
                groups.stream().map(group -> new CommonTreeWrap().setKey(group.getId()).setTitle(group.getGroupName()).setIsLeaf(true).setParentId(group.getParentId()).setTag(UserGroupEnum.GROUP.name())).collect(Collectors.toList());
        // 创建map结构用来临时存储对象
        Map<String, CommonTreeWrap> gMap = treeList.stream().collect(Collectors.toMap(CommonTreeWrap::getKey, Function.identity(), (key1, key2) -> key2));
        // 将列表转化成树结构
        treeList.forEach(group -> {
            String pid = group.getParentId();
            if (Checker.beNotNull(gMap.get(pid))) {
                CommonTreeWrap pGroup = gMap.get(pid);
                if (Checker.beEmpty(pGroup.getChildren())) {
                    pGroup.setIsLeaf(false);
                    pGroup.setChildren(Lists.newArrayList());
                }
                group.setParentGroupName(pGroup.getTitle());
                pGroup.getChildren().add(group);

            } else {
                group.setIsLeaf(false);
                tlist.add(group);
            }
        });

        users.forEach(u -> {
            String pid = u.getGroupId();
            if (Checker.beNotNull(gMap.get(pid))) {
                CommonTreeWrap pGroup = gMap.get(pid);
                if (Checker.beEmpty(pGroup.getChildren())) {
                    pGroup.setIsLeaf(false);
                    pGroup.setChildren(Lists.newArrayList());
                }
                String state = "";
                if (UserState.STOPPED.name().equals(u.getState())) {
                    state = "(停用)";
                } else if (UserState.UNACTIVE.name().equals(u.getState())) {
                    state = "(未激活)";
                } else if (UserState.LOCKED.name().equals(u.getState())) {
                    state = "(已锁定)";
                }

                //key为唯一，存在一个用户位于两个组织，此处设置组织ID+用户ID的方式
                pGroup.getChildren().add(new CommonTreeWrap().setKey(pid + "#" + u.getId())
                        .setTitle(u.getUserRealCnName() + state).setParentId(pid).setParentGroupName(pGroup.getTitle()).setTag(UserGroupEnum.USER.name()).setIsLeaf(true));
            }
        });

        return tlist;
    }

    @Override
    public List<CommonTreeWrap> treeAllGroupsAndPositions(String tenantId) {
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        List<SysTenantGroup> groups = this.baseMapper.selectList(lambdaQuery().isNotNull(SysTenantGroup::getParentId).eq(SysTenantGroup::getTenantId, tenantId));
        List<SysTenantPositionDto> positions = positionService.listAllGroupPositions(tenantId);

        List<CommonTreeWrap> tlist = Lists.newArrayList();

        List<CommonTreeWrap> treeList =
                groups.stream().map(group -> new CommonTreeWrap().setKey(group.getId()).setTitle(group.getGroupName()).setIsLeaf(true).setParentId(group.getParentId()).setTag(PositionGroupEnum.GROUP.name())).collect(Collectors.toList());
        // 创建map结构用来临时存储对象
        Map<String, CommonTreeWrap> gMap = treeList.stream().collect(Collectors.toMap(CommonTreeWrap::getKey, Function.identity(), (key1, key2) -> key2));
        // 将列表转化成树结构
        treeList.forEach(group -> {
            String pid = group.getParentId();
            if (Checker.beNotNull(gMap.get(pid))) {
                CommonTreeWrap pGroup = gMap.get(pid);
                if (Checker.beEmpty(pGroup.getChildren())) {
                    pGroup.setIsLeaf(false);
                    pGroup.setChildren(Lists.newArrayList());
                }
                group.setParentGroupName(pGroup.getTitle());
                pGroup.getChildren().add(group);

            } else {
                group.setIsLeaf(false);
                tlist.add(group);
            }
        });

        positions.forEach(p -> {
            String pid = p.getOrgId();
            if (Checker.beNotNull(gMap.get(pid))) {
                CommonTreeWrap pGroup = gMap.get(pid);
                if (Checker.beEmpty(pGroup.getChildren())) {
                    pGroup.setIsLeaf(false);
                    pGroup.setChildren(Lists.newArrayList());
                }
                //key为唯一，存在一个用户位于两个组织，此处设置组织ID+用户ID的方式
                pGroup.getChildren().add(new CommonTreeWrap().setKey(p.getId()).setTitle(p.getPostName()).setParentId(pid).setParentGroupName(pGroup.getTitle()).setTag(PositionGroupEnum.POSITION.name()).setIsLeaf(true));
            }
        });
        return tlist;
    }

    @Override
    public List<CommonTreeWrap> treeAllGroups(String tenantId) {
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        List<SysTenantGroup> groups = this.baseMapper.selectList(lambdaQuery().isNotNull(SysTenantGroup::getParentId).eq(SysTenantGroup::getTenantId, tenantId));

        List<CommonTreeWrap> tlist = Lists.newArrayList();

        List<CommonTreeWrap> treeList = groups.stream().map(group -> new CommonTreeWrap().setKey(group.getId()).setTitle(group.getGroupName()).setIsLeaf(true).setParentId(group.getParentId())).collect(Collectors.toList());
        // 创建map结构用来临时存储对象
        Map<String, CommonTreeWrap> gMap = treeList.stream().collect(Collectors.toMap(CommonTreeWrap::getKey, Function.identity(), (key1, key2) -> key2));
        // 将列表转化成树结构
        treeList.forEach(group -> {
            String pid = group.getParentId();
            if (Checker.beNotNull(gMap.get(pid))) {
                CommonTreeWrap pGroup = gMap.get(pid);
                if (Checker.beEmpty(pGroup.getChildren())) {
                    pGroup.setIsLeaf(false);
                    pGroup.setChildren(Lists.newArrayList());
                }
                group.setParentGroupName(pGroup.getTitle());
                pGroup.getChildren().add(group);

            } else {
                group.setIsLeaf(false);
                tlist.add(group);
            }
        });
        return tlist;
    }

    @Override
    public SysTenantGroupDto getParentCompanyById(String tenantId, String id) {
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        if (Checker.beEmpty(id) || Checker.beEmpty(tenantId)) {
            return null;
        }
        return mapper2v(this.baseMapper.getParentCompanyById(tenantId, id));
    }

    @Override
    public List<SysTenantGroupDto> listBranchCompanines(String tenantId) {
        tenantId = ObjectUtil.defaultIfNull(currentTenantId(), tenantId);
        LambdaQueryWrapper<SysTenantGroup> queryWrapper = lambdaQuery();
        queryWrapper.eq(SysTenantGroup::getParentId, Strings.R).eq(SysTenantGroup::getTenantId, tenantId);
        queryWrapper.select(SysTenantGroup::getId, SysTenantGroup::getGroupName);
        return mapper(super.baseMapper.selectList(queryWrapper));
    }



    @Override
    public SysTenantGroupDto doInitGroup(String tenantId) {
        Assert.CheckArgument(tenantId);
        SysTenantDto tenant = tenantService.getIt(tenantId);
        if (Checker.beNull(tenant)) {
            throw new ServerRuntimeException(ExceptionEnum.CAN_NOT_FIND_ID_OF_TENANT, tenantId);
        }
        SysTenantGroup group =  baseMapper.selectOne(lambdaQuery().eq(SysTenantGroup::getTenantId, tenantId).eq(SysTenantGroup::getBeRoot, true));
        if (Checker.beNotNull(group)) {
            return mapper2v(group);
        }
        SysTenantGroupDto root = new SysTenantGroupDto();
        root.setParentId(Strings.T).setId(codeService.makeTenantOrderedPrefix(TenantRedisKeyConstant.TENANT_GROUP_PREFIX + Strings.T, 4, getMaxId)).setGroupName(tenant.getSimpleName() + "平台").setTenantId(tenantId).setBeRoot(true);
        saveChild(root, false);
        return root;
    }

    @Override
    public SysTenantGroupDto getRootGroup(String tenantId) {
        if (Checker.beEmpty(tenantId)) {
            return null;
        }
        SysTenantGroup group =  baseMapper.selectOne(lambdaQuery().eq(SysTenantGroup::getTenantId, tenantId).eq(SysTenantGroup::getBeRoot, true));
        return mapper2v(group);
    }

    @Override
    public SysTenantGroupDto doGetOrMakeCompany(String tenantId) {
        Assert.CheckArgument(tenantId);
        SysTenantDto tenant = tenantService.getIt(tenantId);
        if (Checker.beNull(tenant)) {
            throw new ServerRuntimeException(ExceptionEnum.CAN_NOT_FIND_ID_OF_TENANT, tenantId);
        }
        SysTenantGroupDto root = getRootGroup(tenantId);
        if (Checker.beNull(root)) {
            throw new ServerRuntimeException(ExceptionEnum.CAN_NOT_FIND_ROOT_GROUP_OF_TENANT);
        }
        LambdaQueryWrapper<SysTenantGroup> queryWrapper = lambdaQuery();
        List<SysTenantGroup> companys = baseMapper.selectList(queryWrapper.eq(SysTenantGroup::getTenantId, tenantId).eq(SysTenantGroup::getParentId, root.getId()).eq(SysTenantGroup::getGroupLevel, 2));
        if (Checker.beNotEmpty(companys)) {
            return mapper2v(companys.get(0));
        }
        SysTenantGroupDto company = new SysTenantGroupDto();
        company.setGroupName(tenant.getSimpleName() + "公司").setTenantId(tenantId).setBeRoot(false);
        return saveChild(company, true);
    }

    @Override
    public List<CommonTreeWrap> treeAllGroupsAndUsersByTag(String tenantId, String userTag) {
        if (Checker.beEmpty(tenantId)) {
            return Lists.newArrayList();
        }
        List<SysTenantGroup> groups = baseMapper.selectList(lambdaQuery().isNotNull(SysTenantGroup::getParentId));
        List<SysTenantUserDto> users = userService.listAllGroupUsersByTag(tenantId, userTag);

        List<CommonTreeWrap> tlist = Lists.newArrayList();

        List<CommonTreeWrap> treeList =
                groups.stream().map(group -> new CommonTreeWrap().setKey(group.getId()).setTitle(group.getGroupName()).setIsLeaf(true)
                        .setParentId(group.getParentId()).setTag(UserGroupEnum.GROUP.name())).collect(Collectors.toList());

        // 创建map结构用来临时存储对象
        Map<String, CommonTreeWrap> gMap = treeList.stream().collect(Collectors.toMap(CommonTreeWrap::getKey, Function.identity(), (key1, key2) -> key2));
        // 将列表转化成树结构
        treeList.forEach(group -> {
            String pid = group.getParentId();
            if (Checker.beNotNull(gMap.get(pid))) {
                CommonTreeWrap pGroup = gMap.get(pid);
                if (Checker.beEmpty(pGroup.getChildren())) {
                    pGroup.setIsLeaf(false);
                    pGroup.setChildren(Lists.newArrayList());
                }
                group.setParentGroupName(pGroup.getTitle());
                pGroup.getChildren().add(group);

            } else {
                group.setIsLeaf(false);
                tlist.add(group);
            }
        });

        users.forEach(u -> {
            String pid = u.getGroupId();
            if (Checker.beNotNull(gMap.get(pid))) {
                CommonTreeWrap pGroup = gMap.get(pid);
                if (Checker.beEmpty(pGroup.getChildren())) {
                    pGroup.setIsLeaf(false);
                    pGroup.setChildren(Lists.newArrayList());
                }
                String state = "";
                if (UserState.STOPPED.name().equals(u.getState())) {
                    state = "(停用)";
                } else if (UserState.UNACTIVE.name().equals(u.getState())) {
                    state = "(未激活)";
                } else if (UserState.LOCKED.name().equals(u.getState())) {
                    state = "(已锁定)";
                }

                //key为唯一，存在一个用户位于两个组织，此处设置组织ID+用户ID的方式
                pGroup.getChildren().add(new CommonTreeWrap().setKey(pid + "#" + u.getId())
                        .setTitle(u.getUserRealCnName() + state).setParentId(pid).setParentGroupName(pGroup.getTitle()).setTag(UserGroupEnum.USER.name()).setIsLeaf(true));
            }
        });


        return tlist;
    }
}
