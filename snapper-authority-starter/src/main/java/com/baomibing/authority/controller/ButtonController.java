/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
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
