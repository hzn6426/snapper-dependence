
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
