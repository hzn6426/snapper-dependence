
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



import com.baomibing.authority.dto.DictionaryDto;
import com.baomibing.core.base.MBaseService;
import com.baomibing.core.common.SearchResult;

import java.util.List;

public interface DictionaryService extends MBaseService<DictionaryDto> {

	/**
	 * 字典保存
	 *
	 * @param dictionaryDto
	 * @Return: void
	 */
	void saveDictionary(DictionaryDto dictionaryDto);

	/**
	 * 字典更新
	 *
	 * @param dictionaryDto
	 * @Return: void
	 */
	void updateDictionary(DictionaryDto dictionaryDto);

	/**
	 * 字典查询
	 *
	 * @param dto
	 * @param pageNo
	 * @param pageSize
	 * @Return: com.baomibing.core.common.SearchResult<com.baomibing.authority.dto.DictionaryDto>
	 */
	SearchResult<DictionaryDto> searchDictionary(DictionaryDto dto, int pageNo, int pageSize);

	/**
	 * 批量删除字典
	 *
	 * @param ids
	 * @Return: void
	 */
	void deleteDicts(List<String> ids);

	/**
	 * 批量启用字典
	 *
	 * @param ids
	 * @Return: void
	 */
	void useDicts(List<String> ids);

	/**
	 * 批量禁用字典
	 *
	 * @param ids
	 * @Return: void
	 */
	void stopDicts(List<String> ids);

}
