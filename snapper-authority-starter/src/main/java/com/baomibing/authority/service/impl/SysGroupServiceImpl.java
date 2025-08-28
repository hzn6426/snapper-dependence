
package com.baomibing.authority.service.impl;


import com.baomibing.authority.constant.PermActionConst;
import com.baomibing.authority.constant.PermConnectConst;
import com.baomibing.authority.constant.enums.PositionGroupEnum;
import com.baomibing.authority.constant.enums.UserGroupEnum;
import com.baomibing.authority.dto.*;
import com.baomibing.authority.entity.SysGroup;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.mapper.SysGroupMapper;
import com.baomibing.authority.service.*;
import com.baomibing.authority.state.UserState;
import com.baomibing.authority.vo.GroupUserVo;
import com.baomibing.core.annotation.Action;
import com.baomibing.core.annotation.ActionConnect;
import com.baomibing.core.common.Assert;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.core.wrap.CommonTreeWrap;
import com.baomibing.core.wrap.GroupIntervalWrap;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.constant.RedisKeyConstant;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.util.Checker;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 组织服务实现类
 *
 * @author zening
 * @since 1.0.0
 */
@Service
public class SysGroupServiceImpl extends MBaseServiceImpl<SysGroupMapper, SysGroup, GroupDto> implements SysGroupService {
    
    
    @Autowired private CodeService codeService;
    @Autowired private SysUserService userService;
    @Autowired private SysPositionService positionService;
    @Autowired private SysUserGroupService userGroupService;
    @Autowired private SysUserPositionService userPositionService;
    @Autowired private SysUserRoleService userRoleService;
    @Autowired private SysUserUsetService userUsetService;
    
    /**
     * 获取子组织序列号的最大值
     */
    private final Function<String, Integer> getMaxIdByParentGroupId = pid -> {
        //获取子组织列表，为空则最大值返回0
        List<SysGroup> list = super.baseMapper.selectList(lambdaQuery().eq(SysGroup::getParentId, pid));
        if (Checker.beNull(list)) {
            return 0;
        }
        //获取子组织ID中的最大值
        return list.stream().mapToInt(group -> Integer.parseInt(group.getId().replace(pid, "")))
        .max().getAsInt();
    };
    
    
//    @Override
//    public String getMaxId() {
//        return super.baseMapper.getMaxId();
//    }
    
    @Override
    public List<GroupIntervalWrap> unionGroupInterval(List<GroupDto> groupList) {
        List<GroupIntervalWrap> list = new ArrayList<>();
        if (Checker.beEmpty(groupList))
            return list;
        Map<String, String> groupMap = groupList.stream().collect(Collectors.toMap(g -> g.getGlft() + "_" + g.getGrht(), g -> g.getId(), (v1, v2) -> v1));
        //闭区间求并集
        RangeSet<Integer> rs = TreeRangeSet.create();
        groupList.forEach(g -> rs.add(Range.closed(g.getGlft(), g.getGrht())));
        rs.asRanges().forEach(r -> list.add(new GroupIntervalWrap(groupMap.get(r.lowerEndpoint() + "_" + r.upperEndpoint()), r.lowerEndpoint(), r.upperEndpoint())));
        return list;
    }
    
    
    @Override
    public List<GroupDto> listChildrenByParent(String parentId) {
        GroupDto group = super.getIt(parentId);
        if (Checker.beNull(group))
            return Lists.newArrayList();
        LambdaQueryWrapper<SysGroup> wrapper = lambdaQuery();
        wrapper.likeRight(SysGroup::getId, parentId);
        List<SysGroup> list = this.baseMapper.selectList(wrapper);
        return Checker.beEmpty(list) ? Lists.newArrayList() : mapper(list);
    }
    
    
    @Override
    @Transactional
    public void saveChild(GroupDto groupDto) {
        Assert.CheckArgument(groupDto);
        if (Checker.beNull(groupDto.getParentId())) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.GROUP_PARENT_NOT_EXIST);
        }
        GroupDto parentGroup = super.getIt(groupDto.getParentId());
        if (Checker.beNull(parentGroup)) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.GROUP_PARENT_NOT_EXIST);
        }

        List<SysGroup> groups = this.baseMapper.selectList(lambdaQuery().eq(SysGroup::getParentId, groupDto.getParentId()).eq(SysGroup::getGroupName, groupDto.getGroupName()).eq(SysGroup::getBeDeleted, false));
        if (Checker.beNotEmpty(groups)) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.GROUP_NAME_HAS_EXIST);
        }
        groupDto.setGroupLevel(parentGroup.getGroupLevel() + 1);
        final int left = parentGroup.getGlft();
        this.baseMapper.updateAddParentRight(left);
        this.baseMapper.updateAddParentLeft(left);

        groupDto.setGlft(left + 1).setGrht(left + 2);
        String id = codeService.makeOrderedSuffix(RedisKeyConstant.GROUP_PREFIX + groupDto.getParentId(), 2, getMaxIdByParentGroupId, groupDto.getParentId());
        groupDto.setId(groupDto.getParentId() + id);
        saveIt(groupDto);
    }
    
    @Override
    @Transactional
    public void addUsers(String pid, String gid, Set<String> users) {
        Assert.CheckArgument(gid, users);
        assertGroupsExist(Sets.newHashSet(gid));
        assertUsersExist(users);
        if (Checker.beNotEmpty(pid)) {
            assertPositionsExist(Sets.newHashSet(pid));
            userPositionService.deleteByPositions(Sets.newHashSet(pid));
//            userPositionService.deleteByUsers( users);
//            userPositionService.deleteByGroupAndUsers(gid, users);
        }
        //删除用户与其他组织的关联
//        userGroupService.deleteByUsers(users);
        //增加用户组织时，Set可能会变，复制一份作为参数
        Set<String> groupUserParam = new HashSet<>(users);
        //查询该组织下是否已存在用户，若已存在则不必重复插入
        addUsersToGroup(groupUserParam, gid);

        if (Checker.beNotEmpty(pid)) {
            List<UserPositionDto> dtos = new ArrayList<>();
            users.forEach(user -> {
                UserPositionDto userPositionDto = new UserPositionDto().setPositionId(pid).setUserId(user).setGroupId(gid);
                dtos.add(userPositionDto);
            });
            userPositionService.saveItBatch(dtos);
        }

    }
    
    @Override
    @Transactional
    public void doMoveGroupUsers(String ogid, String togid, Set<String> users) {
        Assert.CheckArgument(ogid, togid, users);
        List<UserDto> dbUsers = userService.gets(users);
        for (UserDto u : dbUsers) {
            assertBeLock(u);
        }
        assertGroupsExist(Sets.newHashSet(ogid, togid));
        assertUsersExist(users);
        userGroupService.deleteByGroupIdAndUsers(users, ogid);
        userPositionService.removePositionUsers(ogid, users);
        addUsersToGroup(users, togid);
        //更新角色
        List<UserRoleDto> urs =  userRoleService.listByGroupAndUsers(ogid, users);
        urs.forEach(ur -> ur.setOrgId(togid));
        if (Checker.beNotEmpty(urs)) {
            userRoleService.updateItBatch(urs);
        }
        //更新用户组
        List<UserUsetDto> uus = userUsetService.listByGroupAndUsers(ogid, users);
        uus.forEach(uu -> uu.setOrgId(togid));
        if (Checker.beNotEmpty(uus)) {
            userUsetService.updateItBatch(uus);
        }
    }
    
    
    private void addUsersToGroup(Set<String> users, String gid) {
        List<UserDto> dbUsers = userService.gets(users);
        for (UserDto u : dbUsers) {
            assertBeLock(u);
        }
        List<UserGroupDto> existGroupUsers = userGroupService.listByUsersAndGroup(users, gid);
        if (Checker.beNotEmpty(existGroupUsers)) {
            return;
//            users.removeAll(existGroupUsers.stream().map(UserGroupDto::getUserId).collect(Collectors.toSet()));
        }
        List<UserGroupDto> dtos = new ArrayList<>();
        users.forEach(user -> {
            UserGroupDto userGroupDto = new UserGroupDto().setGroupId(gid).setUserId(user);
            dtos.add(userGroupDto);
        });
        userGroupService.saveItBatch(dtos);
    }
    
    @Override
    @Transactional
    public void removeGroupUsers(String gid, Set<String> users) {
        Assert.CheckArgument(gid, users);
        List<UserDto> dbUsers = userService.gets(users);
        for (UserDto u : dbUsers) {
            assertBeLock(u);
        }
        assertGroupsExist(Sets.newHashSet(gid));
        assertUsersExist(users);
        userGroupService.deleteByGroupIdAndUsers(users, gid);
        userPositionService.removePositionUsers(gid, users);
    }
    
    @Override
    @Transactional
    public void doAssignUserPosition(String uid, String gid, String pid) {
        Assert.CheckArgument(uid, gid);
        assertUsersExist(Sets.newHashSet(uid));
        assertGroupsExist(Sets.newHashSet(gid));
        userPositionService.deleteByGroupAndUsers(gid, Sets.newHashSet(uid));
        if (Checker.beNotEmpty(pid)) {
            assertPositionsExist(Sets.newHashSet(pid));
            UserPositionDto userPositionDto = new UserPositionDto();
            userPositionDto.setUserId(uid).setPositionId(pid).setGroupId(gid);
            userPositionService.saveIt(userPositionDto);
        }
    }
    
    @Override
    @Transactional
    public void deleteGroup(String gid) {
        Assert.CheckArgument(gid);
        GroupDto group = super.getIt(gid);
        assertBeLock(group);
        assertGroupsExist(Sets.newHashSet(gid));
        List<UserGroupDto> ugs = userGroupService.listItAndChilldByGroup(gid);
        if (Checker.beNotEmpty(ugs)) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.GROUP_HAS_USERS_NOT_BE_DELETE);
        }
        //查询当前组织及其子组织ID
        Set<String> deleteGroupIds = listAllChildrenByParent(gid)
        .stream().map(GroupDto::getId).collect(Collectors.toSet());
        //物理删除 sys_user_group
        userGroupService.deleteByGroups(deleteGroupIds);
        //物理删除 sys_position
        positionService.deleteByGroups(deleteGroupIds);
        //删除组织
        this.baseMapper.deleteById(gid);
        
    }
    
    
    @Transactional
    @Override
    public void updateGroup(GroupDto group) {
        GroupDto dbGroup = super.getIt(group.getId());
        assertBeLock(dbGroup);
        List<SysGroup> groups = this.baseMapper.selectList(lambdaQuery().eq(SysGroup::getParentId, group.getParentId())
        .eq(SysGroup::getGroupName, group.getGroupName()).eq(SysGroup::getBeDeleted, false));
        if (Checker.beNotEmpty(groups)) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.GROUP_NAME_HAS_EXIST);
        }
        super.updateIt(group);
    }
    
    
    @Override
    @Transactional
    public GroupUserVo getUserDetail(String uid, String gid) {
        if (Checker.beEmpty(uid) || Checker.beEmpty(gid)) {
            return null;
        }
        GroupUserVo groupUserVo = new GroupUserVo();
        groupUserVo.setGroupId(gid);
        groupUserVo.setUserId(uid);
        PositionDto positionDto = userPositionService.getPositionByUserAndGroup(uid, gid);
        if (Checker.beNotNull(positionDto)) {
            groupUserVo.setPositionId(positionDto.getId());
        }
        return groupUserVo;
    }
    
    @Override
    @Transactional
    public List<GroupDto> listGroupsByUser(String uid) {
        if (Checker.beEmpty(uid)) {
            return new ArrayList<>();
        }
        return mapper(baseMapper.listGroupsByUser(uid));
    }

    
    private void assertGroupsExist(Set<String> groupIds) {
        Assert.CheckArgument(groupIds);
        List<GroupDto> groups = super.gets(groupIds);
        if (Checker.beEmpty(groups) || groups.size() != groupIds.size()) {
            throw new ServerRuntimeException(ExceptionEnum.IDS_CONTAINS_INVALID_ID);
        }
    }
    
    private void assertPositionsExist(Set<String> positions) {
        Assert.CheckArgument(positions);
        List<PositionDto> positionList = positionService.gets(positions);
        if (Checker.beEmpty(positionList) || positions.size() != positionList.size()) {
            throw new ServerRuntimeException(ExceptionEnum.IDS_CONTAINS_INVALID_ID);
        }
    }
    
    private void assertUsersExist(Set<String> users) {
        Assert.CheckArgument(users);
        List<UserDto> userList = userService.gets(users);
        if (Checker.beNull(userList)) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.ID_OF_USER_NOT_EXIST, users);
        }
    }
    
    
    private List<GroupDto> listAllChildrenByParent(String gid) {
        return mapper(baseMapper.selectList(lambdaQuery().eq(SysGroup::getBeDeleted, false)
        .likeRight(SysGroup::getId, gid)));
    }
    
    @Action(value = PermActionConst.GROUP_TREE_ALL_GROUPS_AND_USERS)
    @ActionConnect(value = PermConnectConst.SELECT_LIST, groupAuthColumn = "id")
    @Override
    public List<CommonTreeWrap> treeAllGroupsAndUsers() {
        List<SysGroup> groups = baseMapper.selectList(lambdaQuery().isNotNull(SysGroup::getParentId));
        List<UserDto> users = userService.listAllGroupUsers();
        
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

    @Override
    public List<CommonTreeWrap> treeAllGroupsAndUsersByTag(String userTag) {
        List<SysGroup> groups = baseMapper.selectList(lambdaQuery().isNotNull(SysGroup::getParentId));
        List<UserDto> users = userService.listAllGroupUsersByTag(userTag);

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

    @Override
    public List<CommonTreeWrap> treeAllGroupsAndPositions() {
        List<SysGroup> groups = baseMapper.selectList(lambdaQuery().isNotNull(SysGroup::getParentId));
        List<PositionDto> positions = positionService.listAllGroupPositions();
        
        List<CommonTreeWrap> tlist = Lists.newArrayList();

        List<CommonTreeWrap> treeList =
                groups.stream().map(group -> new CommonTreeWrap().setKey(group.getId()).setTitle(group.getGroupName()).setIsLeaf(true)
                        .setParentId(group.getParentId()).setTag(PositionGroupEnum.GROUP.name())).collect(Collectors.toList());
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
    public List<CommonTreeWrap> treeAllGroups() {
        List<SysGroup> groups = baseMapper.selectList(lambdaQuery().isNotNull(SysGroup::getParentId));
        
        List<CommonTreeWrap> tlist = Lists.newArrayList();
        
        List<CommonTreeWrap> treeList = groups.stream()
        .map(group -> new CommonTreeWrap().setKey(group.getId()).setTitle(group.getGroupName()).setIsLeaf(true)
        .setParentId(group.getParentId())).collect(Collectors.toList());
        // 创建map结构用来临时存储对象
        Map<String, CommonTreeWrap> gMap = treeList.stream()
        .collect(Collectors.toMap(CommonTreeWrap::getKey, Function.identity(), (key1, key2) -> key2));
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
    public GroupDto getParentCompanyById(String id) {
        if (Checker.beEmpty(id)) {
            return null;
        }
        return mapper2v(this.baseMapper.getParentCompanyById(id));
        
    }

    @Override
    public List<GroupDto> listBranchCompanines() {
        LambdaQueryWrapper<SysGroup> queryWrapper = lambdaQuery();
        queryWrapper.eq(SysGroup::getParentId, Strings.R).eq(SysGroup::getBeDeleted, Boolean.FALSE);
        queryWrapper.select(SysGroup::getId, SysGroup::getGroupName);
        return mapper(super.baseMapper.selectList(queryWrapper));
    }
    
    
}
