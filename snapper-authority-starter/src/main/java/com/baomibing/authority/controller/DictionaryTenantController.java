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

import com.baomibing.authority.dto.DictionaryTenantDto;
import com.baomibing.authority.service.DictionaryTenantService;
import com.baomibing.core.common.SearchResult;
import com.baomibing.web.annotation.ULog;
import com.baomibing.web.common.R;
import com.baomibing.tool.common.PageQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 字典管理
 *
 * @Author: zhenyang 2021/3/8 15:06
 * @version: 1.0.0
 */
@RestController
@RequestMapping(path = "/eapi/dictionary", consumes = {"application/json", "application/x-www-form-urlencoded"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class DictionaryTenantController {

    @Autowired
    private DictionaryTenantService dictionaryService;

    /**
     * 字典保存
     *
     * @param dictionaryDto
     * @Return: void
     */
    @ULog("字典保存")
    @PostMapping
    public void saveDictionary(@RequestBody @Valid DictionaryTenantDto dictionaryDto) {
        dictionaryService.saveDictionary(dictionaryDto);
    }

    /**
     * 字典更新
     *
     * @param dictionaryDto
     * @Return: void
     */
    @ULog("字典更新")
    @PutMapping
    public void updateDictionary(@RequestBody @Valid DictionaryTenantDto dictionaryDto) {
        dictionaryService.updateDictionary(dictionaryDto);
    }

    /**
     * 字典查询
     *
     * @param query
     * @Return: com.baomibing.core.common.R<com.baomibing.authority.dto.DictionaryDto>
     */
    @PostMapping("search")
    public R<DictionaryTenantDto> search(@RequestBody PageQuery<DictionaryTenantDto> query) {
        SearchResult<DictionaryTenantDto> result = dictionaryService.searchDictionary(query.getDto(), query.getPageNo(), query.getPageSize());
        return R.build(result);
    }

    /**
     * 批量删除字典
     *
     * @param ids 字典id数组
     * @Return: void
     */
    @ULog("批量删除字典")
    @DeleteMapping
    public void deleteDicts(@RequestBody List<String> ids) {
        dictionaryService.deleteDicts(ids);
    }

    /**
     * 批量启用字典
     *
     * @param ids
     * @Return: void
     */
    @ULog("批量启用字典")
    @PostMapping("use")
    public void useDicts(@RequestBody List<String> ids) {
        dictionaryService.useDicts(ids);
    }

    /**
     * 批量禁用字典
     *
     * @param ids
     * @Return: void
     */
    @ULog("批量禁用字典")
    @PostMapping("stop")
    public void stopDicts(@RequestBody List<String> ids) {
        dictionaryService.stopDicts(ids);
    }


}
