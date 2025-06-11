
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
