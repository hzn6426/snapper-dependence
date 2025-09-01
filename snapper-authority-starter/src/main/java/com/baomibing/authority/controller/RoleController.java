/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.controller;

import com.baomibing.authority.dto.RoleDto;
import com.baomibing.authority.service.SysPositionRoleService;
import com.baomibing.authority.service.SysRoleResourceService;
import com.baomibing.authority.service.SysRoleService;
import com.baomibing.authority.service.SysUserRoleService;
import com.baomibing.core.common.SearchResult;
import com.baomibing.tool.common.PageQuery;
import com.baomibing.web.annotation.ULog;
import com.baomibing.web.base.MBaseController;
import com.baomibing.web.common.R;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping(path = "/api/role", consumes = {"application/json", "application/x-www-form-urlencoded"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class RoleController extends MBaseController<RoleDto> {

	@Autowired private SysRoleService roleService;
	@Autowired private SysUserRoleService userRoleService;
	@Autowired private SysPositionRoleService positionRoleService;
	@Autowired private SysRoleResourceService roleResourceService;
	
	/**
	 * 根据分页条件查询角色列表
	 * 
	 * @param pageQuery 分页查询条件
	 * @return
	 */
	@ULog("角色查询")
	@PostMapping("/search")
	public R<RoleDto> search(@RequestBody PageQuery<RoleDto> pageQuery) {
		SearchResult<RoleDto> result = roleService.search(pageQuery.getDto(), pageQuery.getPageNo(), pageQuery.getPageSize());
		return R.build(result.getTotalSize(), pageQuery.getPageNo(), pageQuery.getPageSize(), result.getDataList());
	}
	
	/**
	 * 新增角色
	 * 
	 * @param role 角色对象
	 */
	@ULog("角色新增")
	@PostMapping()
	public void save(@RequestBody @Valid RoleDto role) {
		roleService.saveRole(role);
	}
	
	/**
     * 根据ID获取角色信息
     *
     * @param id 角色ID
     * @return
     */
    @GetMapping("/{id}")
    public RoleDto getRole(@PathVariable("id") String id) {
        return roleService.getIt(id);
    }
	
	/**
	 * 更新角色对象
	 * 
	 * @param role 角色
	 */
	@ULog("角色更新")
	@PutMapping
	public void update(@RequestBody @Valid RoleDto role) {
		roleService.updateRole(role);
	}
	
	/**
	 * 删除角色列表
	 * 
	 * @param ids 角色ID列表
	 */
	@ULog("角色删除")
	@DeleteMapping
	public void delete(@RequestBody List<String> ids) {
		roleService.deleteRoles(Sets.newHashSet(ids));
	}
	
	/**
	 * 角色启用
	 * 
	 * @param ids 角色ID列表
	 */
	@ULog("角色启用")
	@PostMapping("use")
	public void use(@RequestBody List<String> ids) {
		roleService.use(Sets.newHashSet(ids));
	}
	
	/**
	 * 角色停用 
	 * 
	 * @param ids 角色ID列表
	 */
	@ULog("角色停用")
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
    public List<RoleDto> listByUser(@RequestParam String orgId, @RequestParam String uid) {
        return userRoleService.listRolesByGroupAndUser(orgId, uid);
    }
	
	/**
	 * 获取所有角色列表
	 * 
	 * @return
	 */
	@GetMapping("listAll")
	public List<RoleDto> listAllRoles() {
		return roleService.listAllRoles();
	}

	/**
	 * 根据职位获取角色列表
	 * 
	 * @param pid
	 * @return
	 */
	@GetMapping("/listByPosition")
	public List<RoleDto> listByPosition(@RequestParam String pid) {
		return positionRoleService.listRolesByPosition(pid);
	}

	/**
	 * 刷新权限
	 */
	@ULog("角色刷新权限")
	@PostMapping("refreshPrivileges")
	public void refreshPrivileges() {
		roleResourceService.refreshPrivileges();
	}
}
