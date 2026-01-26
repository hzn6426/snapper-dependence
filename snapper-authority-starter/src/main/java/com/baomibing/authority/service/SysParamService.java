
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


import com.baomibing.authority.dto.ParamDto;
import com.baomibing.core.base.MBaseService;
import com.baomibing.core.common.SearchResult;

import java.util.List;

public interface SysParamService extends MBaseService<ParamDto> {

	/**
	 * 参数保存
	 *
	 * @param dto
	 * @Return: void
	 */
	void saveParam(ParamDto dto);

	/**
	 * 参数更新
	 *
	 * @param dto
	 * @Return: void
	 */
	void updateParam(ParamDto dto);

	/**
	 * 参数查询
	 *
	 * @param dto
	 * @param pageNo
	 * @param pageSize
	 * @Return: com.baomibing.core.common.SearchResult<com.baomibing.authority.dto.ParamDto>
	 */
	SearchResult<ParamDto> searchParam(ParamDto dto, int pageNo, int pageSize);

	/**
	 * 批量删除参数
	 *
	 * @param ids
	 * @Return: void
	 */
	void deleteParams(List<String> ids);

	/**
	 * 功能描述: 根据编码列表获取对应的参数列表
	 * 
	 * @param codes 编码列表
	 * @return
	 */
	List<ParamDto> listByCodes(List<String> codes);
}
