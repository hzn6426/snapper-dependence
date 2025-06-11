
package com.baomibing.authority.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomibing.authority.action.DictionaryAction;
import com.baomibing.authority.dto.DictionaryDto;
import com.baomibing.authority.entity.SysDictionary;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.mapper.SysDictionaryMapper;
import com.baomibing.authority.service.DictionaryChildService;
import com.baomibing.authority.service.DictionaryService;
import com.baomibing.core.common.Assert;
import com.baomibing.core.common.SearchResult;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.core.process.StateWorkFlow;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.constant.RedisKeyConstant;
import com.baomibing.tool.util.Checker;
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
 * @author zening
 * @since 1.0.0
 */
@Service
public class DictionaryServiceImpl extends MBaseServiceImpl<SysDictionaryMapper, SysDictionary, DictionaryDto>
		implements DictionaryService {

	@Autowired private DictionaryChildService dictionaryChildService;

	@Override
	@Transactional
	public void saveDictionary(DictionaryDto dictionaryDto) {
		assertDictNotRepeat(dictionaryDto);
		StateWorkFlow.doInitState(dictionaryDto);
		super.saveIt(dictionaryDto);
		cacheService.set(RedisKeyConstant.KEY_DICT_CODE + dictionaryDto.getDictCode(),
				JSONObject.toJSONString(dictionaryDto), RedisKeyConstant.REDIS_TIME_IN_SECONDS);

	}

	@Override
	@Transactional
	public void updateDictionary(DictionaryDto dictionaryDto) {
		assertDictNotRepeat(dictionaryDto);
		DictionaryDto dbDict = super.getIt(dictionaryDto.getId());
		assertBeLock(dbDict);
		super.updateIt(dictionaryDto);
		cacheService.set(RedisKeyConstant.KEY_DICT_CODE + dictionaryDto.getDictCode(),
				JSONObject.toJSONString(dictionaryDto), RedisKeyConstant.REDIS_TIME_IN_SECONDS);

	}

	@Override
	public SearchResult<DictionaryDto> searchDictionary(DictionaryDto dto, int pageNo, int pageSize) {
		LambdaQueryWrapper<SysDictionary> queryWrapper = lambdaQuery();
		if (Checker.beNotNull(dto)) {
			queryWrapper.like(Checker.beNotEmpty(dto.getDictCode()), SysDictionary::getDictCode, dto.getDictCode());
			queryWrapper.like(Checker.beNotEmpty(dto.getDictName()), SysDictionary::getDictName, dto.getDictName());
			queryWrapper.eq(Checker.beNotNull(dto.getState()), SysDictionary::getState, dto.getState());
			queryWrapper.eq(SysDictionary::getBeDelete, Boolean.FALSE);
			queryWrapper.orderByAsc(SysDictionary::getDictCode, SysDictionary::getDictType);
		}
		return search(queryWrapper, pageNo, pageSize);
	}

	@Override
	@Transactional
	public void deleteDicts(List<String> ids) {
		Assert.CheckArgument(ids);
		List<DictionaryDto> dicts = super.gets(emptySet(ids));
		for (DictionaryDto dict : dicts) {
			assertBeLock(dict);
		}
		// 更新子表
		dictionaryChildService.deleteDictChildsByParentIds(ids);
		// 更新主表
        super.baseMapper.updateDictDeleteByIds(Sets.newHashSet(ids));
		List<DictionaryDto> dictionaryDtos = listByIds(ids);
		// 拼接 key
		List<String> stringList = dictionaryDtos.stream().filter(Checker::beNotNull)
				.map(d -> RedisKeyConstant.KEY_DICT_CODE + d.getDictCode()).collect(Collectors.toList());
		// redis删除字典项
		cacheService.del(stringList);
	}

	@Override
	@Transactional
	public void useDicts(List<String> ids) {
		List<DictionaryDto> dictionaryDtos = listByIds(ids);
		for(DictionaryDto d : dictionaryDtos) {
			assertBeLock(d);
			StateWorkFlow.doProcess(d, DictionaryAction.USE);
			super.updateIt(d);
			cacheService.set(RedisKeyConstant.KEY_DICT_CODE + d.getDictCode(), JSONObject.toJSONString(d),
					RedisKeyConstant.REDIS_TIME_IN_SECONDS);
		};
		// 批量修改字典项
		dictionaryChildService.useDictChildsByParentIds(ids);
	}

	@Override
	@Transactional
	public void stopDicts(List<String> ids) {
		List<DictionaryDto> dictionaryDtos = listByIds(ids);
		for(DictionaryDto d : dictionaryDtos) {
			assertBeLock(d);
			StateWorkFlow.doProcess(d, DictionaryAction.STOP);
			super.updateIt(d);
			cacheService.set(RedisKeyConstant.KEY_DICT_CODE + d.getDictCode(), JSONObject.toJSONString(d),
					RedisKeyConstant.REDIS_TIME_IN_SECONDS);
		};
		// 批量修改字典项
		dictionaryChildService.stopDictChildsByParentIds(ids);
	}

	/**
	 * 功能描述: 通过ids获取字典
	 *
	 * @param ids
	 * @Return: java.util.List<com.baomibing.authority.dto.DictionaryDto>
	 */
	private List<DictionaryDto> listByIds(List<String> ids) {
		LambdaQueryWrapper<SysDictionary> queryWrapper = lambdaQuery();
		queryWrapper.in(SysDictionary::getId, Sets.newHashSet(ids));
		return mapper(super.baseMapper.selectList(queryWrapper));
	}

	/**
	 * 功能描述: 判断字典编号是否存在
	 *
	 * @param dto
	 * @Return: void
	 */
	private void assertDictNotRepeat(DictionaryDto dto) {
		LambdaQueryWrapper<SysDictionary> queryWrapper = lambdaQuery();
		Assert.CheckNotEmpty(dto.getDictCode(), dto);
		queryWrapper.eq(Checker.beNotEmpty(dto.getId()), SysDictionary::getId, dto.getId());
		queryWrapper.eq(SysDictionary::getDictCode, dto.getDictCode()).eq(SysDictionary::getBeDelete, false)
				.select(SysDictionary::getId);
		List<SysDictionary> sysDictionaries = super.baseMapper.selectList(queryWrapper);
		boolean empty = sysDictionaries.isEmpty();
		// id不为空 查询为空 则不允许修改
		if (empty && Checker.beNotEmpty(dto.getId())) {
			throw new ServerRuntimeException(AuthorizationExceptionEnum.DICT_CODE_NOT_MODIFY, dto.getDictCode());
		}
		// id为空 查询不为空 则不允许新增
		if (!empty && Checker.beEmpty(dto.getId())) {
			throw new ServerRuntimeException(AuthorizationExceptionEnum.DICT_CODE_NOT_BE_REPEAT, dto.getDictCode());
		}
	}

}
