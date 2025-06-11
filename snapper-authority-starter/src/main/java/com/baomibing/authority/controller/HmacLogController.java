/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.controller;

import com.baomibing.authority.dto.HmacLogDto;
import com.baomibing.authority.service.SysHmacLogService;
import com.baomibing.tool.common.PageQuery;
import com.baomibing.web.common.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * HmacLogController
 *
 * @author zening 2023/5/30 21:15
 * @version 1.0.0
 **/
@RestController
@RequestMapping(path = {"/api/hmacLog"}, consumes = {"application/json", "application/x-www-form-urlencoded"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class HmacLogController {

    @Autowired private SysHmacLogService hmacLogService;

    @PostMapping("search")
    public R<HmacLogDto> search(@RequestBody PageQuery<HmacLogDto> pageQuery) {
        return R.build(hmacLogService.searchLog(pageQuery.getDto(), pageQuery.getPageNo(), pageQuery.getPageSize()));
    }

    @GetMapping("/{id}")
    public HmacLogDto getLog(@PathVariable("id") String id) {
        return hmacLogService.getIt(id);
    }
}
