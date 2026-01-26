
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

import com.baomibing.authority.dto.HmacLogDto;
import com.baomibing.authority.service.SysHmacLogService;
import com.baomibing.web.common.R;
import com.baomibing.tool.common.PageQuery;
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
