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

import com.baomibing.authority.action.DictionaryAction;
import com.baomibing.authority.dto.DictionaryTenantDto;
import com.baomibing.authority.entity.SysDictionaryTenant;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.mapper.SysDictionaryTenantMapper;
import com.baomibing.authority.service.DictionaryChildService;
import com.baomibing.authority.service.DictionaryTenantService;
import com.baomibing.cache.CacheService;
import com.baomibing.core.common.Assert;
import com.baomibing.core.common.SearchResult;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.core.process.StateWorkFlow;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.constant.RedisKeyConstant;
import com.baomibing.tool.constant.TenantRedisKeyConstant;
import com.baomibing.tool.util.Checker;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典
 *
 */
@Service
public class DictionaryTenantServiceImpl extends MBaseServiceImpl<SysDictionaryTenantMapper, SysDictionaryTenant, DictionaryTenantDto> implements DictionaryTenantService {

    @Autowired private DictionaryChildService dictionaryChildService;


    @Override
    @Transactional
    public void saveDictionary(DictionaryTenantDto dictionaryDto) {
        assertDictNotRepeat(dictionaryDto);
        StateWorkFlow.doInitState(dictionaryDto);
        super.saveIt(dictionaryDto);
        cacheService.set(TenantRedisKeyConstant.KEY_DICT_CODE + dictionaryDto.getDictCode(), JSONObject.toJSONString(dictionaryDto), RedisKeyConstant.REDIS_TIME_IN_SECONDS);

    }

    @Override
    @Transactional
    public void updateDictionary(DictionaryTenantDto dictionaryDto) {
        assertDictNotRepeat(dictionaryDto);
        super.updateIt(dictionaryDto);
        cacheService.set(TenantRedisKeyConstant.KEY_DICT_CODE + dictionaryDto.getDictCode(), JSONObject.toJSONString(dictionaryDto), RedisKeyConstant.REDIS_TIME_IN_SECONDS);

    }

    @Override
    public SearchResult<DictionaryTenantDto> searchDictionary(DictionaryTenantDto dto, int pageNo, int pageSize) {
        LambdaQueryWrapper<SysDictionaryTenant> queryWrapper = lambdaQuery();
        if (Checker.beNotNull(dto)) {
            queryWrapper.like(Checker.beNotEmpty(dto.getDictCode()), SysDictionaryTenant::getDictCode, dto.getDictCode());
            queryWrapper.like(Checker.beNotEmpty(dto.getDictName()), SysDictionaryTenant::getDictName, dto.getDictName());
            queryWrapper.eq(Checker.beNotNull(dto.getState()), SysDictionaryTenant::getState, dto.getState());
            queryWrapper.eq(SysDictionaryTenant::getBeDelete, Boolean.FALSE);
            queryWrapper.orderByAsc(SysDictionaryTenant::getDictCode);
        }
        return search(queryWrapper, pageNo, pageSize);
    }

    @Override
    @Transactional
    public void deleteDicts(List<String> ids) {
        Assert.CheckArgument(ids);
        //更新子表
        dictionaryChildService.deleteDictChildsByParentIds(ids);
        //更新主表
        super.baseMapper.updateDictDeleteByIds(Sets.newHashSet(ids));
        List<DictionaryTenantDto> dictionaryDtos = listByIds(ids);
        //拼接 key
        List<String> stringList = dictionaryDtos.stream().filter(d -> Checker.beNotNull(d)).map(d -> TenantRedisKeyConstant.KEY_DICT_CODE + d.getDictCode()).collect(Collectors.toList());
        //redis删除字典项
        cacheService.del(stringList);
    }

    @Override
    @Transactional
    public void useDicts(List<String> ids) {
        List<DictionaryTenantDto> dictionaryDtos = listByIds(ids);
        dictionaryDtos.forEach(d -> {
                    StateWorkFlow.doProcess(d, DictionaryAction.USE);
                    super.updateIt(d);
            cacheService.set(TenantRedisKeyConstant.KEY_DICT_CODE + d.getDictCode(), JSONObject.toJSONString(d), RedisKeyConstant.REDIS_TIME_IN_SECONDS);
                }
        );
        //批量修改字典项
        dictionaryChildService.useDictChildsByParentIds(ids);
    }

    @Override
    @Transactional
    public void stopDicts(List<String> ids) {
        List<DictionaryTenantDto> dictionaryDtos = listByIds(ids);
        dictionaryDtos.forEach(d -> {
                    StateWorkFlow.doProcess(d, DictionaryAction.STOP);
                    super.updateIt(d);
            cacheService.set(TenantRedisKeyConstant.KEY_DICT_CODE + d.getDictCode(), JSONObject.toJSONString(d), RedisKeyConstant.REDIS_TIME_IN_SECONDS);
                }
        );
        //批量修改字典项
        dictionaryChildService.stopDictChildsByParentIds(ids);
    }

    /**
     * 功能描述: 通过ids获取字典
     *
     * @param ids
     * @Return: java.util.List<com.baomibing.authority.dto.DictionaryDto>
     */
    private List<DictionaryTenantDto> listByIds(List<String> ids) {
        LambdaQueryWrapper<SysDictionaryTenant> queryWrapper = lambdaQuery();
        queryWrapper.in(SysDictionaryTenant::getId, Sets.newHashSet(ids));
        return mapper(super.baseMapper.selectList(queryWrapper));
    }

    /**
     * 功能描述: 判断字典编号是否存在
     *
     * @param dto
     * @Return: void
     */
    private void assertDictNotRepeat(DictionaryTenantDto dto) {
        LambdaQueryWrapper<SysDictionaryTenant> queryWrapper = lambdaQuery();
        Assert.CheckNotEmpty(dto.getDictCode(), dto);
        queryWrapper.eq(Checker.beNotEmpty(dto.getId()), SysDictionaryTenant::getId, dto.getId());
        queryWrapper.eq(SysDictionaryTenant::getDictCode, dto.getDictCode()).eq(SysDictionaryTenant::getBeDelete, false)
                .select(SysDictionaryTenant::getId);
        List<SysDictionaryTenant> sysDictionaries = super.baseMapper.selectList(queryWrapper);
        boolean empty = sysDictionaries.isEmpty();
        //id不为空 查询为空 则不允许修改
        if (empty && Checker.beNotEmpty(dto.getId())) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.DICT_CODE_NOT_MODIFY, dto.getDictCode());
        }
        //id为空 查询不为空 则不允许新增
        if (!empty && Checker.beEmpty(dto.getId())) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.DICT_CODE_NOT_BE_REPEAT, dto.getDictCode());
        }
    }
}
