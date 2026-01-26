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


import com.baomibing.authority.dto.BusinessVariableDto;
import com.baomibing.authority.wrap.VariableWrap;
import com.baomibing.core.common.SearchResult;

import java.util.List;
import java.util.Set;

public interface BusinessVariableService {

    /**
     * 根据编码或名称获取变量
     * @param codeOrName
     * @return
     */
    List<BusinessVariableDto> listByCodeOrName(String codeOrName);

    /**
     * 根据编码获取变量并封装其对应的所有子级
     * @param code
     * @return
     */
    VariableWrap getAndWrapByCode(String code);


    void saveVariable(BusinessVariableDto businessVariableDto);

    void updateVariable(BusinessVariableDto businessVariableDto);

    void deleteVariables(Set<String> ids);

    /**
     * 变量查询
     *
     * @param dto
     * @param pageNo
     * @param pageSize
     * @Return: com.buta.core.common.SearchResult<com.buta.authority.dto.BusinessVariableDto>
     */
    SearchResult<BusinessVariableDto> searchVariable(BusinessVariableDto dto, int pageNo, int pageSize);

    BusinessVariableDto getVariable(String id);

    BusinessVariableDto getByCode(String code);

    List<VariableWrap> listConstantVariable();




}
