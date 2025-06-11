
package com.baomibing.authority.service;


import com.baomibing.authority.dto.HmacLogDto;
import com.baomibing.core.base.MBaseService;
import com.baomibing.core.common.SearchResult;

/**
 * SysHmacLogService
 *
 * @author zening
 * @version 1.0.0
 */
public interface SysHmacLogService extends MBaseService<HmacLogDto> {


    SearchResult<HmacLogDto> searchLog(HmacLogDto log, int pageNo, int pageSize);

    void doSaveLogAsync(HmacLogDto log);
}
