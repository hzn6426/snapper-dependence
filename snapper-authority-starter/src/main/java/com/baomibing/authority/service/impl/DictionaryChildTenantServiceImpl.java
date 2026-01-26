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
import com.baomibing.authority.dto.DictionaryChildTenantDto;
import com.baomibing.authority.entity.SysDictChildTenant;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.mapper.SysDictChildTenantMapper;
import com.baomibing.authority.service.DictionaryChildTenantService;
import com.baomibing.cache.CacheService;
import com.baomibing.cache.redis.RedisService;
import com.baomibing.core.common.Assert;
import com.baomibing.core.common.SearchResult;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.core.process.StateWorkFlow;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.constant.RedisKeyConstant;
import com.baomibing.tool.constant.TenantRedisKeyConstant;
import com.baomibing.tool.util.Checker;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.baomibing.tool.constant.NumberConstant.MAX_IN_BATCH_SIZE;

/**
 * 字典项
 */
@Service
public class DictionaryChildTenantServiceImpl extends MBaseServiceImpl<SysDictChildTenantMapper, SysDictChildTenant, DictionaryChildTenantDto>
        implements DictionaryChildTenantService {

    @Transactional
    @Override
    public void deleteDictChildsByParentIds(List<String> ids) {
        List<String> childIds = Lists.newLinkedList();
        List<DictionaryChildTenantDto> dictionaryChildDtos = listByParentIds(ids);
        dictionaryChildDtos.forEach(d -> childIds.add(d.getId()));
        deleteDictChilds(childIds);
    }

    @Override
    @Transactional
    public void saveDictChild(DictionaryChildTenantDto dictionaryDto) {
        assertDictChildNotRepeat(dictionaryDto);
        StateWorkFlow.doInitState(dictionaryDto);
        super.saveIt(dictionaryDto);
        cacheService.set(TenantRedisKeyConstant.KEY_DICT_CHILD_CODE + dictionaryDto.getDictCode(), JSONObject.toJSONString(dictionaryDto),
                TenantRedisKeyConstant.REDIS_TIME_IN_SECONDS);
    }

    @Override
    @Transactional
    public void updateDictChild(DictionaryChildTenantDto dictionaryDto) {
        assertDictChildNotRepeat(dictionaryDto);
        super.updateIt(dictionaryDto);
        cacheService.set(TenantRedisKeyConstant.KEY_DICT_CHILD_CODE + dictionaryDto.getDictCode(),
                JSONObject.toJSONString(dictionaryDto),
                TenantRedisKeyConstant.REDIS_TIME_IN_SECONDS);
    }

    @Override
    public SearchResult<DictionaryChildTenantDto> searchDictChild(DictionaryChildTenantDto dto, int pageNo, int pageSize) {
        if (Checker.beEmpty(dto.getParentId())) {
            return new SearchResult<>();
        }
        LambdaQueryWrapper<SysDictChildTenant> queryWrapper = lambdaQuery();
        queryWrapper.like(Checker.beNotEmpty(dto.getDictName()), SysDictChildTenant::getDictName, dto.getDictName());
        queryWrapper.eq(Checker.beNotEmpty(dto.getDictCode()), SysDictChildTenant::getDictCode, dto.getDictCode());
        queryWrapper.eq(SysDictChildTenant::getBeDelete, Boolean.FALSE).eq(SysDictChildTenant::getParentId, dto.getParentId());
        queryWrapper.orderByAsc(SysDictChildTenant::getPriority);
        return search(queryWrapper, pageNo, pageSize);
    }

    @Override
    @Transactional
    public void deleteDictChilds(List<String> ids) {
        Assert.CheckArgument(ids);
        super.baseMapper.updateDictChildDeleteByIds(Sets.newHashSet(ids));
        // 拼接 key
        List<String> stringList = ids.stream().filter(Checker::beNotEmpty).map(s -> TenantRedisKeyConstant.KEY_DICT_CHILD_CODE + s)
                .collect(Collectors.toList());
        // redis删除字典项
        cacheService.del(stringList);
    }

    @Override
    @Transactional
    public void useDictChilds(List<String> ids) {
        List<DictionaryChildTenantDto> dictionaryChildDtos = listByIds(ids);
        dictionaryChildDtos.forEach(d -> {
            StateWorkFlow.doProcess(d, DictionaryAction.USE);
            super.updateIt(d);
            cacheService.set(TenantRedisKeyConstant.KEY_DICT_CHILD_CODE + d.getDictCode(), JSONObject.toJSONString(d), TenantRedisKeyConstant.REDIS_TIME_IN_SECONDS);
        });
    }

    @Override
    @Transactional
    public void stopDictChilds(List<String> ids) {
        List<DictionaryChildTenantDto> dictionaryChildDtos = listByIds(ids);
        dictionaryChildDtos.forEach(d -> {
            StateWorkFlow.doProcess(d, DictionaryAction.STOP);
            super.updateIt(d);
            cacheService.set(TenantRedisKeyConstant.KEY_DICT_CHILD_CODE + d.getDictCode(), JSONObject.toJSONString(d), TenantRedisKeyConstant.REDIS_TIME_IN_SECONDS);
        });
    }

    @Override
    @Transactional
    public void useDictChildsByParentIds(List<String> ids) {
        List<DictionaryChildTenantDto> dictionaryChildDtos = listByParentIds(ids);
        dictionaryChildDtos.forEach(d -> {
            StateWorkFlow.doProcess(d, DictionaryAction.USE);
            super.updateIt(d);
            cacheService.set(TenantRedisKeyConstant.KEY_DICT_CHILD_CODE + d.getDictCode(), JSONObject.toJSONString(d), TenantRedisKeyConstant.REDIS_TIME_IN_SECONDS);
        });
    }

    @Override
    @Transactional
    public void stopDictChildsByParentIds(List<String> ids) {
        List<DictionaryChildTenantDto> dictionaryChildDtos = listByParentIds(ids);
        dictionaryChildDtos.forEach(d -> {
            StateWorkFlow.doProcess(d, DictionaryAction.STOP);
            super.updateIt(d);
            cacheService.set(TenantRedisKeyConstant.KEY_DICT_CHILD_CODE + d.getDictCode(), JSONObject.toJSONString(d), TenantRedisKeyConstant.REDIS_TIME_IN_SECONDS);
        });
    }

    /**
     * 功能描述: 通过字典项id数组获取字典项
     *
     * @param ids
     * @Return: java.util.List<com.baomibing.authority.dto.DictionaryChildDto>
     */
    private List<DictionaryChildTenantDto> listByIds(List<String> ids) {
        Assert.CheckBeGreaterOrEqualThan(MAX_IN_BATCH_SIZE, ids.size(), ExceptionEnum.EXCEED_SQL_MAX_IN_SIZE,
                ids.size());
        LambdaQueryWrapper<SysDictChildTenant> queryWrapper = lambdaQuery();
        queryWrapper.in(SysDictChildTenant::getId, Sets.newHashSet(ids));
        return mapper(super.baseMapper.selectList(queryWrapper));
    }

    /**
     * 功能描述: 通过字典id数组获取字典项
     *
     * @param parentIds
     * @Return: java.util.List<com.baomibing.authority.dto.DictionaryChildDto>
     */
    private List<DictionaryChildTenantDto> listByParentIds(List<String> parentIds) {
        LambdaQueryWrapper<SysDictChildTenant> queryWrapper = lambdaQuery();
        queryWrapper.in(SysDictChildTenant::getParentId, Sets.newHashSet(parentIds));
        return mapper(super.baseMapper.selectList(queryWrapper));
    }

    /**
     * 功能描述: 判断字典项目编号是否存在
     *
     * @param dto
     * @Return: void
     */
    private void assertDictChildNotRepeat(DictionaryChildTenantDto dto) {
        LambdaQueryWrapper<SysDictChildTenant> queryWrapper = lambdaQuery();
        Assert.CheckNotEmpty(dto.getDictCode(), dto);
        queryWrapper.eq(Checker.beNotEmpty(dto.getId()), SysDictChildTenant::getId, dto.getId());
        queryWrapper.eq(SysDictChildTenant::getDictCode, dto.getDictCode()).eq(SysDictChildTenant::getBeDelete, false)
                .select(SysDictChildTenant::getId);
        List<SysDictChildTenant> sysDictChilds = super.baseMapper.selectList(queryWrapper);
        boolean empty = sysDictChilds.isEmpty();
        // id不为空 查询为空 则不允许修改
        if (empty && Checker.beNotEmpty(dto.getId())) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.DICT_CHILD_CODE_NOT_MODIFY, dto.getDictCode());
        }
        // id为空 查询不为空 则不允许新增
        if (!empty && Checker.beEmpty(dto.getId())) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.DICT_CODE_NOT_BE_REPEAT, dto.getDictCode());
        }
    }

    @Override
    public List<DictionaryChildTenantDto> listChildByParentCode(String parentCode) {
        if (Checker.beEmpty(parentCode))
            return Lists.newArrayList();
        return mapper(this.baseMapper.listByParentCode(parentCode));
    }

    @Override
    public List<DictionaryChildTenantDto> listChildByParentCodes(List<String> pcodes) {
        if (Checker.beEmpty(pcodes))
            return Lists.newArrayList();
        return mapper(this.baseMapper.listByParentCodes(Sets.newHashSet(pcodes)));
    }

    @Override
    public DictionaryChildTenantDto getByCode(String code) {
        if (Checker.beEmpty(code))
            return null;
        return mapper2v(this.baseMapper.selectOne(
                lambdaQuery().eq(SysDictChildTenant::getDictCode, code).eq(SysDictChildTenant::getBeDelete, Boolean.FALSE)));
    }

}
