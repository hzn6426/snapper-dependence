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

import com.baomibing.authority.dto.SysTenantRoleDto;
import com.baomibing.authority.service.SysTenantPositionRoleService;
import com.baomibing.authority.service.SysTenantRoleService;
import com.baomibing.authority.service.SysTenantUserRoleService;
import com.baomibing.authority.vo.TenantBatchVo;
import com.baomibing.core.common.SearchResult;
import com.baomibing.web.base.MBaseController;
import com.baomibing.web.common.R;
import com.baomibing.tool.common.PageQuery;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping(path = "/eapi/role", consumes = {"application/json", "application/x-www-form-urlencoded"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class TenantRoleController extends MBaseController<SysTenantRoleDto> {

	@Autowired private SysTenantRoleService roleService;
	@Autowired private SysTenantUserRoleService userRoleService;
	@Autowired private SysTenantPositionRoleService positionRoleService;
	
	/**
	 * 根据分页条件查询角色列表
	 * 
	 * @param pageQuery 分页查询条件
	 * @return
	 */
	@PostMapping("/search")
	public R<SysTenantRoleDto> search(@RequestBody PageQuery<SysTenantRoleDto> pageQuery) {
		SearchResult<SysTenantRoleDto> result = roleService.search(pageQuery.getDto(), pageQuery.getPageNo(), pageQuery.getPageSize());
		return R.build(result.getTotalSize(), pageQuery.getPageNo(), pageQuery.getPageSize(), result.getDataList());
	}
	
	/**
	 * 新增角色
	 * 
	 * @param role 角色对象
	 */
	@PostMapping()
	public void save(@RequestBody @Valid SysTenantRoleDto role) {
		roleService.doSaveIt(role);
	}
	
	/**
     * 根据ID获取角色信息
     *
     * @param id 角色ID
     * @return
     */
    @GetMapping("/{id}")
    public SysTenantRoleDto getRole(@PathVariable("id") String id) {
        return roleService.getIt(id);
    }
	
	/**
	 * 更新角色对象
	 * 
	 * @param role 角色
	 */
	@PutMapping
	public void update(@RequestBody @Valid SysTenantRoleDto role) {
		roleService.doUpdateIt(role);
	}
	
	/**
	 * 删除角色列表
	 * 
	 * @param vo 角色ID列表
	 */
	@DeleteMapping
	public void delete(@RequestBody TenantBatchVo vo) {
		roleService.deleteRoles(Sets.newHashSet(vo.getIds()), vo.getTenantId());
	}
	
	/**
	 * 角色启用
	 * 
	 * @param ids 角色ID列表
	 */
	@PostMapping("use")
	public void use(@RequestBody List<String> ids) {
		roleService.use(Sets.newHashSet(ids));
	}
	
	/**
	 * 角色停用 
	 * 
	 * @param ids 角色ID列表
	 */
	@PostMapping("stop")
	public void stop(@RequestBody List<String> ids) {
		roleService.stop(Sets.newHashSet(ids));
	}
	
	/**
	 * 根据用户ID获取角色列表
	 * 
	 * @param uid 用户ID
	 * @return
	 */
	@GetMapping("/listByUser")
    public List<SysTenantRoleDto> listByUser(@RequestParam String tid, @RequestParam String orgId, @RequestParam String uid) {
        return userRoleService.listRolesByGroupAndUser(tid, orgId, uid);
    }
	
	/**
	 * 获取所有角色列表
	 * 
	 * @return
	 */
	@GetMapping("listAll")
	public List<SysTenantRoleDto> listAllRoles(@RequestParam String tid) {
		return roleService.listAllRoles(tid);
	}

	/**
	 * 根据职位获取角色列表
	 * 
	 * @param pid
	 * @return
	 */
	@GetMapping("/listByPosition")
	public List<SysTenantRoleDto> listByPosition(@RequestParam String tid, @RequestParam String pid) {
		return positionRoleService.listRolesByPosition(tid, pid);
	}
}
