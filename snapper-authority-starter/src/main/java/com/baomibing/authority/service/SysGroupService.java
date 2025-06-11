package com.baomibing.authority.service;


import com.baomibing.authority.dto.GroupDto;
import com.baomibing.authority.vo.GroupUserVo;
import com.baomibing.core.base.MBaseService;
import com.baomibing.core.wrap.CommonTreeWrap;
import com.baomibing.core.wrap.GroupIntervalWrap;

import java.util.List;
import java.util.Set;

public interface SysGroupService extends MBaseService<GroupDto> {

//	String getMaxId();

	/**
	 * 获取group列表左右值区间的并集
	 * 
	 * @param groupList 组织列表
	 * @return
	 */
	List<GroupIntervalWrap> unionGroupInterval(List<GroupDto> groupList);

	/**
	 * 根据父节点加载子节点
	 * 
	 * @param parentId 父节点
	 * @return
	 */
	List<GroupDto> listChildrenByParent(String parentId);

	/**
	 * 添加分公司
	 *
	 * @param groupDto
	 * @Return: void
	 */
	void saveChild(GroupDto groupDto);

	/**
	 * 添加成员
	 *
	 * @param pid   职位ID
	 * @param gid   组织ID
	 * @param users 用户ID列表
	 * @Return: void
	 */
	void addUsers(String pid, String gid, Set<String> users);

	/**
	 * 移动成员
	 *
	 * @param ogid  源组织ID
	 * @param togid 目标组织ID
	 * @param users 移动的用户ID列表
	 * @Return: void
	 */
	void doMoveGroupUsers(String ogid, String togid, Set<String> users);

	/**
	 * 删除成员
	 *
	 * @param gid   组织ID
	 * @param users 用户的ID列表
	 * @Return: void
	 */
	void removeGroupUsers(String gid, Set<String> users);

	/**
	 * 修改成员职位
	 *
	 * @param uid 用户ID
	 * @param gid 组织ID
	 * @param pid 职位ID
	 * @Return: void
	 */
	void doAssignUserPosition(String uid, String gid, String pid);

	/**
	 * 删除组织
	 *
	 * @param gid
	 * @Return: void
	 */
	void deleteGroup(String gid);

	/**
	 * 编辑组织
	 * 
	 * @param group 组织对象
	 */
	void updateGroup(GroupDto group);

	/**
	 * 成员明细
	 *
	 * @param gid
	 * @param uid
	 * @Return: com.baomibing.authority.vo.GroupUserVo
	 */
	GroupUserVo getUserDetail(String uid, String gid);

	/**
	 * 用户组织列表
	 *
	 * @Return: java.util.List<com.baomibing.authority.dto.GroupDto>
	 */
	List<GroupDto> listGroupsByUser(String uid);


	/**
	 * 以树的方式封装获取所有组织和用户
	 * 
	 * @return
	 */
	List<CommonTreeWrap> treeAllGroupsAndUsers();

	/**
	 * 以树的方式封装获取所有组织和Tag对应的用户
	 * @param userTag
	 * @return
	 */
	List<CommonTreeWrap> treeAllGroupsAndUsersByTag(String userTag);

	/**
	 * 以树得分昂视封装所有组织和职位
	 *
	 * @return
	 */
	List<CommonTreeWrap> treeAllGroupsAndPositions();

	/**
	 * 以树的方式封装所有组织
	 *
	 * @return
	 */
	List<CommonTreeWrap> treeAllGroups();

	/**
	 * 根据组织ID获取对应的公司
	 * 
	 * @param id 组织ID
	 * @return
	 */
	GroupDto getParentCompanyById(String id);

	/**
	 * 获取分公司列表
	 *
	 * @param
	 * @Return: java.util.List<com.baomibing.authority.dto.GroupDto>
	 */
	List<GroupDto> listBranchCompanines();

}
