
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

import com.baomibing.authority.dto.HmacUserDto;
import com.baomibing.authority.service.SysHmacUserService;
import com.baomibing.core.common.SearchResult;
import com.baomibing.tool.common.PageQuery;
import com.baomibing.web.annotation.ULog;
import com.baomibing.web.common.R;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * HmacUserController
 *
 * @author zening 2023/5/30 15:10
 * @version 1.0.0
 **/
@RestController
@RequestMapping(path = {"/api/hmacUser"}, consumes = {"application/json", "application/x-www-form-urlencoded"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class HmacUserController {

    @Autowired private SysHmacUserService hmacUserService;

    @ULog("外部用户查询")
    @PostMapping("search")
    public R<HmacUserDto> searchHmacUser(@RequestBody PageQuery<HmacUserDto> pageQuery) {
        SearchResult<HmacUserDto> result = hmacUserService.searchHmacUser(pageQuery.getDto(), pageQuery.getPageNo(), pageQuery.getPageSize());
        return R.build(result);
    }

    @ULog("外部用户新增")
    @PostMapping
    public void saveHmacUser(@RequestBody HmacUserDto user) {
        hmacUserService.saveHmacUser(user);
    }

    @ULog("外部用户更新")
    @PutMapping
    public void updateHmacUser(@RequestBody HmacUserDto user) {
        hmacUserService.updateHmacUser(user);
    }

    @ULog("外部用户删除")
    @DeleteMapping
    public void deleteHmacUsers(@RequestBody List<String> ids) {
        hmacUserService.deleteHmacUsers(Sets.newHashSet(ids));
    }

   @GetMapping("/{id}")
    public HmacUserDto getHmacUser(@PathVariable("id") String id) {
        return hmacUserService.getHmacUser(id);
    }

    @ULog("外部用户刷新权限")
    @PostMapping("refreshCache")
    public void refreshCache(@RequestBody List<String> ids) {
        hmacUserService.refreshCaches(Sets.newHashSet(ids));
    }
}
