
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

import com.baomibing.authority.constant.ParamConst;
import com.baomibing.authority.dto.ParamDto;
import com.baomibing.authority.entity.SysParam;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.mapper.SysParamMapper;
import com.baomibing.authority.service.SysParamService;
import com.baomibing.core.common.Assert;
import com.baomibing.core.common.SearchResult;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.constant.RedisKeyConstant;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.util.Checker;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.baomibing.tool.constant.RedisKeyConstant.KEY_RULE_APPEND_SCRIPT;

/**
 * 参数管理
 * 
 * @author zening
 * @since 1.0.0
 */
@Service
public class SysParamServiceImpl extends MBaseServiceImpl<SysParamMapper, SysParam, ParamDto>
		implements SysParamService {


    @Override
	public void saveParam(ParamDto dto) {
        assertParamRepeat(dto);
        super.saveIt(dto);
        cacheRuleGroovyScript(dto);
    }

    private void cacheRuleGroovyScript(ParamDto dto) {
        if (ParamConst.GROOVY_APPEND_SCRIPT.equals(dto.getParamCode())) {
            cacheService.set(KEY_RULE_APPEND_SCRIPT, dto.getParamValue());
        }
    }

    private void removeCacheRuleGroovyScript(ParamDto dto) {
        if (ParamConst.GROOVY_APPEND_SCRIPT.equals(dto.getParamCode())) {
            cacheService.del(KEY_RULE_APPEND_SCRIPT);
        }
    }

    @Override
	public void updateParam(ParamDto dto) {
        assertParamRepeat(dto);
        ParamDto p = super.getIt(dto.getId());
        assertBeLock(p);
        super.updateIt(dto);
        cacheRuleGroovyScript(dto);
    }

    @Override
	public SearchResult<ParamDto> searchParam(ParamDto dto, int pageNo, int pageSize) {
        LambdaQueryWrapper<SysParam> queryWrapper = lambdaQuery();
        Assert.CheckArgument(dto);
		queryWrapper.like(Checker.beNotEmpty(dto.getParamName()), SysParam::getParamName, dto.getParamName());
		queryWrapper.like(Checker.beNotEmpty(dto.getParamCode()), SysParam::getParamCode, dto.getParamCode());
        queryWrapper.eq(SysParam::getBeDelete, Strings.ZERO);
        return search(queryWrapper, pageNo, pageSize);
    }

    @Transactional
    @Override
    public void deleteParams(List<String> ids) {
        Assert.CheckArgument(ids);
		List<ParamDto> dtos = Lists.newLinkedList();
        ids.forEach(s -> {
			ParamDto param = new ParamDto();
			param.setId(s).setBeDelete(Boolean.TRUE);
			dtos.add(param);
        });
        List<ParamDto> params = super.gets(Sets.newHashSet(ids));
        for (ParamDto p : params) {
            assertBeLock(p);
            removeCacheRuleGroovyScript(p);
        }
        super.updateItBatch(dtos);
        //redis删除系统参数
		List<String> stringList = ids.stream().filter(s -> Checker.beNotEmpty(s)).map(s -> RedisKeyConstant.KEY_PARAM_ID + s).collect(Collectors.toList());
        cacheService.del(stringList);
    }

    /**
     * 功能描述: 判断参数编号是否存在
     *
     * @param dto
     * @Return: void
     */
	private void assertParamRepeat(ParamDto dto) {
        LambdaQueryWrapper<SysParam> queryWrapper = lambdaQuery();
        Assert.CheckNotEmpty(dto.getParamCode(), dto);
		queryWrapper.eq(Checker.beNotEmpty(dto.getId()), SysParam::getId, dto.getId());
        queryWrapper.eq(SysParam::getParamCode, dto.getParamCode()).select(SysParam::getId);
        List<SysParam> sysParams = super.baseMapper.selectList(queryWrapper);
        boolean empty = sysParams.isEmpty();
        //id不为空 查询为空 则不允许修改
		if (empty && Checker.beNotEmpty(dto.getId())) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.PARAM_CODE_NOT_MODIFY, dto.getParamCode());
        }
        //id为空 查询不为空 则不允许新增
		if (!empty && Checker.beEmpty(dto.getId())) {
            throw new ServerRuntimeException(AuthorizationExceptionEnum.PARAM_CODE_NOT_BE_REPEAT, dto.getParamCode());
        }
    }

    @Override
	public List<ParamDto> listByCodes(List<String> codes) {
		if (Checker.beEmpty(codes)) {
            return Lists.newArrayList();
        }
        return mapper(this.baseMapper.selectList(lambdaQuery().in(SysParam::getParamCode, codes)));
    }


}
