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

import com.baomibing.authority.dto.BusinessActionDto;
import com.baomibing.authority.service.SysBusinessActionService;
import com.baomibing.core.common.SearchResult;
import com.baomibing.tool.common.PageQuery;
import com.baomibing.web.annotation.ULog;
import com.baomibing.web.base.MBaseController;
import com.baomibing.web.common.R;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * BusinessActionController
 *
 * @author zening (316279828@qq.com) 2025/6/26 10:27
 * @version 1.0.0
 **/
@RequestMapping(path = "/api/action", consumes = {"application/json", "application/x-www-form-urlencoded"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class BusinessActionController extends MBaseController<BusinessActionDto> {

    @Autowired private SysBusinessActionService businessActionService;

    /**
     * 根据分页条件查询Action列表
     *
     * @param pageQuery 分页查询条件
     * @return
     */
    @PostMapping("/search")
    public R<BusinessActionDto> search(@RequestBody PageQuery<BusinessActionDto> pageQuery) {
        SearchResult<BusinessActionDto> result = businessActionService.search(pageQuery.getDto(), pageQuery.getPageNo(), pageQuery.getPageSize());
        return R.build(result.getTotalSize(), pageQuery.getPageNo(), pageQuery.getPageSize(), result.getDataList());
    }

    /**
     * 新增Action
     *
     * @param action 权限ACTION对象
     */
    @ULog("权限Action新增")
    @PostMapping()
    public void saveAction(@RequestBody @Valid BusinessActionDto action) {
        businessActionService.saveAction(action);
    }

    /**
     * 根据ID获取Action信息
     *
     * @param id action ID
     * @return
     */
    @ULog("Action明细")
    @GetMapping("/{id}")
    public BusinessActionDto getAction(@PathVariable("id") String id) {
        return businessActionService.getIt(id);
    }

    /**
     * 更新Action对象
     *
     * @param action 权限Action
     */
    @ULog("Action更新")
    @PutMapping
    public void update(@RequestBody @Valid BusinessActionDto action) {
        businessActionService.updateAction(action);
    }

    /**
     * 删除Action列表
     *
     * @param ids Action ID列表
     */
    @ULog("Action删除")
    @DeleteMapping
    public void delete(@RequestBody List<String> ids) {
        businessActionService.deleteActions(Sets.newHashSet(ids));
    }

    /**
     * Action 启用
     *
     * @param ids ActionID列表
     */
    @ULog("Action启用")
    @PostMapping("use")
    public void use(@RequestBody List<String> ids) {
        businessActionService.use(Sets.newHashSet(ids));
    }

    /**
     * Action停用 
     *
     * @param ids ActionID列表
     */
    @ULog("Action停用")
    @PostMapping("stop")
    public void stop(@RequestBody List<String> ids) {
        businessActionService.stop(Sets.newHashSet(ids));
    }


    @ULog("Action刷新权限")
    @PostMapping("refreshActionCache")
    public void refreshActionCache(@RequestBody List<String> ids) {
        businessActionService.refreshTheActionsCache(Sets.newHashSet(ids));
    }
}
