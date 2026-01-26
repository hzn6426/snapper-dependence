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

package com.baomibing.authority.service;


import com.baomibing.authority.dto.DictionaryChildTenantDto;
import com.baomibing.core.base.MBaseService;
import com.baomibing.core.common.SearchResult;

import java.util.List;

/**
 * @Author: zhenyang 2021/3/8 15:20
 * @version: 1.0.0
 */
public interface DictionaryChildTenantService extends MBaseService<DictionaryChildTenantDto> {

    /**
     * 通过字典表id删除字典项
     *
     * @param ids 字典表id数组
     * @Return: void
     */
    void deleteDictChildsByParentIds(List<String> ids);

    /**
     * 字典项保存
     *
     * @param dictionaryDto
     * @Return: void
     */
    void saveDictChild(DictionaryChildTenantDto dictionaryDto);

    /**
     * 字典项更新
     *
     * @param dictionaryDto
     * @Return: void
     */
    void updateDictChild(DictionaryChildTenantDto dictionaryDto);

    /**
     * 字典项更搜索Dict
     *
     * @param dto      com.baomibing.authority.dto.DictionaryChildDto
     * @param pageNo
     * @param pageSize
     * @Return: com.baomibing.core.common.SearchResult<com.baomibing.authority.dto.DictionaryChildDto>
     */
    SearchResult<DictionaryChildTenantDto> searchDictChild(DictionaryChildTenantDto dto, int pageNo, int pageSize);

    /**
     * 批量删除字典项
     *
     * @param ids 字典项id数组
     * @Return: void
     */
    void deleteDictChilds(List<String> ids);

    /**
     * 根据父编码获取子项列表
     *
     * @param parentCode 父编码
     * @return java.util.List<com.baomibing.authority.dto.DictionaryChildDto>
     */
    List<DictionaryChildTenantDto> listChildByParentCode(String parentCode);

    /**
     * 根据父编码列表获取子项列表
     *
     * @param pcodes 父编码列表
     * @return
     */
    List<DictionaryChildTenantDto> listChildByParentCodes(List<String> pcodes);

    /**
     * 根据编码信息获取子项列表
     *
     * @param code 编码
     * @return com.baomibing.authority.dto.DictionaryChildDto
     */
    DictionaryChildTenantDto getByCode(String code);

    /**
     * 通过字典项id数组批量使用字典项
     *
     * @param ids 字典项id数组
     * @Return: void
     */
    void useDictChilds(List<String> ids);

    /**
     * 通过字典项id数组批量停用字典项
     *
     * @param ids 字典项id数组
     * @Return: void
     */
    void stopDictChilds(List<String> ids);

    /**
     * 通过字典id数组批量使用字典项
     *
     * @param ids 字典id数组
     * @Return: void
     */
    void useDictChildsByParentIds(List<String> ids);

    /**
     * 通过字典id数组批量停用字典项
     *
     * @param ids 字典表id数组
     * @Return: void
     */
    void stopDictChildsByParentIds(List<String> ids);

}
