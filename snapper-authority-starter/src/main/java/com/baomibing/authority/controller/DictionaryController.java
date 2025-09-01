/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.controller;

import com.baomibing.authority.dto.DictionaryDto;
import com.baomibing.authority.service.DictionaryService;
import com.baomibing.core.common.SearchResult;
import com.baomibing.tool.common.PageQuery;
import com.baomibing.web.annotation.ULog;
import com.baomibing.web.common.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典管理
 *
 * @author : zening
 * @since : 1.0.0
 */
@RestController
@RequestMapping(path = "/api/dictionary", consumes = {"application/json", "application/x-www-form-urlencoded"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class DictionaryController {

    @Autowired
    private DictionaryService dictionaryService;

    /**
     * 字典保存
     *
     * @param dictionaryDto
     * @Return: void
     */
    @ULog("字典保存")
    @PostMapping
    public void saveDictionary(@RequestBody  DictionaryDto dictionaryDto) {
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
    public void updateDictionary(@RequestBody  DictionaryDto dictionaryDto) {
        dictionaryService.updateDictionary(dictionaryDto);
    }

    /**
     * 字典查询
     *
     * @param query
     * @Return: com.baomibing.core.common.R<com.baomibing.authority.dto.DictionaryDto>
     */
    @ULog("字典查询")
    @PostMapping("search")
    public R<DictionaryDto> search(@RequestBody PageQuery<DictionaryDto> query) {
        SearchResult<DictionaryDto> result = dictionaryService.searchDictionary(query.getDto(), query.getPageNo(), query.getPageSize());
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


    @GetMapping("/{id}")
    public DictionaryDto getDict(@PathVariable("id") String id) {
        return dictionaryService.getIt(id);
    }
}
