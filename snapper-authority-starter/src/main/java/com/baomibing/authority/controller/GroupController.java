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

import com.baomibing.authority.dto.GroupDto;
import com.baomibing.authority.dto.UserDto;
import com.baomibing.authority.dto.UserGroupDto;
import com.baomibing.authority.service.SysGroupService;
import com.baomibing.authority.service.SysUserGroupService;
import com.baomibing.authority.service.SysUserService;
import com.baomibing.authority.vo.CopyUserPermVo;
import com.baomibing.authority.vo.GroupUserVo;
import com.baomibing.cache.CacheService;
import com.baomibing.core.common.Assert;
import com.baomibing.core.common.SearchResult;
import com.baomibing.core.wrap.CommonTreeWrap;
import com.baomibing.tool.common.PageQuery;
import com.baomibing.tool.constant.RedisKeyConstant;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ObjectUtil;
import com.baomibing.web.annotation.NotWrap;
import com.baomibing.web.annotation.ULog;
import com.baomibing.web.base.MBaseController;
import com.baomibing.web.common.R;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.baomibing.tool.constant.PermConstant.ROOT_GROUP;

@RestController
@RequestMapping(path = {"/api/group", "/fapi/group"}, consumes = {"application/json", "application/x-www-form-urlencoded"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class GroupController extends MBaseController<GroupDto> {

	@Autowired private SysUserGroupService userGroupService;
	@Autowired private SysGroupService groupService;
	@Autowired private SysUserService userService;
	@Autowired private CacheService cacheService;


	@GetMapping("userDepartments")
	public List<UserGroupDto> userDepartments() {
		if (userNotLogin()) return Lists.newArrayList();
		List<UserGroupDto> userGroups = userGroupService.listByUser(currentUserId());
		// 获取用户当前的组织
		String group = ObjectUtil.toStringIfNotNull(cacheService.hGet(MessageFormat.format(RedisKeyConstant.KEY_USER_CONTEXT, currentUserName()), RedisKeyConstant.KEY_USER_GROUP_ID));
		if (Checker.beNotNull(group)) {
			return userGroups.stream().filter(ug -> !group.equals(ug.getGroupId())).collect(Collectors.toList());
		}
		return userGroups;
	}

	@GetMapping("change")
	public void change(@RequestParam("gid") String groupId) {
		Assert.CheckArgument(groupId);
//		userGroupService.changeUser2Group(currentUserId(), groupId);
	}


	/**
	 * 添加分公司
	 *
	 * @param groupDto 分公司信息
	 * @Return: void
	 */
	@ULog("添加分公司")
	@PostMapping("/addCompany")
	public void addCompany(@Valid @RequestBody GroupDto groupDto) {
		groupDto.setParentId(ROOT_GROUP);
		groupService.saveChild(groupDto);

	}


	/**
	 * 添加子部门
	 *
	 * @param groupDto 子部门信息
	 * @Return: void
	 */
	@ULog("添加子部门")
	@PostMapping("/addDepartment")
	public void addDepartment(@Valid @RequestBody GroupDto groupDto) {
		groupService.saveChild(groupDto);
	}


	/**
	 * 添加成员
	 *
	 * @param groupUserVo 组织用户信息
	 * @Return: void
	 */
	@ULog("添加成员")
	@PostMapping("/addUsers")
	public void addUsers(@Valid @RequestBody GroupUserVo groupUserVo) {
		groupService.addUsers(groupUserVo.getPositionId(), groupUserVo.getGroupId(), Sets.newHashSet(groupUserVo.getUsers()));
	}


	/**
	 * 移动成员
	 *
	 * @param groupUserVo 组织用户信息
	 * @Return: void
	 */
	@ULog("移动成员")
	@PostMapping("/moveUsers")
	public void moveUsers(@RequestBody GroupUserVo groupUserVo) {
		Assert.CheckArgument(groupUserVo.getUsers());
		groupService.doMoveGroupUsers(groupUserVo.getGroupId(), groupUserVo.getToGroupId(), groupUserVo.isBeSyncUserPerm(), new HashSet<>(groupUserVo.getUsers()));
	}

    /**
     * 复制成员权限
     * @param copyUserPermVo
     */
    @ULog("复制用户权限")
    @PostMapping("/copyUserPerm")
    public void copyUserPerm(@RequestBody CopyUserPermVo copyUserPermVo) {
        groupService.doCopyUserPerm(copyUserPermVo.getUserId(), copyUserPermVo.getGroupId(), copyUserPermVo.getToUserId());
    }

	/**
	 * 删除成员
	 *
	 * @param groupUserVo 组织用户信息
	 * @Return: void
	 */
	@ULog("删除成员")
	@PostMapping("/removeUsers")
	public void removeUsers(@RequestBody GroupUserVo groupUserVo) {
		Assert.CheckArgument(groupUserVo.getUsers());
		groupService.removeGroupUsers(groupUserVo.getGroupId(), new HashSet<>(groupUserVo.getUsers()));
	}

	/**
	 * 修改成员职位
	 *
	 * @param groupUserVo 组织用户信息
	 * @Return: void
	 */
	@ULog("修改成员职位")
	@PostMapping("/assignPosition")
	public void assignUserPosition(@RequestBody GroupUserVo groupUserVo) {
		Assert.CheckArgument(groupUserVo);
		groupService.doAssignUserPosition(groupUserVo.getUserId(), groupUserVo.getGroupId(), groupUserVo.getPositionId());
	}

	/**
	 * 删除组织
	 *
	 * @param gid
	 * @Return: void
	 */
	@ULog("删除组织")
	@DeleteMapping
	public void delete(@RequestParam String gid) {
		Assert.CheckArgument(gid);
		groupService.deleteGroup(gid);
	}

	@ULog("编辑组织")
	@PutMapping
	public void update(@RequestBody GroupDto group) {
		Assert.CheckArgument(group);
		groupService.updateGroup(group);
	}

	/**
	 * 成员明细
	 *
	 * @param gid
	 * @param uid
	 * @Return: com.baomibing.authority.vo.GroupUserVo
	 */
	@PostMapping("/userDetail")
	public GroupUserVo userDetail(@RequestParam String uid, @RequestParam String gid) {
		Assert.CheckArgument(gid, uid);
		return groupService.getUserDetail(uid, gid);
	}


	/**
	 * 用户组织列表
	 *
	 * @Return: java.util.List<com.baomibing.authority.dto.GroupDto>
	 */
	@PostMapping("/userDepartments")
	public List<GroupDto> currentUserDepartments() {
		String uid = currentUserId();
		return groupService.listGroupsByUser(uid);
	}


	/**
	 * 以树的方式获取所有组织及用户信息
	 *
	 * @return
	 */
	@GetMapping("/treeAllGroupsAndUsers")
	public List<CommonTreeWrap> treeAllGroupsAndUsers() {
		return groupService.treeAllGroupsAndUsers();
	}

	/**
	 * 以树的方式获取所有组织及用户信息
	 *
	 * @return
	 */
	@GetMapping("/treeAllGroupsAndUsersByTag")
	public List<CommonTreeWrap> treeAllGroupsAndUsersByTag(@RequestParam String utag) {
		return groupService.treeAllGroupsAndUsersByTag(utag);
	}

	/**
	 * 以树的方式获取所有组织和职位
	 *
	 * @return
	 */
	@GetMapping("/treeAllGroupsAndPositions")
	public List<CommonTreeWrap> treeAllGroupsAndPositions() {
		return groupService.treeAllGroupsAndPositions();
	}

	/**
	 * 以树的方式获取所有组织
	 *
	 * @return
	 */
	@GetMapping("/treeAllGroups")
	public List<CommonTreeWrap> treeAllGroups() {
		return groupService.treeAllGroups();
	}


	/**
	 * 根据组织查询用户
	 *
	 * @param query 构建的查询信息
	 * @return com.baomibing.core.common.R<com.baomibing.authority.dto.UserDto> 
	 */
	@PostMapping("/searchUser")
	public R<UserDto> searchUser(@RequestBody PageQuery<UserDto> query) {
		if (Checker.beNull(query)) {
			query = new PageQuery<>();
		}
		SearchResult<UserDto> result = userService.searchByGroupCondition(query.getDto(), query.getPageNo(), query.getPageSize());
		return R.build(result);
	}

	/**
	 * 查询未分配组织的用户
	 *
	 * @param query 构建的查询信息
	 * @return com.baomibing.core.common.R<com.baomibing.authority.dto.UserDto> 
	 */
	@PostMapping("/searchNotAssignUser")
	public R<UserDto> searchNotAssignUser(@RequestBody PageQuery<UserDto> query) {
		if (Checker.beNull(query)) {
			query = new PageQuery<>();
		}
		SearchResult<UserDto> result = userService.searchForNotAssignGroup(query.getDto(), query.getPageNo(), query.getPageSize());
		return R.build(result);
	}

	/**
	 * 用户编号获取组织id
	 *
	 * @param userNo 用户账号
	 * @return: java.util.List<java.lang.String>
	 */
	@NotWrap
	@GetMapping("getGroupIdByUserNo/{userNo}")
	public String getGroupIdByUserNo(@PathVariable("userNo") String userNo) {
		return userGroupService.getGroupIdByUserNo(userNo);
	}

	/**
	 * 根据用户编号列表获取用户组织列表
	 * @param userNos 用户编号列表
	 * @return
	 */
	@NotWrap
	@PostMapping("listUserGroupByUserNos")
	public List<UserGroupDto> listUserGroupByUserNos(@RequestBody Set<String> userNos) {
		return userGroupService.listByUserNos(userNos);
	}

	/**
	 * 获取分公司列表
	 *
	 * @param
	 * @Return: java.util.List<com.baomibing.authority.dto.GroupDto>
	 */
	@NotWrap
	@GetMapping("listBranchCompanines")
	public List<GroupDto> listBranchCompanines() {
		return groupService.listBranchCompanines();
	}

	@GetMapping("getParentCompanyById")
	public GroupDto getParentCompanyById(@RequestParam String groupId) {
		return groupService.getParentCompanyById(groupId);
	}

}
