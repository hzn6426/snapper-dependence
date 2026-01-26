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

package com.baomibing.authority.service.impl;

import com.baomibing.authority.constant.enums.VariableTypeEnum;
import com.baomibing.authority.dto.BusinessVariableDto;
import com.baomibing.authority.entity.SysBusinessVariable;
import com.baomibing.authority.mapper.SysBusinessVariableMapper;
import com.baomibing.authority.service.BusinessVariableService;
import com.baomibing.authority.wrap.ParamWrap;
import com.baomibing.authority.wrap.VariableWrap;
import com.baomibing.core.common.SearchResult;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * BusinessVariableServiceImpl
 *
 * @author zening (316279828@qq.com)
 * @version 1.0.0
 **/
@Service
public class BusinessVariableServiceImpl extends MBaseServiceImpl<SysBusinessVariableMapper, SysBusinessVariable, BusinessVariableDto> implements BusinessVariableService {

    @Override
    public List<BusinessVariableDto> listByCodeOrName(String codeOrName) {
        if (Checker.beEmpty(codeOrName)) {
            return Lists.newArrayList();
        }
        return mapper(baseMapper.listByCodeOrName(codeOrName));
    }

    @Override
    public VariableWrap getAndWrapByCode(String code) {
        if (Checker.beEmpty(code)) {
            return null;
        }
        List<String> parentList = baseMapper.listAllParentByCode(code);
        Set<String> allParents = parentList.stream().flatMap(t -> Splitter.on(",").splitToStream(t))
                .filter(Checker::beNotEmpty).collect(Collectors.toSet());
        List<BusinessVariableDto> variables = super.gets(allParents);

        List<VariableWrap> wraps = variables.stream()
                .map(variable -> new VariableWrap().setId(variable.getId()).setParentId(variable.getParentId()).setLabel(variable.getName()).setValue(variable.getCode()))
                .collect(Collectors.toList());

        Map<String, VariableWrap> allMenuMap = wraps.stream()
                .collect(Collectors.toMap(VariableWrap::getId, Function.identity(), (key1, key2) -> key2));
        List<VariableWrap> tlist = Lists.newArrayList();

        wraps.forEach(variable -> {
            String pid = variable.getParentId();
            if (Checker.beNotEmpty(pid)) {
                VariableWrap pvariable = allMenuMap.get(pid);
                if (Checker.beNotNull(pvariable)) {
                    if (Checker.beEmpty(pvariable.getChildren())) {
                        pvariable.setChildren(Lists.newArrayList());
                    }
                    pvariable.getChildren().add(variable);
                } else {
                    tlist.add(variable);
                }
            } else {
                tlist.add(variable);
            }
        });

        return tlist.get(0);

    }

    @Override
    public void saveVariable(BusinessVariableDto businessVariableDto) {
        super.saveIt(businessVariableDto);
    }

    @Override
    public void updateVariable(BusinessVariableDto businessVariableDto) {
        BusinessVariableDto variable = super.getIt(businessVariableDto.getId());
        assertBeLock(variable);
        super.updateIt(businessVariableDto);
    }

    @Override
    public void deleteVariables(Set<String> ids) {
        List<BusinessVariableDto> variables = gets(ids);
        for (BusinessVariableDto variable : variables) {
            assertBeLock(variable);
        }
        super.deletes(ids);
    }

    @Override
    public SearchResult<BusinessVariableDto> searchVariable(BusinessVariableDto dto, int pageNo, int pageSize) {
        SearchResult<BusinessVariableDto> result = super.search(lambdaQuery().like(Checker.beNotEmpty(dto.getCode()), SysBusinessVariable::getCode, dto.getCode())
                .like(Checker.beNotEmpty(dto.getName()), SysBusinessVariable::getName, dto.getName()), pageNo, pageSize);
//        List<String> ids = result.getDataList().stream().map(v -> v.getId()).collect(Collectors.toList());
//        List<String> allParents = baseMapper.listAllParentByIds(Sets.newHashSet(ids));
//        Set<String> allParentSet = allParents.stream().flatMap(t -> Splitter.on(",").splitToStream(t))
//                .filter(Checker::BeNotEmpty).collect(Collectors.toSet());
//        List<BusinessVariableDto> parents = super.gets(allParentSet);
//
//        parents.addAll(result.getDataList());
//        Map<String, BusinessVariableDto> allMenuMap = parents.stream()
//                .collect(Collectors.toMap(BusinessVariableDto::getId, Function.identity(), (key1, key2) -> key2));
//
//        List<BusinessVariableDto> tlist = Lists.newArrayList();
//        parents.forEach(variable -> {
//            String pid = variable.getParentId();
//            if (Checker.BeNotEmpty(pid)) {
//                BusinessVariableDto pvariable = allMenuMap.get(pid);
//                if (Checker.BeNotNull(pvariable)) {
//                    if (Checker.BeEmpty(pvariable.getChildren())) {
//                        pvariable.setChildren(Lists.newArrayList());
//                    }
//                    pvariable.getChildren().add(variable);
//                } else {
//                    tlist.add(variable);
//                }
//            } else {
//                tlist.add(variable);
//            }
//        });
//        result.setDataList(tlist);
        return result;
    }

    @Override
    public BusinessVariableDto getVariable(String id) {
        return super.getIt(id);
    }

    @Override
    public BusinessVariableDto getByCode(String code) {
        if (Checker.beEmpty(code)) {
            return null;
        }
        return mapper2v(baseMapper.selectOne(lambdaQuery().eq(SysBusinessVariable::getCode, code)));
    }

    @Override
    public List<VariableWrap> listConstantVariable() {
        List<VariableWrap> wraps = Lists.newArrayList();
        VariableWrap userWrap = new VariableWrap().setId("user").setLabel("用户").setValue("user").setType(VariableTypeEnum.CONSTANT.name()).setChildren(Lists.newArrayList());
        userWrap.getChildren().add(new VariableWrap().setLabel("用户ID").setValue("id"));
        userWrap.getChildren().add(new VariableWrap().setLabel("用户名").setValue("userName"));
        userWrap.getChildren().add(new VariableWrap().setLabel("中文名").setValue("userCnName"));
        userWrap.getChildren().add(new VariableWrap().setLabel("英文名").setValue("userEnName"));
        userWrap.getChildren().add(new VariableWrap().setLabel("部门ID").setValue("currentGroupId"));
        userWrap.getChildren().add(new VariableWrap().setLabel("部门名称").setValue("currentGroupName"));
        userWrap.getChildren().add(new VariableWrap().setLabel("公司ID").setValue("companyId"));
        userWrap.getChildren().add(new VariableWrap().setLabel("公司名称").setValue("companyName"));
        userWrap.getChildren().add(new VariableWrap().setLabel("用户标识").setValue("userTag"));
        userWrap.getChildren().add(new VariableWrap().setLabel("系统标识").setValue("systemTag"));
        wraps.add(userWrap);

        VariableWrap functionWrap = new VariableWrap().setId("function").setLabel("常用").setValue("function").setType(VariableTypeEnum.FUNC.name()).setChildren(Lists.newArrayList());
        functionWrap.getChildren().add(new VariableWrap().setLabel("连接").setValue("concat").setParameters(Lists.newArrayList(new ParamWrap().setName("字符串1").setType("String"), new ParamWrap().setName("字符串2").setType("String"))));
        functionWrap.getChildren().add(new VariableWrap().setLabel("加法").setValue("add").setParameters(Lists.newArrayList(new ParamWrap().setName("数字1").setType("BigDecimal"), new ParamWrap().setName("数字2").setType("BigDecimal"))));
        functionWrap.getChildren().add(new VariableWrap().setLabel("减法").setValue("subtract").setParameters(Lists.newArrayList(new ParamWrap().setName("数字1").setType("BigDecimal"), new ParamWrap().setName("数字2").setType("BigDecimal"))));
        functionWrap.getChildren().add(new VariableWrap().setLabel("乘法").setValue("multiply").setParameters(Lists.newArrayList(new ParamWrap().setName("数字1").setType("BigDecimal"), new ParamWrap().setName("数字2").setType("BigDecimal"))));
        functionWrap.getChildren().add(new VariableWrap().setLabel("除法").setValue("divide").setParameters(Lists.newArrayList(new ParamWrap().setName("数字1").setType("BigDecimal"), new ParamWrap().setName("数字2").setType("BigDecimal"))));
        functionWrap.getChildren().add(new VariableWrap().setLabel("取余").setValue("mod").setParameters(Lists.newArrayList(new ParamWrap().setName("数字1").setType("BigDecimal"), new ParamWrap().setName("数字2").setType("BigDecimal"))));
        wraps.add(functionWrap);
        return wraps;
    }
}
