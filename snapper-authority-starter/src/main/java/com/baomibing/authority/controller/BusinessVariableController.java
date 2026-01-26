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

import com.baomibing.authority.dto.BusinessVariableDto;
import com.baomibing.authority.service.BusinessVariableService;
import com.baomibing.authority.wrap.VariableWrap;
import com.baomibing.core.common.SearchResult;
import com.baomibing.tool.common.PageQuery;
import com.baomibing.web.annotation.ULog;
import com.baomibing.web.common.R;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = { "/api/businessVariable" }, consumes = { "application/json",  "application/x-www-form-urlencoded" }, produces = MediaType.APPLICATION_JSON_VALUE)
public class BusinessVariableController {
    
    @Autowired private BusinessVariableService businessVariableService;

    /**
     * 业务变量保存
     *
     * @param dto
     * @Return: void
     */
    @ULog("业务变量保存")
    @PostMapping
    public void saveVariable(@RequestBody @Valid BusinessVariableDto dto) {
        businessVariableService.saveVariable(dto);
    }

    /**
     * 业务变量更新
     *
     * @param dto
     * @Return: void
     */
    @ULog("业务变量更新")
    @PutMapping
    public void updateVariable(@RequestBody @Valid BusinessVariableDto dto) {
        businessVariableService.updateVariable(dto);
    }

    /**
     * 业务变量查询
     *
     * @param query
     * @Return: com.buta.core.common.R<com.buta.authority.dto.dto>
     */
    @PostMapping("search")
    public R<BusinessVariableDto> search(@RequestBody PageQuery<BusinessVariableDto> query) {
        SearchResult<BusinessVariableDto> result = businessVariableService.searchVariable(query.getDto(), query.getPageNo(),
                query.getPageSize());
        return R.build(result);
    }

    /**
     * 批量删除业务变量
     *
     * @param ids 业务变量id数组
     * @Return: void
     */
    @ULog("批量删除业务变量")
    @DeleteMapping
    public void deleteVariables(@RequestBody List<String> ids) {
        businessVariableService.deleteVariables(Sets.newHashSet(ids));
    }

    @GetMapping("listByCodeOrName")
    List<BusinessVariableDto> listByCodeOrName(@RequestParam String codeOrName) {
        return businessVariableService.listByCodeOrName(codeOrName);
    }

    @GetMapping("getAndWrapByCode")
    public VariableWrap getAndWrapByCode(@RequestParam String code) {
        return businessVariableService.getAndWrapByCode(code);
    }

    @GetMapping("listConstantVariable")
    public List<VariableWrap> listConstantVariable() {
        return businessVariableService.listConstantVariable();
    }

    /**
     * 变量明细
     *
     * @param id
     * @Return: com.buta.authority.dto.BusinessVariableDto
     */
    @GetMapping("/{id}")
    public BusinessVariableDto getVariable(@PathVariable String id){
        return businessVariableService.getVariable(id);
    }

    @GetMapping("getByCode")
    public BusinessVariableDto getByCode(@RequestParam String code) {
        return businessVariableService.getByCode(code);
    }


}
