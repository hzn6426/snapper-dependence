/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.controller;

import com.baomibing.authority.dto.DictionaryChildDto;
import com.baomibing.authority.service.DictionaryChildService;
import com.baomibing.core.common.SearchResult;
import com.baomibing.tool.common.PageQuery;
import com.baomibing.web.annotation.ULog;
import com.baomibing.web.common.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 字典项
 *
 * @author : zening
 * @since : 1.0.0
 */
@RestController
@RequestMapping(path = "/api/dictionaryChild", consumes = {"application/json", "application/x-www-form-urlencoded"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class DictionaryChildController {

	@Autowired
	private DictionaryChildService dictionaryChildService;


	/**
	 * 字典项保存
	 *
	 * @param dictionaryDto
	 * @Return: void
	 */
	@ULog("字典项保存")
	@PostMapping
	public void saveDictChild(@RequestBody @Valid DictionaryChildDto dictionaryDto) {
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
	public void updateDictChild(@RequestBody @Valid DictionaryChildDto dictionaryDto) {
		dictionaryChildService.updateDictChild(dictionaryDto);
	}

	/**
	 * 根据字典id查询字典项列表
	 *
	 * @param query
	 * @Return: com.baomibing.core.common.R<com.baomibing.authority.dto.DictionaryChildDto>
	 */
	@PostMapping("search")
	public R<DictionaryChildDto> search(@RequestBody PageQuery<DictionaryChildDto> query) {
		SearchResult<DictionaryChildDto> result = dictionaryChildService.searchDictChild(query.getDto(), query.getPageNo(), query.getPageSize());
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
	public List<DictionaryChildDto> listByParentCode(@RequestParam String pcode) {
		return dictionaryChildService.listChildByParentCode(pcode);
	}

	/**
	 * 根据福编码列表获取子项列表
	 *
	 * @param pcodes
	 * @return
	 */
	@PostMapping("listByParentCodes")
	public List<DictionaryChildDto> listByParentCode(@RequestBody List<String> pcodes) {
		return dictionaryChildService.listChildByParentCodes(pcodes);
	}

	/**
	 * 根据ID获取字典子项信息
	 * @param id 字典子项ID
	 * @return
	 */
	@GetMapping("/{id}")
	public DictionaryChildDto getDictChild(@PathVariable("id") String id) {
		return dictionaryChildService.getDictChild(id);
	}

}
