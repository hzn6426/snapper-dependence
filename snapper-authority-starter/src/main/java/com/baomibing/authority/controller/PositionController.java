
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


import com.baomibing.authority.dto.PositionDto;
import com.baomibing.authority.service.SysPositionRoleService;
import com.baomibing.authority.service.SysPositionService;
import com.baomibing.authority.vo.PositionRoleVo;
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

/**
 * 职位管理
 *
 * @author : zening 
 * @since : 1.0.0
 */

@RestController
@RequestMapping(path = "/api/position", consumes = {"application/json", "application/x-www-form-urlencoded"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class PositionController extends MBaseController<PositionDto> {

    @Autowired private SysPositionService positionService;
	@Autowired private SysPositionRoleService positionRoleService;

    /**
     * 根据条件查询职位列表
     * 
     * @param pageQuery 分页查询条件
     * @return
     */
    @ULog("职位查询")
    @PostMapping("search")
    public R<PositionDto> search(@RequestBody PageQuery<PositionDto> pageQuery) {
    	SearchResult<PositionDto> result = positionService.search(pageQuery.getDto(), pageQuery.getPageNo(), pageQuery.getPageSize());
    	return R.build(result.getTotalSize(), pageQuery.getPageNo(), pageQuery.getPageSize(), result.getDataList());
    }

    /**
     * 组织职位列表查询
     *
     * @param groupId
     * @Return: com.baomibing.core.common.R<com.baomibing.authority.dto.PositionDto>
     */
    @GetMapping("/listByGroup")
    public List<PositionDto> listPositionByGroup(@RequestParam String groupId){
        return positionService.listPositionByGroup(groupId);
    }


    /**
     * 组织生效职位列表查询
     *
     * @param groupId
     * @Return: com.baomibing.core.common.R<com.baomibing.authority.dto.PositionDto>
     */
    @GetMapping("/listActiveByGroup")
    public List<PositionDto>  listActivePositionByGroup(@RequestParam String groupId){
        return positionService.listActivePositionByGroup(groupId);

    }

    /**
     * 职位新增
     *
     * @param positionDto
     * @Return: void
     */
    @ULog("职位新增")
    @PostMapping
    public void savePosition(@Validated @RequestBody PositionDto positionDto){
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
    public void updatePosition(@Validated @RequestBody PositionDto positionDto){
        positionService.updatePosition(positionDto);
    }

    /**
     * 职位明细
     *
     * @param id
     * @Return: com.baomibing.authority.dto.PositionDto
     */
    @GetMapping("/{id}")
    public PositionDto getPosition(@PathVariable String id){
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
     * @param ids 待删除的ID列表
     */
    @ULog("职位删除")
    @DeleteMapping
    public void deletes(@RequestBody List<String> ids) {
    	positionService.deletePositions(Sets.newHashSet(ids));
    }

	/**
	 * 根据职位ID获取职位委托ID列表（用户和组织委托）
	 * 
	 * @param pid
	 * @return
	 */
	@GetMapping("/listEntrusIdsByPosition")
	public List<String> listEntrustIdsByPosition(@RequestParam String pid) {
        return positionService.listEntrustIdsByPosition(pid);
	}

	/**
	 * 保存职位角色
	 * 
	 * @param positionRole 职位角色VO
	 */
    @ULog("职位角色保存")
	@PostMapping("/savePositionRole")
	public void savePositionRole(@RequestBody PositionRoleVo positionRole) {
		positionRoleService.savePositionRoles(positionRole.getPositionId(), Sets.newHashSet(positionRole.getRoleIds()));
	}


}
