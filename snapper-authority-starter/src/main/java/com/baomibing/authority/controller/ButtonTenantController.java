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

import com.baomibing.authority.dto.ButtonTenantDto;
import com.baomibing.authority.service.SysButtonTenantService;
import com.baomibing.web.base.MBaseController;
import com.baomibing.web.common.R;
import com.baomibing.tool.common.PageQuery;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 按钮管理
 *
 * @author zening 2022/3/3 16:54
 * @version 1.0.0
 */
@RestController
@RequestMapping(path = {"/eapi/button","/api/tbutton"}, consumes = {"application/json", "application/x-www-form-urlencoded"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class ButtonTenantController extends MBaseController<ButtonTenantDto> {

    @Autowired private SysButtonTenantService buttonService;

    @PostMapping("searchButtonsAndApiByMenu")
    public R<ButtonTenantDto> searchButtonsAndApiByMenu(@RequestBody PageQuery<ButtonTenantDto> pager) {
        return R.build(buttonService.searchButtonsAndApiByMenu(pager.getDto(), pager.getPageNo(), pager.getPageSize()));
    }

    @GetMapping("listByKeyWord")
    public List<ButtonTenantDto> listByKeyWord(@RequestParam String keyWord) {
        return buttonService.listByKeyWord(keyWord);
    }

    @GetMapping("getButtonAndApiById")
    public ButtonTenantDto getButtonAndApiById(@RequestParam String id) {
        return buttonService.getButtonAndApiById(id);
    }

    @PostMapping("saveOrUpdate")
    public void saveOrUpdateButton(@RequestBody ButtonTenantDto button) {
        buttonService.saveOrUpdateButton(button);
    }


    /**
     * 删除按钮
     *
     * @param ids 带删除的按钮列表
     */
    @DeleteMapping
    public void deleteButton(@RequestBody List<String> ids) {
        buttonService.deleteButtons(Sets.newHashSet(ids));
    }
}
