
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


import com.baomibing.authority.dto.UserLogDto;
import com.baomibing.core.base.MBaseService;
import com.baomibing.core.common.SearchResult;

public interface SysUserLogService extends MBaseService<UserLogDto> {

	/**
	 * 分页条件查询日志
	 * 
	 * @param v          条件封装
	 * @param pageNumber 页号
	 * @param pageSize   每页数量
	 * @return
	 */
	SearchResult<UserLogDto> search(UserLogDto v, int pageNumber, int pageSize);

	/**
	 * 异步保存日志
	 * 
	 * @param log Log对象
	 */
	void doSaveLogAsync(UserLogDto userLog);
}
