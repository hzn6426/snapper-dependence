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


import com.baomibing.authority.dto.SysTenantGroupDto;
import com.baomibing.authority.dto.SysTenantPositionDto;
import com.baomibing.authority.dto.SysTenantUserDto;
import com.baomibing.authority.service.SysTenantPositionGroupEntrustService;
import com.baomibing.authority.service.SysTenantPositionRoleService;
import com.baomibing.authority.service.SysTenantPositionService;
import com.baomibing.authority.service.SysTenantPositionUserEntrustService;
import com.baomibing.authority.vo.TenantBatchVo;
import com.baomibing.authority.vo.TenantPositionRoleVo;
import com.baomibing.core.common.Assert;
import com.baomibing.core.common.SearchResult;
import com.baomibing.tool.common.PageQuery;
import com.baomibing.web.annotation.ULog;
import com.baomibing.web.base.MBaseController;
import com.baomibing.web.common.R;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 职位管理
 *
 * @Author: yangmignchang 2021/4/28 11:20
 * @version: 1.0.0
 */

@RestController
@RequestMapping(path = "/api/tposition", consumes = {"application/json", "application/x-www-form-urlencoded"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class TenantAdminPositionController extends MBaseController<SysTenantPositionDto> {

    @Autowired private SysTenantPositionService positionService;
	@Autowired private SysTenantPositionUserEntrustService positionUserEntrustService;
	@Autowired private SysTenantPositionGroupEntrustService positionGroupEntrustService;
	@Autowired private SysTenantPositionRoleService positionRoleService;

    /**
     * 根据条件查询职位列表
     * 
     * @param pageQuery 分页查询条件
     * @return
     */
    @PostMapping("search")
    public R<SysTenantPositionDto> search(@RequestBody PageQuery<SysTenantPositionDto> pageQuery) {
    	SearchResult<SysTenantPositionDto> result = positionService.search(pageQuery.getDto(), pageQuery.getPageNo(), pageQuery.getPageSize());
    	return R.build(result.getTotalSize(), pageQuery.getPageNo(), pageQuery.getPageSize(), result.getDataList());
    }

    /**
     * 组织职位列表查询
     *
     * @param groupId
     * @Return: com.baomibing.core.common.R<com.baomibing.authority.dto.PositionDto>
     */
    @GetMapping("/listByGroup")
    public List<SysTenantPositionDto> listPositionByGroup(@RequestParam String tid, @RequestParam String groupId){
        return positionService.listPositionByGroup(tid, groupId);
    }


    /**
     * 组织生效职位列表查询
     *
     * @param groupId
     * @Return: com.baomibing.core.common.R<com.baomibing.authority.dto.PositionDto>
     */
    @GetMapping("/listActiveByGroup")
    public List<SysTenantPositionDto>  listActivePositionByGroup(@RequestParam String tid, @RequestParam String groupId){
        return positionService.listActivePositionByGroup(tid, groupId);

    }

    /**
     * 职位新增
     *
     * @param positionDto
     * @Return: void
     */
    @ULog("职位新增")
    @PostMapping
    public void savePosition(@Validated @RequestBody SysTenantPositionDto positionDto){
        positionService.savePosition(positionDto);
    }

    /**
     * 职位修改
     *
     * @param positionDto
     * @Return: void
     */
    @ULog("职位修改")
    @PutMapping
    public void updatePosition(@Validated @RequestBody SysTenantPositionDto positionDto){
        positionService.updatePosition(positionDto);
    }

    /**
     * 职位明细
     *
     * @param id
     * @Return: com.baomibing.authority.dto.PositionDto
     */
    @GetMapping("/{id}")
    public SysTenantPositionDto getPosition(@PathVariable String id){
        return positionService.getPosition(id);
    }

    /**
     * 职位启用
     *
     * @param ids
     * @Return: void
     */
    @ULog("职位启用")
    @PostMapping("/use")
    public void use(@RequestBody List<String> ids){
        Assert.CheckArgument(ids);
        positionService.doUsePosition(ids);
    }

    /**
     * 职位停用
     *
     * @param ids
     * @Return: void
     */
    @ULog("职位停用")
    @PostMapping("/stop")
    public void stop(@RequestBody List<String> ids){
        Assert.CheckArgument(ids);
        positionService.doStopPosition(ids);
    }
    
    /**
     * 批量删除职位
     * 
     * @param vo
     */
    @DeleteMapping
    public void deletes(@RequestBody TenantBatchVo vo) {
    	positionService.deletePositions(Sets.newHashSet(vo.getIds()), vo.getTenantId());
    }

	/**
	 * 根据职位ID获取职位委托ID列表（用户和组织委托）
	 * 
	 * @param pid
	 * @return
	 */
	@GetMapping("/listEntrusIdsByPosition")
	public List<String> listEntrustIdsByPosition(@RequestParam String tid, @RequestParam String pid) {
		List<SysTenantUserDto> entrustUsers = positionUserEntrustService.listEntrustUsersByPosition(tid, pid);
		// 用于渲染树，ID是唯一的，可能一个用户位于两个组织中，此处需要连接父组织ID
		List<String> entrustUids = entrustUsers.stream().map(u -> u.getGroupId() + '#' + u.getId())
				.collect(Collectors.toList());
		List<SysTenantGroupDto> entrustGroups = positionGroupEntrustService.listEntrustGroupsByPosition(tid, pid);
		List<String> entrustGids = entrustGroups.stream().map(SysTenantGroupDto::getId).collect(Collectors.toList());
		entrustUids.addAll(entrustGids);
		return entrustUids;
	}

	/**
	 * 保存职位角色
	 * 
	 * @param positionRole 职位角色VO
	 */
	@PostMapping("/savePositionRole")
	public void savePositionRole(@RequestBody TenantPositionRoleVo positionRole) {
		positionRoleService.savePositionRoles(positionRole.getTenantId(), positionRole.getPositionId(), Sets.newHashSet(positionRole.getRoleIds()));
	}


}
