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


import com.baomibing.core.wrap.ColumnPermWrap;
import com.baomibing.core.wrap.DataPermWrap;
import com.baomibing.core.wrap.TenantEntrustWarpper;
import com.baomibing.tool.user.TenantUser;

import java.util.List;

/**
 * 通用数据权限服务
 * @author zening
 * @date 2019-06-15 09:28:26
 * @version 1.0.0
 */


public interface CommonBusinessTenantAuthService {

	/**
	 * 根据用户及权限ID获取用户委托的组织和用户信息
	 * 
	 * @param user              当前登录用户
	 * @param permId            业务权限ID
	 * @param scope             业务范围
	 * @param beIgnoreUserScope 是否忽略用户权限范围
	 * @param beIgnoreGroupScop 是否忽略组织权限范围
	 * @return
	 */
	TenantEntrustWarpper getEntrustBusinessPerm(TenantUser user, String permId, String scope, boolean beIgnoreUserScope, boolean beIgnoreGroupScop);

	/**
	 * 根据请求URL和方法获取权限对应的动作标识
	 * @param url 资源请求URL
	 * @param method 资源请求方法
	 * @return
	 */
	String getPermActionByUrlAndMethod(String url, String method);

	 /**
	  * 根据请求URL和方法获取权限对应的业务权限ID
	  * @param url 资源请求URL
	  * @param method 资源请求方法
	  * @return
	  */
	 String getPermIdByUrlAndMethod(String url, String method);

	 /**
	  * 根据权限动作action获取对应权限的ID
	  * @param action 权限动作
	  * @return
	  */
	 String getPermIdByAction(String action);

	/**
	 * 获取用户的数据权限表达式
	 * @param user
	 * @param permId
	 * @return
	 */
	List<DataPermWrap> getEntrustDataPerm(TenantUser user, String permId);

	/**
	 * 获取用户的排除列信息
	 * @param user
	 * @param permId
	 * @return
	 */
	List<ColumnPermWrap> getEntrustColumnPerm(TenantUser user, String permId);
}
