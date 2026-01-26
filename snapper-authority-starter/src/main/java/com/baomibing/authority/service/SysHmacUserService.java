
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



import com.baomibing.authority.dto.HmacUserDto;
import com.baomibing.core.base.MBaseService;
import com.baomibing.core.common.SearchResult;

import java.util.Set;


/**
 * hamc用户服务
 * @author zening
 * @version 1.0.0
 */
public interface SysHmacUserService extends MBaseService<HmacUserDto> {

//	/**
//	 * 根据APPid获取hmac用户
//	 * @param appId 外部程序识别码
//	 * @return
//	 */
//	SysHmacUserDto getByAppId(String appId);
//
//	/**
//	 * 根据appId获取角色名列表
//	 * @param appId 外部程序识别码
//	 * @return 角色名列表，没有返回空列表
//	 */
//	List<String> listRolesByAppId(String appId);
//
//	/**
//	 * 根据appId获取权限字符串列表，包括<b>菜单</b>,<b>按钮</b>权限
//	 * @param appId 外部程序识别码
//	 * @return 权限列表，没有返回空列表
//	 */
//	List<String> listPermsByAppId(String appId);

	/**
	 * 条件查找第三方用户
	 * @param user
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	SearchResult<HmacUserDto> searchHmacUser(HmacUserDto user, int pageNo, int pageSize);

	void saveHmacUser(HmacUserDto user);

	void updateHmacUser(HmacUserDto user);

	 void deleteHmacUsers(Set<String> ids);

	 void refreshCaches(Set<String> ids);

	/**
	 * 刷新所有HMAC权限
	 */
	public void refreshPrivileges();

	 HmacUserDto getHmacUser(String id);

	
}
