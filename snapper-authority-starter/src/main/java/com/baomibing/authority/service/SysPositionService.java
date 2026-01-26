
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


import com.baomibing.authority.dto.PositionDto;
import com.baomibing.core.base.MBaseService;
import com.baomibing.core.common.SearchResult;

import java.util.List;
import java.util.Set;

public interface SysPositionService extends MBaseService<PositionDto> {

	/**
	 * 根据条件分页查询职位列表
	 * 
	 * @param v          条件封装
	 * @param pageNumber 页号
	 * @param pageSize   每页数量
	 * @return
	 */
	SearchResult<PositionDto> search(PositionDto v, int pageNumber, int pageSize);

	/**
	 * 组织职位列表查询
	 *
	 * @param groupId
	 * @Return: com.baomibing.core.common.SearchResult<com.baomibing.authority.dto.PositionDto>
	 */
	List<PositionDto> listPositionByGroup(String groupId);

	/**
	 * 组织生效职位列表查询
	 *
	 * @param groupId
	 * @Return: com.baomibing.core.common.SearchResult<com.baomibing.authority.dto.PositionDto>
	 */
	List<PositionDto> listActivePositionByGroup(String groupId);

	/**
	 * 职位新增
	 *
	 * @param positionDto
	 * @Return: void
	 */
	void savePosition(PositionDto positionDto);

	/**
	 * 职位修改
	 *
	 * @param positionDto
	 * @Return: void
	 */
	void updatePosition(PositionDto positionDto);

	/**
	 * 职位明细
	 *
	 * @param id
	 * @Return: com.baomibing.authority.dto.PositionDto
	 */
	PositionDto getPosition(String id);

	/**
	 * 职位启用
	 *
	 * @param ids
	 * @Return: void
	 */
	void doUsePosition(List<String> ids);

	/**
	 * 职位停用
	 *
	 * @param ids
	 * @Return: void
	 */
	void doStopPosition(List<String> ids);

	/**
	 * 删除组织下的职位
	 *
	 * @param gids
	 * @Return: void
	 */
	void deleteByGroups(Set<String> gids);

	/**
	 * 根据职位ID列表删除对应的职位
	 * 
	 * @param pids 职位ID
	 */
	void deletePositions(Set<String> pids);

	/**
	 * 获取所有组织中的职位
	 *
	 * @return
	 */
	List<PositionDto> listAllGroupPositions();

	/**
	 * 根据职位ID获取职位的委托信息
	 * @param positionId 职位ID
	 * @return
	 */
	List<String> listEntrustIdsByPosition(String positionId);
}
