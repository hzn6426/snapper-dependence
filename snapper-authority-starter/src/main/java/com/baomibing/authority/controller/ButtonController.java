
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

import com.baomibing.authority.dto.ButtonDto;
import com.baomibing.authority.service.SysButtonService;
import com.baomibing.tool.common.PageQuery;
import com.baomibing.web.annotation.ULog;
import com.baomibing.web.base.MBaseController;
import com.baomibing.web.common.R;
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
@RequestMapping(path = {"/api/button", "/fapi/button"}, consumes = {"application/json", "application/x-www-form-urlencoded"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class ButtonController extends MBaseController<ButtonDto> {

    @Autowired private SysButtonService buttonService;

    @PostMapping("searchButtonsAndApiByMenu")
    public R<ButtonDto> searchButtonsAndApiByMenu(@RequestBody PageQuery<ButtonDto> pager) {
        return R.build(buttonService.searchButtonsAndApiByMenu(pager.getDto(), pager.getPageNo(), pager.getPageSize()));
    }

    @ULog("按钮新增或更新")
    @PostMapping("saveOrUpdate")
    public void saveOrUpdateButton(@RequestBody ButtonDto button) {
        buttonService.saveOrUpdateButton(button);
    }


    @GetMapping("listByKeyWord")
    public List<ButtonDto> listByKeyWord(@RequestParam String keyWord) {
        return buttonService.listByKeyWord(keyWord);
    }

    @GetMapping("getButtonAndApiById")
    public ButtonDto getButtonAndApiById(@RequestParam String id) {
        return buttonService.getButtonAndApiById(id);
    }

    /**
     * 删除按钮
     *
     * @param ids 带删除的按钮列表
     */
    @ULog("按钮删除")
    @DeleteMapping
    public void deleteButton(@RequestBody List<String> ids) {
        buttonService.deleteButtons(Sets.newHashSet(ids));
    }
}
