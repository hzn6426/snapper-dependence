/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
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
