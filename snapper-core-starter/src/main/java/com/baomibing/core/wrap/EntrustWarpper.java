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
package com.baomibing.core.wrap;

import com.baomibing.tool.constant.PermConstant;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
/**
 * 用户业务权限委托的用户列表和组织列表
 */
@JsonInclude(Include.NON_NULL)
@Data @Accessors(chain = true) 
public class EntrustWarpper implements Serializable {

	private static final long serialVersionUID = -1606829935911752421L;

	private String action;
	//委托用户权限列表
	private List<String> userNos = Lists.newArrayList();
	//委托组织权限列表
	private List<GroupIntervalWrap> groupWraps = Lists.newArrayList();
	//公司委托列表
	private List<GroupIntervalWrap> companyGroupWraps = Lists.newArrayList();
	//公司组织委托
	private GroupIntervalWrap companyWrap;//公司Group范围
	private String scope;// 权限范围(当前用户/全部/指定组织)
	//用户权限筛选列
	private String[] userColumn = {PermConstant.CREATE_USER};
	//组织权限筛选列
	private String[] groupColumn = {PermConstant.GROUP_ID};
	//是否忽略用户权限
	private Boolean beIgnoreUserScope = Boolean.FALSE;
	//是否忽略组织权限
	private Boolean beIgnoreGroupScope = Boolean.FALSE;
	///是否仅仅过滤分公司
	private Boolean beOnlyFilterCompany = Boolean.FALSE;
	//是否忽略分公司权限
	private Boolean beIgnoreCompanyScope = Boolean.FALSE;
	//是否用户列筛选使用FIND_IN_SET方式
	private boolean beUserColumnWithComma = false;
	//授权码登录
	private boolean beLoginWithAuthCode = false;
	//权限插入的位置-指定的表名或别名同级
	private String tableNameWithAuthAppend = null;
	//排除列插入的位置 - 指定的表名或别名同级
	private String tableNameWithColumnAppend = null;
	//数据权限组合
	private List<DataPermWrap> dataPerms;
	//排除的列
	private List<ColumnPermWrap> exceptColumns;
	//排除的用户列表
	private List<String> exceptUserNos = Lists.newArrayList();
	//排除的组织列表
	private List<GroupIntervalWrap> exceptGroupWraps = Lists.newArrayList();
}
