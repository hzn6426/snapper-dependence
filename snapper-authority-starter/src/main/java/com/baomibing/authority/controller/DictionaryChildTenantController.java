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

import com.baomibing.authority.dto.DictionaryChildTenantDto;
import com.baomibing.authority.service.DictionaryChildTenantService;
import com.baomibing.core.common.SearchResult;
import com.baomibing.web.annotation.NotWrap;
import com.baomibing.web.annotation.ULog;
import com.baomibing.web.common.R;
import com.baomibing.tool.common.PageQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 字典项
 *
 * @Author: zhenyang 2021/3/8 15:06
 * @version: 1.0.0
 */
@RestController
@RequestMapping(path = {"/eapi/dictionaryChild"}, consumes = {"application/json", "application/x-www-form-urlencoded"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class DictionaryChildTenantController {

	@Autowired
	private DictionaryChildTenantService dictionaryChildService;


	/**
	 * 字典项保存
	 *
	 * @param dictionaryDto
	 * @Return: void
	 */
	@ULog("字典项保存")
	@PostMapping
	public void saveDictChild(@RequestBody @Valid DictionaryChildTenantDto dictionaryDto) {
		dictionaryChildService.saveDictChild(dictionaryDto);
	}

	/**
	 * 字典项更新
	 *
	 * @param dictionaryDto
	 * @Return: void
	 */
	@ULog("字典项更新")
	@PutMapping
	public void updateDictChild(@RequestBody @Valid DictionaryChildTenantDto dictionaryDto) {
		dictionaryChildService.updateDictChild(dictionaryDto);
	}

	/**
	 * 根据字典id查询字典项列表
	 *
	 * @param query
	 * @Return: com.baomibing.core.common.R<com.baomibing.authority.dto.DictionaryChildDto>
	 */
	@PostMapping("search")
	public R<DictionaryChildTenantDto> search(@RequestBody PageQuery<DictionaryChildTenantDto> query) {
		SearchResult<DictionaryChildTenantDto> result = dictionaryChildService.searchDictChild(query.getDto(), query.getPageNo(), query.getPageSize());
		return R.build(result);
	}

	/**
	 * 批量删除字典项
	 *
	 * @param ids 字典项id数组
	 * @Return: void
	 */
	@ULog("批量删除字典项")
	@DeleteMapping
	public void deleteDicts(@RequestBody List<String> ids) {
		dictionaryChildService.deleteDictChilds(ids);
	}


	/**
	 * 批量启用字典项
	 *
	 * @param ids
	 * @Return: void
	 */
	@ULog("批量启用字典项")
	@PostMapping("use")
	public void useDictChilds(@RequestBody List<String> ids) {
		dictionaryChildService.useDictChilds(ids);
	}

	/**
	 * 批量禁用字典项
	 *
	 * @param ids
	 * @Return: void
	 */
	@ULog("批量禁用字典项")
	@PostMapping("stop")
	public void stopDictChilds(@RequestBody List<String> ids) {
		dictionaryChildService.stopDictChilds(ids);
	}

	/**
	 * 根据父编码获取子项列表
	 *
	 * @param pcode 父项编码
	 * @return
	 */
	@GetMapping("listByParentCode")
	public List<DictionaryChildTenantDto> listByParentCode(@RequestParam String pcode) {
		return dictionaryChildService.listChildByParentCode(pcode);
	}

	/**
	 * 根据福编码列表获取子项列表
	 *
	 * @param pcodes
	 * @return
	 */
	@PostMapping("listByParentCodes")
	public List<DictionaryChildTenantDto> listByParentCode(@RequestBody List<String> pcodes) {
		return dictionaryChildService.listChildByParentCodes(pcodes);
	}

	/**
	 * 根据福编码列表获取子项列表
	 *
	 * @param pcodes
	 * @return
	 */
	@NotWrap
	@PostMapping("listChildsByParentCodes")
	public List<DictionaryChildTenantDto> listChildsByParentCodes(@RequestBody List<String> pcodes) {
		return dictionaryChildService.listChildByParentCodes(pcodes);
	}

}
