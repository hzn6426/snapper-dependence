
package com.baomibing.authority.service;


import com.baomibing.authority.dto.DictionaryChildDto;
import com.baomibing.core.base.MBaseService;
import com.baomibing.core.common.SearchResult;

import java.util.List;

public interface DictionaryChildService extends MBaseService<DictionaryChildDto> {

	/**
	 * 通过字典表id删除字典项
	 *
	 * @param ids 字典表id数组
	 * @Return: void
	 */
	void deleteDictChildsByParentIds(List<String> ids);

	/**
	 * 字典项保存
	 *
	 * @param dictionaryDto
	 * @Return: void
	 */
	void saveDictChild(DictionaryChildDto dictionaryDto);

	/**
	 * 字典项更新
	 *
	 * @param dictionaryDto
	 * @Return: void
	 */
	void updateDictChild(DictionaryChildDto dictionaryDto);

	/**
	 * 字典项更搜索Dict
	 *
	 * @param dto      com.baomibing.authority.dto.DictionaryChildDto
	 * @param pageNo
	 * @param pageSize
	 * @Return: com.baomibing.core.common.SearchResult<com.baomibing.authority.dto.DictionaryChildDto>
	 */
	SearchResult<DictionaryChildDto> searchDictChild(DictionaryChildDto dto, int pageNo, int pageSize);

	/**
	 * 批量删除字典项
	 *
	 * @param ids 字典项id数组
	 * @Return: void
	 */
	void deleteDictChilds(List<String> ids);

	/**
	 * 根据父编码获取子项列表
	 *
	 * @param parentCode 父编码
	 * @return java.util.List<com.baomibing.authority.dto.DictionaryChildDto>
	 */
	List<DictionaryChildDto> listChildByParentCode(String parentCode);

	/**
	 * 根据父编码列表获取子项列表
	 *
	 * @param pcodes 父编码列表
	 * @return
	 */
	List<DictionaryChildDto> listChildByParentCodes(List<String> pcodes);

	/**
	 * 根据编码信息获取子项列表
	 *
	 * @param code 编码
	 * @return com.baomibing.authority.dto.DictionaryChildDto
	 */
//	DictionaryChildDto getByCode(String code);

	/**
	 * 通过字典项id数组批量使用字典项
	 *
	 * @param ids 字典项id数组
	 * @Return: void
	 */
	void useDictChilds(List<String> ids);

	/**
	 * 通过字典项id数组批量停用字典项
	 *
	 * @param ids 字典项id数组
	 * @Return: void
	 */
	void stopDictChilds(List<String> ids);

	/**
	 * 通过字典id数组批量使用字典项
	 *
	 * @param ids 字典id数组
	 * @Return: void
	 */
	void useDictChildsByParentIds(List<String> ids);

	/**
	 * 通过字典id数组批量停用字典项
	 *
	 * @param ids 字典表id数组
	 * @Return: void
	 */
	void stopDictChildsByParentIds(List<String> ids);

	/**
	 * 根据ID获取字典信息(包含父字典信息)
	 * @param id 字典ID
	 * @return com.baomibing.authority.dto.DictionaryChildDto
	 */
	DictionaryChildDto getDictChild(String id);

}
