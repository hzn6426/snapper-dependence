
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

package com.baomibing.authority.service;


import com.baomibing.authority.dto.ButtonDto;
import com.baomibing.authority.dto.MenuDto;
import com.baomibing.authority.dto.UserDto;
import com.baomibing.core.base.MBaseService;
import com.baomibing.core.common.SearchResult;

import java.util.List;
import java.util.Set;


public interface SysUserService extends MBaseService<UserDto> {

	/**
	 * 分页查询用户信息
	 * 
	 * @param user       查询条件封装
	 * @param pageNumber 页号
	 * @param pageSize   每页数量
	 * @return
	 */
	SearchResult<UserDto> search(UserDto user, int pageNumber, int pageSize);

    /**
     * 根据用户工号获取用户信息
     *
     * @param userNo 工号
     * @return 用户对象信息
     */
    UserDto getByUserNo(String userNo);
    
    /**
     * 根据用户ID列表删除用户信息
     * 
     * @param uids 用户ID列表
     */
    void deleteByIds(Set<String> uids);

    /**
     * 更新用户的密钥
     *
     * @param userId 用户ID
     * @param secret 密钥
     */
    void updateUserSecret(String userId, String secret);
    
    /**
     * 保存用户
     * 
     * @param user 待保存的用户
     */
    void doSave(UserDto user);

    /**
     * 更新用户
     *
     * @param user 待更新的用户
     */
    void doUpdate(UserDto user);

    /**
     * 修改用户密码
     *
     * @param user 待修改的用户
     */
    void updatePassword(UserDto user);


    /**
     * 激活ID列表的用户
     *
     * @param ids 用户的ID列表
     */
    void activeUsers(Set<String> ids);

    /**
     * 所用ID列表的用户
     *
     * @param ids 用户的ID列表
     */
    void lockUsers(Set<String> ids);

    /**
     * 解锁ID列表的用户
     *
     * @param ids 用户的ID列表
     */
    void unlockUsers(Set<String> ids);

    /**
     * 停用ID列表的用户
     *
     * @param ids 用户的ID列表
     */
    void stopUsers(Set<String> ids);

    /**
     * 取消停用ID列表的用户
     *
     * @param ids 用户的ID列表
     */
    void unstopUsers(Set<String> ids);

    /**
     * 重置用户密码-发送邮件
     *
     * @param ids 用户的ID列表
     */
    void resetPasswd(Set<String> ids);

    /**
	 * 查找组织下所有用户
	 *
	 * @param gid
	 * @Return: java.util.List<com.baomibing.authority.dto.UserDto>
	 */
    List<UserDto> listUsersByGroupId(String gid);

    /**
	 * 通过用户标签和关键字来匹配
	 *
	 * @param userTag
	 * @param KeyWord
	 * @Return: java.util.List<com.baomibing.authority.dto.UserDto>
	 */
    List<UserDto> listByKeyWordAndType(String userTag, String KeyWord);

    /**
	 * 通过用户账号获取用户列表
	 *
	 * @param userNos
	 * @Return: java.util.List<com.baomibing.authority.dto.UserDto>
	 */
    List<UserDto> listByUserNos(List<String> userNos);
    
	/**
	 * 通过组织条件获取用户查询结果信息
	 * 
	 * @param user       用户组织
	 * @param pageNumber 页码
	 * @param pageSize   页数据条数
	 * @return
	 */
    SearchResult<UserDto> searchByGroupCondition(UserDto user,  int pageNumber, int pageSize);
    
    /**
	 * 查询未分配组织的用户信息
	 * 
	 * @param user
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	SearchResult<UserDto> searchForNotAssignGroup(UserDto user, int pageNumber, int pageSize);

	/**
	 * 获取所有建立用户组织关系的用户列表
	 * 
	 * @return
	 */
    List<UserDto> listAllGroupUsers();

	/**
	 * 获取所有建立用户组织关系的对应TAG的用户列表
	 *
	 * @return
	 */
	List<UserDto> listAllGroupUsersByTag(String tag);

	/**
	 * 通过用户标签和关键字来匹配分公司订舱对应的用户
	 * @param userTag
	 * @param keyWord
	 * @param companyId
	 * @return
	 */
	List<UserDto> listByKeyWordAndTypeOfCompany(String userTag, String keyWord, String companyId);

	/**
	 * 通过用户中文名获取用户列表
	 * @param cnNames 中文名列表
	 * @return
	 */
	List<UserDto> listByUserCnNames(List<String> cnNames);

	/**
	 * 分配菜单权限
	 * @param userId
	 * @param menuIds
	 */
	void saveMenuPerm(String userId, String groupId, Set<String> menuIds);

	/**
	 * 分配按钮权限
	 * @param userId
	 * @param menuId
	 * @param buttonIds
	 */
	void saveButtonPerm(String userId, String groupId, String menuId, Set<String> buttonIds);

	/**
	 * 根据角色列表及资源类型获取权限对应的资源ID列表
	 *
	 * @param userId      用户ID
	 * @param groupId      用户组织ID
	 * @return
	 */
	List<String> listPermMenuIdsByUser(String userId, String groupId);

	/**
	 * 根据角色列表和菜单ID获取对应的权限按钮列表-包括无权限的按钮
	 *
	 * @param userId  用户ID
	 * @param groupId 用户组织ID
	 * @param menuId  菜单ID
	 * @return
	 */
	List<ButtonDto> listPermButtonsByUserAndMenu(String userId, String groupId, String menuId);

	/**
	 * 根据用户和组织获取菜单列表和按钮列表
	 * @param userId 用户ID
	 * @param groupId 用户组织ID
	 * @param roleId 角色ID
	 * @param beFilterPerm 是否过滤数据权限
	 * @param beFilterNoPerm  是否过滤无权限数据
	 * @return
	 */
	List<MenuDto> listPermMenusAndButtonsByUser(String userId, String groupId, String roleId, boolean beFilterPerm, boolean beFilterNoPerm);

	/**
	 * 根据用户和组织获取@ACTION标注的菜单列表和按钮列表
	 * @param userId
	 * @param groupId
	 * @return
	 */
	List<MenuDto> listActionMenusAndButtonsByUser(String userId, String groupId);
}

