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

import com.baomibing.authority.dto.SysTenantGroupDto;
import com.baomibing.authority.dto.SysTenantUserDto;
import com.baomibing.authority.dto.SysTenantUserGroupDto;
import com.baomibing.authority.service.SysTenantGroupService;
import com.baomibing.authority.service.SysTenantUserGroupService;
import com.baomibing.authority.service.SysTenantUserService;
import com.baomibing.authority.vo.TenantGroupUserVo;
import com.baomibing.cache.CacheService;
import com.baomibing.core.common.Assert;
import com.baomibing.core.common.SearchResult;
import com.baomibing.core.wrap.CommonTreeWrap;
import com.baomibing.tool.common.PageQuery;
import com.baomibing.tool.constant.TenantRedisKeyConstant;
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
import java.util.stream.Collectors;

import static com.baomibing.tool.user.UserContext.currentTenantId;

@RestController
@RequestMapping(path = {"/api/tgroup"}, consumes = {"application/json", "application/x-www-form-urlencoded"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class TenantAdminGroupController extends MBaseController<SysTenantGroupDto> {

	@Autowired private SysTenantUserGroupService userGroupService;
	@Autowired private SysTenantGroupService groupService;
	@Autowired private SysTenantUserService userService;
	@Autowired private CacheService cacheService;
//	@Autowired
//	private RedisTemplate<String, String> redisTemplate;


	@GetMapping("userDepartments")
	public List<SysTenantUserGroupDto> userDepartments(@RequestParam String tid) {
		if (userNotLogin()) return Lists.newArrayList();
		List<SysTenantUserGroupDto> userGroups = userGroupService.listByUser(tid, currentUserId());
		// 获取用户当前的组织
		String group = ObjectUtil.toStringIfNotNull(cacheService.hGet(MessageFormat.format(TenantRedisKeyConstant.KEY_USER_CONTEXT, currentUserName()), TenantRedisKeyConstant.KEY_USER_GROUP_ID));
		if (Checker.beNotNull(group)) {
			return userGroups.stream().filter(ug -> !group.equals(ug.getGroupId())).collect(Collectors.toList());
		}
		return userGroups;
	}

	@GetMapping("change")
	public void change(@RequestParam String tid, @RequestParam("gid") String groupId) {
		Assert.CheckArgument(groupId);
	}


	/**
	 * 添加分公司
	 *
	 * @param groupDto
	 * @Return: void
	 */
	@ULog("添加分公司")
	@PostMapping("/addCompany")
	public void addCompany(@Valid @RequestBody SysTenantGroupDto groupDto) {
		groupService.saveChild(groupDto, true);

	}


	/**
	 * 添加子部门
	 *
	 * @param groupDto
	 * @Return: void
	 */
	@ULog("添加子部门")
	@PostMapping("/addDepartment")
	public void addDepartment(@Valid @RequestBody SysTenantGroupDto groupDto) {
		groupService.saveChild(groupDto, false);
	}


	/**
	 * 添加成员
	 *
	 * @param groupUserVo
	 * @Return: void
	 */
	@ULog("添加成员")
	@PostMapping("/addUsers")
	public void addUsers(@Valid @RequestBody TenantGroupUserVo groupUserVo) {
		groupService.addUsers(groupUserVo.getTenantId(), groupUserVo.getPositionId(), groupUserVo.getGroupId(), Sets.newHashSet(groupUserVo.getUsers()));
	}


	/**
	 * 移动成员
	 *
	 * @param groupUserVo
	 * @Return: void
	 */
	@ULog("移动成员")
	@PostMapping("/moveUsers")
	public void moveUsers(@RequestBody TenantGroupUserVo groupUserVo) {
		Assert.CheckArgument(groupUserVo.getUsers());
		groupService.doMoveGroupUsers(groupUserVo.getTenantId(), groupUserVo.getGroupId(), groupUserVo.getToGroupId(), new HashSet<>(groupUserVo.getUsers()));
	}

	/**
	 * 删除成员
	 *
	 * @param groupUserVo
	 * @Return: void
	 */
	@ULog("删除成员")
	@PostMapping("/removeUsers")
	public void removeUsers(@RequestBody TenantGroupUserVo groupUserVo) {
		Assert.CheckArgument(groupUserVo.getUsers());
		groupService.removeGroupUsers(groupUserVo.getTenantId(), groupUserVo.getGroupId(), new HashSet<>(groupUserVo.getUsers()));
	}

	/**
	 * 修改成员职位
	 *
	 * @param groupUserVo
	 * @Return: void
	 */
	@ULog("修改成员职位")
	@PostMapping("/assignPosition")
	public void assignUserPosition(@RequestBody TenantGroupUserVo groupUserVo) {
		Assert.CheckArgument(groupUserVo);
		groupService.doAssignUserPosition(groupUserVo.getTenantId(), groupUserVo.getUserId(), groupUserVo.getGroupId(), groupUserVo.getPositionId());
	}

	/**
	 * 删除组织
	 *
	 * @param gid
	 * @Return: void
	 */
	@ULog("删除组织")
	@DeleteMapping
	public void delete(@RequestParam String tid, @RequestParam String gid) {
		Assert.CheckArgument(gid);
		groupService.deleteGroup(tid, gid);
	}

	@ULog("编辑组织")
	@PutMapping
	public void update(@RequestBody SysTenantGroupDto group) {
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
	public TenantGroupUserVo userDetail(@RequestParam String tid, @RequestParam String uid, @RequestParam String gid) {
		Assert.CheckArgument(tid, gid, uid);
		return groupService.getUserDetail(tid, uid, gid);
	}


	/**
	 * 用户组织列表
	 *
	 * @Return: java.util.List<com.baomibing.authority.dto.GroupDto>
	 */
	@PostMapping("/userDepartments")
	public List<SysTenantGroupDto> currentUserDepartments() {
		String uid = currentUserId();
		String tid = currentTenantId();
		return groupService.listGroupsByUser(tid, uid);
	}


	/**
	 * 以树的方式获取所有组织及用户信息
	 *
	 * @return
	 */
	@GetMapping("/treeAllGroupsAndUsers")
	public List<CommonTreeWrap> treeAllGroupsAndUsers(@RequestParam String tid) {
		return groupService.treeAllGroupsAndUsers(tid);
	}

	/**
	 * 以树的方式获取所有组织和职位
	 *
	 * @return
	 */
	@GetMapping("/treeAllGroupsAndPositions")
	public List<CommonTreeWrap> treeAllGroupsAndPositions(@RequestParam String tid) {
		return groupService.treeAllGroupsAndPositions(tid);
	}

	/**
	 * 以树的方式获取所有组织
	 *
	 * @return
	 */
	@GetMapping("/treeAllGroups")
	public List<CommonTreeWrap> treeAllGroups(@RequestParam String tid) {
		return groupService.treeAllGroups(tid);
	}

	/**
	 //	 * 根据父组织获取子组织以树的方式展示
	 //	 *
	 //	 * @param gid 父组织ID
	 //	 * @return
	 //	 */
//	@GetMapping("/treeByParent")
//	public List<CommonTreeWrap> treeByParent(@RequestParam String gid) {
//		List<GroupDto> groups = groupService.listByParent(gid);
//		List<CommonTreeWrap> lists = Lists.newArrayList();
//		groups.forEach(g -> lists.add(new CommonTreeWrap().setIsLeaf(false).setParentId(g.getParentId())
//				.setKey(g.getId()).setTitle(g.getGroupName()).setSelectable(Boolean.TRUE).setParentGroupName(g.getParentGroupName())
//				.setDisableCheckbox(false)));
//		return lists;
//	}

	/**
	 * 根据组织查询用户
	 *
	 * @param query 构建的查询信息
	 * @return com.baomibing.core.common.R<com.baomibing.authority.dto.UserDto> 
	 */
	@PostMapping("/searchUser")
	public R<SysTenantUserDto> searchUser(@RequestBody PageQuery<SysTenantUserDto> query) {
		if (Checker.beNull(query)) {
			query = new PageQuery<>();
		}
		SearchResult<SysTenantUserDto> result = userService.searchByGroupCondition(query.getDto(), query.getPageNo(), query.getPageSize());
		return R.build(result);
	}

	/**
	 * 查询未分配组织的用户
	 *
	 * @param query 构建的查询信息
	 * @return com.baomibing.core.common.R<com.baomibing.authority.dto.UserDto> 
	 */
	@PostMapping("/searchNotAssignUser")
	public R<SysTenantUserDto> searchNotAssignUser(@RequestBody PageQuery<SysTenantUserDto> query) {
		if (Checker.beNull(query)) {
			query = new PageQuery<>();
		}
		SearchResult<SysTenantUserDto> result = userService.searchForNotAssignGroup(query.getDto(), query.getPageNo(), query.getPageSize());
		return R.build(result);
	}

	/**
	 //	 * 组织职位列表查询
	 //	 *
	 //	 * @param groupId
	 //	 * @Return: com.baomibing.core.common.R<com.baomibing.authority.dto.PositionDto>
	 //	 */
//	@GetMapping("/listByGroup")
//	public List<PositionDto> listPositionByGroup(@RequestParam String groupId){
//		return positionService.listPositionByGroup(groupId);
//	}

	/**
	 * 用户编号获取组织id
	 *
	 * @param userNo
	 * @Return: java.util.List<java.lang.String>
	 */
	@NotWrap
	@GetMapping("getGroupIdByUserNo/{userNo}")
	public String getGroupIdByUserNo(@PathVariable("userNo") String userNo, @RequestParam String tid) {
		return userGroupService.getGroupIdByUserNo(tid, userNo);
	}

	/**
	 * 获取分公司列表
	 *
	 * @param
	 * @Return: java.util.List<com.baomibing.authority.dto.GroupDto>
	 */
	@NotWrap
	@GetMapping("listBranchCompanines")
	public List<SysTenantGroupDto> listBranchCompanines(@RequestParam String tid) {
		return groupService.listBranchCompanines(tid);
	}

	/**
	 * 获取分公司列表
	 *
	 * @param
	 * @Return: java.util.List<com.baomibing.authority.dto.GroupDto>
	 */
	@GetMapping("getListBranchCompanines")
	public List<SysTenantGroupDto> getListBranchCompanines(@RequestParam String tid) {
		return groupService.listBranchCompanines(tid);
	}

	@GetMapping("getParentCompanyById")
	public SysTenantGroupDto getParentCompanyById(@RequestParam String tid, @RequestParam String groupId) {
		return groupService.getParentCompanyById(tid, groupId);
	}

	/**
	 * 以树的方式获取所有组织及用户信息
	 *
	 * @return
	 */
	@GetMapping("/treeAllGroupsAndUsersByTag")
	public List<CommonTreeWrap> treeAllGroupsAndUsersByTag(@RequestParam String utag, @RequestParam String tid) {
		return groupService.treeAllGroupsAndUsersByTag(tid, utag);
	}
}
